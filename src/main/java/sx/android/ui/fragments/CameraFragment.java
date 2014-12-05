package sx.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import sx.android.Camera;
import sx.android.R;
import sx.android.ui.FragmentEventDispatcher;
import sx.android.ui.views.AsyncImageView;
import sx.android.ui.views.CameraView;
import sx.android.ui.views.CircleButton;
import sx.util.EventDispatcher;
import sx.util.EventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    Function<Context, View> mOverlayViewFactory;
    int mControlsColor;
    int mControlsColorDarkened;

    // Views
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
     * Set overlay view to display
     *
     * @param vf
     */
    public void setOverlayViewFactory(Function<Context, View> vf) {
        mOverlayViewFactory = vf;
    }

    /**
     * Set controls base color
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

        // Retaining this instance in order to preserve the overlayViewFactory stored as a reference within this instance
        this.setRetainInstance(true);

        mEventDispatcher = new FragmentEventDispatcher(Listener.class, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(CameraFragment.class.getName(), "CameraFragment::onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        mOverlayViewContainer = (ViewGroup) rootView.findViewById(R.id.overlayViewContainer);
        if (mOverlayViewFactory != null)
            mOverlayViewContainer.addView(mOverlayViewFactory.apply(this.getActivity()));

        mPictureImageView = (AsyncImageView) rootView.findViewById(R.id.pictureImageView);
        if (mPicture != null) {
            mPictureImageView.setImageBitmap(mPicture);
        }
        mOkButton = (CircleButton) rootView.findViewById(R.id.okButton);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventDispatcher.emit(new EventDispatcher.Runnable<Listener>() {
                    @Override
                    public void run(Listener listener) {
                        listener.onCameraFragmentPictureTaken(mImageData);
                    }
                });
                showCamera();
            }
        });
        mCancelButton = (CircleButton) rootView.findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventDispatcher.emit(new EventDispatcher.Runnable<Listener>() {
                    @Override
                    public void run(Listener listener) {
                        listener.onCameraFragmentDiscarded();
                    }
                });
                showCamera();
            }
        });

        mZoomSeekBar = (SeekBar) rootView.findViewById(R.id.zoomSeekBar);
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

        mCameraButton = (CircleButton) rootView.findViewById(R.id.snapshotButton);
        mCameraButton.setColor(mControlsColor);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO there may be a re-entry issue here, seen very sporadic failures of takePicture.
                mCameraButton.setInnerColor(mControlsColorDarkened);
                mCameraButton.setEnabled(false);
                mEventDispatcher.emit(new EventDispatcher.Runnable<Listener>() {
                    @Override
                    public void run(Listener listener) {
                        listener.onCameraFragmentShutter();
                    }
                });

                new AsyncTask<Void, Void, Void>() {
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
        });

        mMainViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.cameraMainViewSwitcher);
        mMainViewSwitcher.setDisplayedChild((mPicture == null) ? 0 : 1);

        // Asynchronous camera initialization
        final Activity activity = this.getActivity();
        // Camera related configuration, deferred as it blocks too long
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
                List<android.hardware.Camera.Size> resolutions = Lists.newArrayList(Collections2.filter(parameters.getSupportedPictureSizes(), new Predicate<android.hardware.Camera.Size>() {
                    @Override
                    public boolean apply(android.hardware.Camera.Size input) {
                        return input.height > 1000;
                    }
                }));
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
                ViewGroup cameraViewContainer = (ViewGroup) rootView.findViewById(R.id.cameraViewContainer);
                cameraViewContainer.addView(mCameraView);

                mZoomSeekBar.setMax(mCameraMaxZoom);
            }
        };
        mCameraInitializeTask.execute(null, null);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().hide();
    }

    @Override
    public void onDetach() {
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

        super.onDetach();
        this.getActivity().getActionBar().show();
    }

    @Override
    public void onResume() {
        Log.d(CameraFragment.class.getName(), "CameraFragment::onResume");
        if (mCameraView != null && mCameraView.hasSurface()) {
            Log.d(CameraFragment.class.getName(), "CameraFragment::onResume::hasSurface");
            mCameraView.update();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDetach();
    }
}


