package com.rere.fish.gcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PreviewActivity extends AppCompatActivity {
    private static String FILE_NAME = "vader3.png";
    private static String EXTRA_INTENT_IMAGE = "EXTRA_IMAGE";
    private CropImageView cropImageView;
    private String pathToFile;

    public static Intent createIntent(Context context, String pathToImage) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(EXTRA_INTENT_IMAGE, pathToImage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);


        Uri uri = Uri.fromFile(new File("//android_asset/vader2.jpeg"));
/*        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/

        pathToFile = getIntent().getStringExtra(EXTRA_INTENT_IMAGE);

        try {
            InputStream is = getApplicationContext().getAssets().open(FILE_NAME);
            cropImageView.setImageBitmap(getImageBitmapFromStorage(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private Bitmap getImageBitmapFromStorage(String pathToFile) {
        Drawable drawable = null;
        File file = new File(pathToFile);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true); //for changing sz

    }
}
