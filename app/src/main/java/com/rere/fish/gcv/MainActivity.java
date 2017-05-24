package com.rere.fish.gcv;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

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

    Fragment f1 = CaptureImageFragment.newInstance();
    Fragment f2 = ProcessFragment.newInstance();
    Fragment f3 = NotifFragment.newInstance();

    List<Fragment> listFragment = new ArrayList<>();

    @BindView(R.id.rootView) CoordinatorLayout rootView;
    @BindView(R.id.navigation) BottomNavigationView navigation;

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

        navigation.setOnNavigationItemSelectedListener(setNavigationListener());
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener setNavigationListener() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
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

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("something", "here");
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
