package com.rere.fish.gcv.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;

import com.rere.fish.gcv.PreviewActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import timber.log.Timber;

/**
 * Created by Android dev on 5/20/17.
 */

public class AdditionalCameraTaskImpl implements AdditionalCameraTask {
    private final Context context;

    public AdditionalCameraTaskImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onFinishCamera(Bitmap bmp) {
        new StoreFile().execute(bmp);
    }

    class StoreFile extends AsyncTask<Bitmap, Void, Void> {
        final String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().toString();
        final String EXTERNAL_STORAGE_APP = EXTERNAL_STORAGE_PATH + File.separator + context.getPackageName() + File.separator + "Snapper/";
        String finalPath;

        @Override
        protected Void doInBackground(Bitmap... bmp) {
            finalPath = EXTERNAL_STORAGE_APP + generateRandomString();
            Timber.i("External storage path" + finalPath);

            File f = new File(EXTERNAL_STORAGE_APP);
            if (!f.exists()) {
                f.mkdirs();
            }
            saveImageToDirectory(finalPath, bmp[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            context.startActivity(PreviewActivity.createIntent(context, finalPath));
        }

        void saveImageToDirectory(String path, Bitmap bmp) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
                // PNG is a lossless format, the compression factor (100) is ignored
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, out); // bmp is your Bitmap instance
                scanImageToGallery(path);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void scanImageToGallery(String path) {
            MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"}, null);
        }

        String generateRandomString() {
            return new BigInteger(80, new SecureRandom()).toString(32) + ".png";
        }
    }
}