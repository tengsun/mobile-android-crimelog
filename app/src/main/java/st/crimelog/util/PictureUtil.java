package st.crimelog.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by tengsun on 2/24/16.
 */
public class PictureUtil {

    private static final String TAG = "PictureUtil";

    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // get the dimension of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        Log.d(TAG, "Window: " + destWidth + ", " + destHeight);
        Log.d(TAG, "Image: " + srcWidth + ", " + srcHeight);

        int inSampleSize = 1;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }

        // clean up the view's image for the sake of memeory
        BitmapDrawable bitmap = (BitmapDrawable) imageView.getDrawable();
        bitmap.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

}
