package com.rere.fish.gcv.modules;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Android dev on 5/21/17.
 */

public interface GCVInterface {
    @POST("./")
    Call<ResponseBody> getLabelDetection(@Query("key") String key, @Body RequestBody body);
}
