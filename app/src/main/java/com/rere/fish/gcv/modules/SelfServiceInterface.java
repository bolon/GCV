package com.rere.fish.gcv.modules;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Android dev on 5/11/17.
 */

public interface SelfServiceInterface {
    @POST("generateImg")
    Call<Object> generateImg(@Header("Authorization") String key, @Body Object body);
}
