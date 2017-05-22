package com.rere.fish.gcv.result;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.rere.fish.gcv.App;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.modules.SelfServiceInterface;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    @Inject
    SelfServiceInterface selfServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);
        App.get(getApplicationContext()).getInjector().inject(this);

        startServices();
    }

    public void startServices() {
        RequestBody requestRaw = RequestBody.create(MediaType.parse("application/json"), loadJSONFromAsset());

        selfServiceInterface.getLabel(requestRaw).enqueue(new Callback<ResponseEngineModel>() {
            @Override
            public void onResponse(Call<ResponseEngineModel> call, Response<ResponseEngineModel> response) {
                Timber.i("result_size : " + response.body().getListResponsePair().size());
            }

            @Override
            public void onFailure(Call<ResponseEngineModel> call, Throwable t) {

            }
        });

        //TODO : GET RESULT FROM BL
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open("request_body_engine.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e("Error reading file", ex.getMessage());
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}
