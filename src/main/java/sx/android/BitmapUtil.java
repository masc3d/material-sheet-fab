package sx.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by masc on 02.12.14.
 */
public class BitmapUtil {
    /**
     * Scale bitmap to square image, cropping as needed
     * @param src Source bitmap
     * @param size Square size
     * @return
     */
    public static Bitmap scaleSquare(Bitmap src, int size) {
        // Calculate thumbnail scale/crop parameters
        int newWidth = 0;
        int newHeight = 0;
        int leftOffset;
        int topOffset;
        if (src.getWidth() < src.getHeight()) {
            double ratio = (double)src.getHeight() / (double)src.getWidth();
            newWidth = size;
            newHeight = (int)(size * ratio);
            leftOffset = 0;
            topOffset = (newHeight - size) / 2;
        } else {
            double ratio = (double)src.getWidth() / (double)src.getHeight();
            newWidth = (int)(size * ratio);
            newHeight = size;
            leftOffset = (newWidth - 200) / 2;
            topOffset = 0;
        }

        Bitmap scaled = Bitmap.createScaledBitmap(src, newWidth, newHeight, false);
        return Bitmap.createBitmap(scaled, leftOffset, topOffset, size, size);
    }
}
