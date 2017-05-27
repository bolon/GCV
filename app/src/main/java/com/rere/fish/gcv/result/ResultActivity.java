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
import com.rere.fish.gcv.MainActivity;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.modules.BukalapakInterface;
import com.rere.fish.gcv.result.preexec.PreProcessFragment;
import com.rere.fish.gcv.result.product.ProductFragment;
import com.rere.fish.gcv.result.product.ResponseBL;
import com.rere.fish.gcv.utils.FileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements ProductFragment.OnProductsFragmentInteractionListener, PreProcessFragment.OnLoadingFragmentInteractionListener, OnFinishBLProcess {
    public static final String PREPROCESS = "LOADING_FRAG";
    public static final String PRODUCTS = "PRODUCTS_FRAG";
    public static final int NUMBER_PER_FETCH = 20;
    private static final String KEY_PATH = "FILEPATH";
    private static final int INITIAL_PAGE = 1;
    @BindView(R.id.content) FrameLayout frameLayout;
    @BindView(R.id.rootView) CoordinatorLayout coordinatorLayout;
    @Inject BukalapakInterface bukalapakInterface;
    List<Fragment> listFragment = new ArrayList<>();
    private String finalKeywords;
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

        String tempFilePath = getIntent().getStringExtra(KEY_PATH);

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
    public void onReceivedKeywords(String keywords) {
        Timber.i("keyword_here " + keywords);
        finalKeywords = keywords;
        callBukalapakService(finalKeywords, INITIAL_PAGE, NUMBER_PER_FETCH, Collections.EMPTY_LIST);
    }

    @Override
    public void onProductsInteraction(String url) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(newIntent);
    }

    @Override
    public void onRequestedMoreProducts(int page, int number, List<String> categories) {
        callBukalapakService(finalKeywords, page, number, Collections.EMPTY_LIST);
    }

    private void callBukalapakService(String keywords, int page, int numbers, List<String> categories) {
        bukalapakInterface.getListProducts(keywords, page, numbers).enqueue(
                new Callback<ResponseBL>() {
                    @Override
                    public void onResponse(Call<ResponseBL> call, Response<ResponseBL> response) {
                        if (response.isSuccessful()) {
                            if (page == INITIAL_PAGE) onReceivedInitialBLResult(response.body());
                            else ((ProductFragment) getSupportFragmentManager().findFragmentByTag(
                                    PRODUCTS)).doProductAddition(response.body().getProducts());
                        } else Timber.e("Can't get products");

                        Timber.i("url_request : " + call.request().url().toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBL> call, Throwable t) {
                        Timber.e(
                                "Can't get items with url : " + call.request().url().toString() + t.getMessage());
                    }
                });
    }

    @Override
    public void onReceivedInitialBLResult(ResponseBL resp) {
        Fragment f2 = ProductFragment.newInstance(2, resp);
        listFragment.add(f2);

        ft = fragmentManager.beginTransaction();
        ft.add(R.id.content, f2, PRODUCTS);
        ft.commit();

        resurfaceFragment(PRODUCTS);
        FileUtil.cleanTempFile(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        this.finish();
    }
}
