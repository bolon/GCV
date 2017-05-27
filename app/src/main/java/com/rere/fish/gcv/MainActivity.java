package com.rere.fish.gcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.rere.fish.gcv.imagesource.CaptureImageFragment;
import com.rere.fish.gcv.modules.BukalapakInterface;
import com.rere.fish.gcv.notification.NotifFragment;
import com.rere.fish.gcv.process.ProcessFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    final String CAPTURE = "capture_frag";
    final String PROCESS = "process_frag";
    final String GARBAGE = "garbage_frag";
    final int IMG_REQ = 1;

    Fragment f1 = CaptureImageFragment.newInstance();
    Fragment f2 = ProcessFragment.newInstance();
    Fragment f3 = NotifFragment.newInstance();

    List<Fragment> listFragment = new ArrayList<>();

    @BindView(R.id.rootView) CoordinatorLayout rootView;

    @Inject BukalapakInterface bukalapakInterface;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;

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

        resurfaceFragment(CAPTURE);
    }

    @Override
    public void onButtonGalleryClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null & data.getData() != null) {
            startActivity(PreviewActivity.createIntent(getApplicationContext(), data.getData()));
        }
    }

    private void resurfaceFragment(String TAG) {
        ft = fragmentManager.beginTransaction();
        for (Fragment f : listFragment) {
            if (TAG.equals(f.getTag())) {
                ft.show(f);
            } else ft.hide(f);
        }
        ft.commit();
    }
}
