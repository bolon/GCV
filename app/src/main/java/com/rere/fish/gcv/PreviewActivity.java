package com.rere.fish.gcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.result.ResultActivity;
import com.rere.fish.gcv.utils.FileUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PreviewActivity extends AppCompatActivity {
    private static String FILE_NAME = "vader3.png";
    private static String EXTRA_INTENT_IMAGE = "EXTRA_IMAGE";
    @BindView(R.id.tab_layout) LinearLayout tabLayout;
    @Inject SelfServiceInterface selfServiceInterface;
    private CropImageView cropImageView;

    public static Intent createIntent(Context context, Uri imgUri) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.setType(EXTRA_INTENT_IMAGE);
        intent.setAction(EXTRA_INTENT_IMAGE);
        intent.putExtra(EXTRA_INTENT_IMAGE, imgUri.toString());
        return intent;
    }

    @OnClick(R.id.text_action_cancel)
    public void onCancelClicked() {
        this.finish();
    }

    @OnClick(R.id.text_action_next)
    public void onNextClicked() {
        String croppedImagePath = FileUtil.saveCroppedImage(getApplicationContext(),
                cropImageView.getCroppedImage(), FileUtil.generateRandomString());
        startActivity(ResultActivity.createIntent(getApplicationContext(), croppedImagePath));
        this.finish();
    }

    @OnClick(R.id.image_action_rotate)
    public void onRotateClicked() {
        cropImageView.rotateImage(90);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        } else {
            if (type.equals(EXTRA_INTENT_IMAGE)) {
                handleCapturedImage(intent);
            }
        }
    }

    private void handleCapturedImage(Intent intent) {
        InputStream input;
        Bitmap bmp;

        Uri fileUri = Uri.parse(intent.getStringExtra(EXTRA_INTENT_IMAGE));

        try {
            input = getContentResolver().openInputStream(fileUri);
            bmp = BitmapFactory.decodeStream(input);
            cropImageView.setImageBitmap(bmp);
        } catch (FileNotFoundException e) {
            Timber.e("No file found for URI : " + fileUri.getPath());
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            cropImageView.setImageUriAsync(imageUri);
            // Update UI to reflect image being shared
        }
    }

    private Bitmap getImageBitmapFromStorage(String pathToFile) {
        Drawable drawable = null;
        File file = new File(pathToFile);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true); //for changing sz
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        //TODO : Change this later (just for quick fix). Investigate cameraview | hint : put cam in separate thread
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        this.finish();
    }
}
