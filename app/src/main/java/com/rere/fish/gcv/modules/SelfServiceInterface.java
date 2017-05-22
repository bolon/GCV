package com.rere.fish.gcv.modules;

import com.rere.fish.gcv.result.ResponseEngineModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Android dev on 5/11/17.
 */

public interface SelfServiceInterface {
    @POST("label")
    Call<ResponseEngineModel> getLabel(@Body RequestBody requestBody);
}
