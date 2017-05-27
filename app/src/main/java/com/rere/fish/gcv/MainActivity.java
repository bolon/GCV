package com.rere.fish.gcv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.rere.fish.gcv.camera.AdditionalCameraTaskImpl;
import com.rere.fish.gcv.modules.BukalapakInterface;
import com.rere.fish.gcv.uicustoms.CircularPulsingButton;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    final int IMG_REQ = 1;

    @BindView(R.id.rootView) CoordinatorLayout rootView;
    @BindView(R.id.camera) CameraView cameraView;
    @BindView(R.id.btn_capture) CircularPulsingButton btnCapture;
    @BindView(R.id.btn_gallery) CircularPulsingButton btnGallery;
    @BindView(R.id.btn_info) CircularPulsingButton btnInfo;
    @BindView(R.id.rootRePermission) RelativeLayout rePermissionLayout;

    @Inject BukalapakInterface bukalapakInterface;
    private AdditionalCameraTaskImpl additionalCameraTask;
    private MultiplePermissionsListener multiplePermissionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);

        btnCapture.setClickable(true);

        if (!checkPermission()) {
            rePermissionLayout.setVisibility(View.VISIBLE);
        }

        setConfigCamera();
        setupButton();

        additionalCameraTask = new AdditionalCameraTaskImpl(this);

        multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.getDeniedPermissionResponses().isEmpty()) {
                    for (PermissionDeniedResponse p : report.getDeniedPermissionResponses()) {
                        Timber.i("denied " + p.getPermissionName());
                    }
                } else {
                    rePermissionLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Snackbar.make(rootView, "Please manually allow permission(s) from settings",
                        Snackbar.LENGTH_SHORT).show();
                token.cancelPermissionRequest();

            }
        };
    }

    @OnClick(R.id.btn_capture)
    public void onClickCaptureButton() {
        btnCapture.setVisibility(GONE);
        cameraView.captureImage();
    }

    @OnClick(R.id.btn_gallery)
    void onClickButtonGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_REQ);
    }

    @OnClick(R.id.btnRequestPermission)
    public void onClickButtonPermission() {
        Timber.i("checkpermission", checkPermission());
        if (!checkPermission()) {
            Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE).withListener(
                    new CompositeMultiplePermissionsListener(multiplePermissionsListener,
                            SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                                    R.string.all_permissions_denied_feedback)
                                    //.withOpenSettingsButton("Settings") //TODO : Handle this
                                    .build())).withErrorListener(
                    error -> Timber.e("Error request permission " + error.toString())).check();
        } else {
            rePermissionLayout.setVisibility(GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null & data.getData() != null) {
            startActivity(PreviewActivity.createIntent(getApplicationContext(), data.getData()));
            this.finish();
        }
    }

    private void setConfigCamera() {
        cameraView.setJpegQuality(70);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                additionalCameraTask.onFinishCamera(result);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        btnCapture.setVisibility(View.VISIBLE);
        if (checkPermission()) {
            startCamera();
        }
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    void startCamera() {
        try {
            if (cameraView != null) cameraView.start();
            else Timber.i("camera null");
        } catch (RuntimeException e) {
            Timber.e("Failed to start camera " + e.getMessage());
        }
    }

    private boolean checkPermission() {
        int resultCamera = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);
        int resultMedia1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultMedia2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (resultCamera == PackageManager.PERMISSION_GRANTED & resultMedia1 == PackageManager.PERMISSION_GRANTED & resultMedia2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void setupButton() {
        VectorDrawableCompat icon = VectorDrawableCompat.create(getResources(),
                R.drawable.ic_picture_black_24dp, null);

        btnCapture.setColor(getResources().getColor(R.color.alphaGray80));
        btnCapture.setDrawable(getResources().getDrawable(R.mipmap.icon_camera));
        btnGallery.setColor(Color.TRANSPARENT);
        icon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        btnGallery.setDrawable(icon);
        icon = VectorDrawableCompat.create(getResources(), R.drawable.ic_info_outline_black_24dp,
                null);
        icon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        btnInfo.setColor(Color.TRANSPARENT);
        btnInfo.setDrawable(icon);

        btnCapture.setAnimationDuration(300);
    }

}
