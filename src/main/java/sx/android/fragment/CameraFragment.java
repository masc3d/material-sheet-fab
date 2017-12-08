package sx.android.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import sx.android.Camera;
import sx.android.Function;
import sx.android.R;
import sx.android.event.FragmentEventDispatcher;
import sx.android.view.AsyncImageView;
import sx.android.view.CameraView;
import sx.android.view.CircleButton;
import sx.event.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by masc on 26.11.14.
 */
public class CameraFragment extends Fragment {
    /**
     * Event listener interface
     */
    public interface Listener extends EventListener {
        void onCameraFragmentPictureTaken(byte[] data);
        void onCameraFragmentShutter();
        void onCameraFragmentDiscarded();
    }

    int mCameraMaxZoom;
    /**
     * Event dispatcher
     */
    FragmentEventDispatcher<Listener> mEventDispatcher;
    int mControlsColor;
    int mControlsColorDarkened;

    // Views
    View mRootView;
    CameraView mCameraView;
    ViewGroup mOverlayViewContainer;
    ViewSwitcher mMainViewSwitcher;

    // Camera controls
    SeekBar mZoomSeekBar;
    CircleButton mCameraButton;

    // Confirmation controls
    AsyncImageView mPictureImageView;
    CircleButton mOkButton;
    CircleButton mCancelButton;

    // Image data and bitmap for current picture
    byte[] mImageData;
    Bitmap mPicture;

    // Async tasks
    AsyncTask<Void, Void, Void> mCameraInitializeTask;
    AsyncTask<Void, Void, Void> mCameraStartPreviewTask;
    AsyncTask<Void, Void, Void> mCameraTakePictureTask;

