package com.rere.fish.gcv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rere.fish.gcv.imagesource.CaptureImageFragment;
import com.rere.fish.gcv.modules.BukalapakInterface;
import com.rere.fish.gcv.notification.NotifFragment;
import com.rere.fish.gcv.process.ProcessFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    final String CAPTURE = "capture_frag";
    final String PROCESS = "process_frag";
    final String GARBAGE = "garbage_frag";

    Fragment f1 = new CaptureImageFragment();
    Fragment f2 = new ProcessFragment();
    Fragment f3 = new NotifFragment();

    List<Fragment> listFragment = new ArrayList<>();

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @Inject
    BukalapakInterface bukalapakInterface;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private MultiplePermissionsListener multiplePermissionsListener;
    private DexterBuilder permissionBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.get(getApplicationContext()).getInjector().inject(this);
        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();

        ft = fragmentManager.beginTransaction();
        ft.add(R.id.content, f1, CAPTURE);
        ft.add(R.id.content, f2, PROCESS);
        ft.add(R.id.content, f3, GARBAGE);
        ft.commit();

        listFragment.add(f1);
        listFragment.add(f2);
        listFragment.add(f3);

        navigation.setOnNavigationItemSelectedListener(setNavigationListener());
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener setNavigationListener() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //TODO : CHECK THE FUCKING PERMISSION
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        navigation.setBackgroundColor(getColor(R.color.transparent));
                    } else {
                        navigation.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    resurfaceFragment(CAPTURE);
                    return true;
                case R.id.navigation_dashboard:
                    navigation.setBackgroundColor(getColor(R.color.colorPrimaryLight));
                    resurfaceFragment(PROCESS);
                    return true;
                case R.id.navigation_notifications:
                    navigation.setBackgroundColor(getColor(R.color.colorPrimaryLight));
                    resurfaceFragment(GARBAGE);
                    return true;
            }
            return false;
        };
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            result = PermissionChecker.checkSelfPermission(getApplicationContext(), permission)
                    == PermissionChecker.PERMISSION_GRANTED;
        }

        Timber.i("Permission for : " + permission + " is " + result);
        return result;
    }

    private DexterBuilder setPermission(MultiplePermissionsListener permissionListener) {
        return Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied())
                            setPermission(permissionListener);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                })
                .withErrorListener(error -> Timber.e("Error request permission " + error.toString()));
    }

    private MultiplePermissionsListener setupPermissionListener() {
        MultiplePermissionsListener cameraPermission = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Camera & audio permission")
                .withMessage("Camera permission are needed to take pictures")
                .withButtonText(android.R.string.ok)
                //.withIcon(R.mipmap.my_icon)
                .build();

        MultiplePermissionsListener writeStorage = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Write to storage permission")
                .withMessage("Write to storage permission are needed")
                .withButtonText(android.R.string.ok)
                //.withIcon(R.mipmap.my_icon)
                .build();

        MultiplePermissionsListener readStorage = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Read to storage permission")
                .withMessage("Read to storage permission are needed")
                .withButtonText(android.R.string.ok)
                //.withIcon(R.mipmap.my_icon)
                .build();

        return new CompositeMultiplePermissionsListener(cameraPermission, writeStorage, readStorage);
    }

    private void testInterface() {
        bukalapakInterface.getListProduct("toke3nt", 1, 20).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "ngpaein woi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("something", "here");
    }

    private void resurfaceFragment(String TAG) {
        ft = fragmentManager.beginTransaction();
        for (Fragment f : listFragment) {
            if (TAG.equals(f.getTag())) {
                ft.show(f);
            } else
                ft.hide(f);
        }
        ft.commit();
    }
}
