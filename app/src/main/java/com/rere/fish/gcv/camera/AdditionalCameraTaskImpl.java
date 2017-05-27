package com.rere.fish.gcv.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.media.ExifInterface;

import com.rere.fish.gcv.PreviewActivity;
import com.rere.fish.gcv.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

import static android.support.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.support.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.support.media.ExifInterface.ORIENTATION_ROTATE_90;

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
        String finalPath;

        @Override
        protected Void doInBackground(Bitmap... bmp) {
            finalPath = FileUtil.getAppStorage(context) + FileUtil.generateRandomString();
            Timber.i("External storage path" + finalPath);

            File f = new File(FileUtil.getAppStorage(context));
            if (!f.exists()) {
                f.mkdirs();
            }
            saveImageToDirectory(finalPath, bmp[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Uri uri = Uri.fromFile(new File(finalPath));
            context.startActivity(PreviewActivity.createIntent(context, uri));
            ((Activity) context).finish();
        }

        void saveImageToDirectory(String path, Bitmap bmp) {
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(path);
                // PNG is a lossless format, the compression factor (100) is ignored
                Matrix matrix = new Matrix();
                matrix.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);

                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is your Bitmap instance

                scanImageToGallery(path);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        bmp.recycle();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void scanImageToGallery(String path) {
            MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"},
                    null);
        }

        int getRotationImage(String path) {
            File f = new File(path);
            try {
                ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int rotationAlign = 0;

                switch (orientation) {
                    case ORIENTATION_ROTATE_270:
                        rotationAlign = 270;
                        break;
                    case ORIENTATION_ROTATE_180:
                        rotationAlign = 180;
                        break;
                    case ORIENTATION_ROTATE_90:
                        rotationAlign = 90;
                        break;
                }

                return rotationAlign;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}