    /**
     * Shows camera preview and controls
     */
    private void showCamera() {
        mImageData = null;
        mPicture = null;
        mPictureImageView.setImageDrawable(null);
        mMainViewSwitcher.setDisplayedChild(0);
        mCameraButton.setInnerColor(mControlsColor);
        mCameraButton.setEnabled(true);

        if (mCameraView.hasSurface()) {
            mCameraStartPreviewTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Camera.getInstance().getHardwareCamera().startPreview();
                    return null;
                }
            }.execute();
        }
    }

    /**
     * Show confirmation for current image
     */
    private void showConfirmation() {
        final byte[] imageData = mImageData;
        mPictureImageView.load(new Function<Void, Bitmap>() {
            @Override
            public Bitmap apply(Void input) {
                mPicture = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                return mPicture;
            }
        });
        mMainViewSwitcher.setDisplayedChild(1);
    }

    /**
     * Set controls base color
     *
     * @param color
     */
    public void setControlsBaseColor(int color) {
        mControlsColor = color;

        // Darkened controls color
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.5f;
        mControlsColorDarkened = Color.HSVToColor(hsv);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventDispatcher = new FragmentEventDispatcher(Listener.class, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sx_fragment_camera, container, false);

        mPictureImageView = (AsyncImageView) mRootView.findViewById(R.id.pictureImageView);
        if (mPicture != null) {
            mPictureImageView.setImageBitmap(mPicture);
        }
        mOkButton = (CircleButton) mRootView.findViewById(R.id.okButton);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventDispatcher.emit(new Function1<Listener, Unit>() {
                    @Override
                    public Unit invoke(Listener listener) {
                        listener.onCameraFragmentPictureTaken(mImageData);
                        return Unit.INSTANCE;
                    }
                });
                showCamera();
            }
        });
        mCancelButton = (CircleButton) mRootView.findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventDispatcher.emit(new Function1<Listener, Unit>() {
                    @Override
                    public Unit invoke(Listener listener) {
                        listener.onCameraFragmentDiscarded();
                        return Unit.INSTANCE;
                    }
                });
                showCamera();
            }
        });

        mZoomSeekBar = (SeekBar) mRootView.findViewById(R.id.zoomSeekBar);
        mZoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                android.hardware.Camera camera = Camera.getInstance().getHardwareCamera();
                android.hardware.Camera.Parameters p = camera.getParameters();
                p.setZoom(progress);
                camera.setParameters(p);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCameraButton = (CircleButton) mRootView.findViewById(R.id.snapshotButton);
        mCameraButton.setColor(mControlsColor);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitForAsyncTasksToFinish();
                if (mCameraView != null && mCameraView.hasSurface()) {
                    mCameraButton.setInnerColor(mControlsColorDarkened);
                    mCameraButton.setEnabled(false);
                    mEventDispatcher.emit(new Function1<Listener, Unit>() {
                        @Override
                        public Unit invoke(Listener listener) {
                            listener.onCameraFragmentShutter();
                            return Unit.INSTANCE;
                        }
                    });

                    mCameraTakePictureTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            android.hardware.Camera camera = Camera.getInstance().getHardwareCamera();
                            camera.takePicture(
                                    new android.hardware.Camera.ShutterCallback() {
                                        @Override
                                        public void onShutter() {
                                        }
                                    },
                                    null,
                                    // JPG picture callback
                                    new android.hardware.Camera.PictureCallback() {
                                        @Override
                                        public void onPictureTaken(final byte[] data, android.hardware.Camera camera) {
                                            mImageData = data;
                                            showConfirmation();
                                        }
                                    });
                            return null;
                        }
                    }.execute(null, null);
                }
            }
        });

        mMainViewSwitcher = (ViewSwitcher) mRootView.findViewById(R.id.cameraMainViewSwitcher);
        mMainViewSwitcher.setDisplayedChild((mPicture == null) ? 0 : 1);

        // Asynchronous camera initialization
        final Activity activity = this.getActivity();

        // Camera related configuration, deferred as it blocks too long

        // Semaphore for intialize post execution (within UI thread) required to sync with async takepicture task
        // which has to wait until the initialization is complete entirely (onPostExecute)
        mCameraInitializeTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Configure camera
                android.hardware.Camera camera = Camera.getInstance().getHardwareCamera();
                android.hardware.Camera.Parameters parameters = camera.getParameters();
                // Set parameters
                parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
                mCameraMaxZoom = parameters.getMaxZoom();
                // Find resolutions above 1000 lines
                ArrayList<android.hardware.Camera.Size> resolutions = new ArrayList<>();
                for (android.hardware.Camera.Size res : parameters.getSupportedPictureSizes()) {
                    if (res.height > 1000) resolutions.add(res);
                }

                // Sort resolutions ascending
                Collections.sort(resolutions, new Comparator<android.hardware.Camera.Size>() {
                    @Override
                    public int compare(android.hardware.Camera.Size lhs, android.hardware.Camera.Size rhs) {
                        return new Integer(lhs.height).compareTo(rhs.height);
                    }
                });
                android.hardware.Camera.Size resolution = resolutions.get(0);
                parameters.setPictureSize(resolution.width, resolution.height);
                camera.setParameters(parameters);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // Camera view
                mCameraView = new CameraView(activity, new Function<Void, android.hardware.Camera>() {
                    @Override
                    public android.hardware.Camera apply(Void input) {
                        return Camera.getInstance().getHardwareCamera();
                    }
                });
                ViewGroup cameraViewContainer = (ViewGroup) mRootView.findViewById(R.id.cameraViewContainer);
                cameraViewContainer.addView(mCameraView);

                mZoomSeekBar.setMax(mCameraMaxZoom);
            }
        };
        mCameraInitializeTask.execute(null, null);

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.getActivity().getActionBar().show();
    }

    @Override
    public void onResume() {
        if (mCameraView != null && mCameraView.hasSurface()) {
            mCameraView.update();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        // masc20141210. Important to wait for all camera tasks to finish before views are destroyed.
        this.waitForAsyncTasksToFinish();

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Waits for all asynchronous (usually camera related) async task to finish up
     */
    private void waitForAsyncTasksToFinish() {
        // Wait for async tasks to finish up
        if (mCameraInitializeTask != null) {
            try {
                mCameraInitializeTask.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCameraStartPreviewTask != null) {
            try {
                mCameraStartPreviewTask.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCameraTakePictureTask != null) {
            try {
                mCameraTakePictureTask.get();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}


