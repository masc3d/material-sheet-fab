package sx.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import sx.android.Function;

/**
 * ImageView with async loading support
 * Created by masc on 01.12.14.
 */
public class AsyncImageView extends ImageView {
    private AsyncTask mLoaderTask;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Asyncronously loads image
     *
     * @param loader Loader function to use
     */
    public void load(final Function<Void, Bitmap> loader) {
        if (mLoaderTask != null)
            mLoaderTask.cancel(true);

        mLoaderTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return loader.apply(null);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                setImageBitmap(bitmap);
            }
        }.execute();
    }

    public void cancelLoad() {
        if (mLoaderTask != null)
            mLoaderTask.cancel(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.cancelLoad();
    }
}
