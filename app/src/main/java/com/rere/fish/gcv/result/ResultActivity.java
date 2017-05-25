package com.rere.fish.gcv.result;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.rere.fish.gcv.App;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.modules.GCVInterface;
import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.result.preexec.PreProcessFragment;
import com.rere.fish.gcv.result.product.ProductFragment;
import com.rere.fish.gcv.result.product.ResponseBL;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements ProductFragment.OnProductsFragmentInteractionListener, PreProcessFragment.OnLoadingFragmentInteractionListener {
    public static final String PREPROCESS = "LOADING_FRAG";
    public static final String PRODUCTS = "PRODUCTS_FRAG";
    private static final String KEY_PATH = "FILEPATH";
    @BindView(R.id.content) FrameLayout frameLayout;
    @BindView(R.id.rootView) CoordinatorLayout coordinatorLayout;
    @Inject SelfServiceInterface engineServices;
    @Inject GCVInterface gcvServices;

    List<Fragment> listFragment = new ArrayList<>();
    private String tempFilePath;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;

    public static Intent createIntent(Context context, String filePath) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(KEY_PATH, filePath);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);
        App.get(getApplicationContext()).getInjector().inject(this);

        tempFilePath = getIntent().getStringExtra(KEY_PATH);

        fragmentManager = getSupportFragmentManager();

        Fragment f1 = PreProcessFragment.newInstance(tempFilePath);

        ft = fragmentManager.beginTransaction();
        ft.add(R.id.content, f1, PREPROCESS);
        ft.commit();

        listFragment.add(f1);

        resurfaceFragment(PREPROCESS);
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

    @Override
    public void onPreProcessInteraction(ResponseBL responseBL) {
        Fragment f2 = ProductFragment.newInstance(2, responseBL);
        listFragment.add(f2);

        ft = fragmentManager.beginTransaction();
        ft.add(R.id.content, f2, PRODUCTS);
        ft.commit();

        resurfaceFragment(PRODUCTS);
    }

    @Override
    public void onProductsInteraction(String url) {
        Timber.i("item_clicked " + url);
        Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(newIntent);
    }
}
