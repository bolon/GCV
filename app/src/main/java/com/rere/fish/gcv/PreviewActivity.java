package com.rere.fish.gcv;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PreviewActivity extends AppCompatActivity {
    CropImageView cropImageView;
    static String FILE_NAME = "vader3.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        Uri uri = Uri.fromFile(new File("//android_asset/vader2.jpeg"));

/*        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/

        try {
            InputStream is = getApplicationContext().getAssets().open(FILE_NAME);
            cropImageView.setImageBitmap(BitmapFactory.decodeStream(is));
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
}
