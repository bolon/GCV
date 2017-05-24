package com.rere.fish.gcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.result.ResultActivity;
import com.rere.fish.gcv.utils.FileUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {
    private static String FILE_NAME = "vader3.png";
    private static String EXTRA_INTENT_IMAGE = "EXTRA_IMAGE";
    @BindView(R.id.toolbarPreviewActivity) Toolbar toolbar;
    @Inject SelfServiceInterface selfServiceInterface;
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

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);

/*        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/
        pathToFile = getIntent().getStringExtra(EXTRA_INTENT_IMAGE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationIcon(new IconDrawable(this, FontAwesomeIcons.fa_close).colorRes(R.color.colorAccent).actionBarSize());

        try {
            InputStream is = getApplicationContext().getAssets().open(FILE_NAME);
            cropImageView.setImageBitmap(getImageBitmapFromStorage(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_preview, menu);

        menu.findItem(R.id.action_next).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_check).colorRes(R.color.colorAccent).actionBarSize()).setOnMenuItemClickListener(item -> {
            String croppedImagePath = FileUtil.saveCroppedImage(getApplicationContext(), cropImageView.getCroppedImage(), getFileName());
            startActivity(ResultActivity.createIntent(getApplicationContext(), croppedImagePath));
            this.finish();
            return true;
        });


        return true;
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
