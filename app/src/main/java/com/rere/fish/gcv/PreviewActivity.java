package com.rere.fish.gcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.result.ResultActivity;
import com.rere.fish.gcv.utils.FileUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends AppCompatActivity {
    private static String FILE_NAME = "vader3.png";
    private static String EXTRA_INTENT_IMAGE = "EXTRA_IMAGE";
    @BindView(R.id.tab_layout) LinearLayout tabLayout;
    @Inject SelfServiceInterface selfServiceInterface;
    private CropImageView cropImageView;
    private String pathToFile;

    public static Intent createIntent(Context context, String pathToImage) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(EXTRA_INTENT_IMAGE, pathToImage);
        return intent;
    }

    @OnClick(R.id.text_action_cancel)
    public void onCancelClicked() {
        this.finish();
    }

    @OnClick(R.id.text_action_next)
    public void onNextClicked() {
        String croppedImagePath = FileUtil.saveCroppedImage(getApplicationContext(),
                cropImageView.getCroppedImage(), getFileName());
        startActivity(ResultActivity.createIntent(getApplicationContext(), croppedImagePath));
        this.finish();
    }

    @OnClick(R.id.image_action_rotate)
    public void onRotateClicked() {
        //TODO : STUB
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);

        pathToFile = getIntent().getStringExtra(EXTRA_INTENT_IMAGE);

        cropImageView.setImageBitmap(getImageBitmapFromStorage(pathToFile));
/*
        bottomNavigationView.setSelectedItemId(R.id.navigation_rotate);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_cancel:
                    break;
                case R.id.navigation_rotate:
                    Timber.i("is_rotate");
                    break;
                case R.id.navigation_next:
                    String croppedImagePath = FileUtil.saveCroppedImage(getApplicationContext(),
                            cropImageView.getCroppedImage(), getFileName());
                    startActivity(
                            ResultActivity.createIntent(getApplicationContext(), croppedImagePath));
                    this.finish();
                    break;
            }
            return false;
        });*/
    }

    public String getFileName() {
        return pathToFile.split("/")[pathToFile.split("/").length - 1];
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
}
