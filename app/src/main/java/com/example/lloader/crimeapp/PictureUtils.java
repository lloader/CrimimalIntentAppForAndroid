package com.example.lloader.crimeapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Alexander Garkavenko
 */

public class PictureUtils {

    public static Bitmap getScaledBitmap(final String path, final int destHeight, final int destWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        final Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        final int srcHeight = options.outHeight;
        final int srcWidth = options.outWidth;

        int inSampleSize = 1;
        if (srcHeight > destHeight && srcWidth > destWidth) {
            final float heightScale = srcHeight / destHeight;
            final float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(final String path, final Activity activity) {
        final Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return getScaledBitmap(path, point.y, point.x);
    }
}
