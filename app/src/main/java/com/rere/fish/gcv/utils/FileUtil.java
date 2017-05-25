package com.rere.fish.gcv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Android dev on 5/23/17.
 */

public class FileUtil {
    public static final String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().toString();

    public static String saveCroppedImage(Context context, Bitmap bmp, String originalName) {
        String tempPath = getAppCacheStorage(context) + File.separator + "temp" + File.separator;

        try {
            File tempDir = new File(tempPath);
            if (!tempDir.exists()) tempDir.mkdirs();

            File tempFile = File.createTempFile("cropped" + originalName, null, tempDir);
            FileOutputStream out = new FileOutputStream(tempFile);

            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppStorage(Context context) {
        return EXTERNAL_STORAGE_PATH + File.separator + context.getPackageName() + File.separator + "Snapper/";
    }

    public static String getAppCacheStorage(Context context) {
        return context.getExternalCacheDir().getPath() + File.separator + context.getPackageName() + File.separator;
    }

    public static Bitmap getBitmapFromStorage(String path) {
        return decodeSampledBitmapFromFile(path, 100, 100);
    }

    private static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void cleanTempFile(Context context) {
        String tempPath = getAppCacheStorage(context) + File.separator + "temp" + File.separator;

        File tempDir = new File(tempPath);

        for (File f : tempDir.listFiles()) {
            if (!f.isDirectory()) f.delete();
        }
    }

    public static String generateRandomString() {
        return new BigInteger(80, new SecureRandom()).toString(32) + ".png";
    }
}
