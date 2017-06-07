package com.rere.fish.gcv.modules;

import com.rere.fish.gcv.ResponseScrapeImage;
import com.rere.fish.gcv.result.preexec.ResponseEngine;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Android dev on 5/11/17.
 */

public interface SelfServiceInterface {
    @POST("label")
    Call<ResponseEngine> getLabel(@Body RequestBody requestBody);

    @GET("urlimage")
    Call<ResponseScrapeImage> getUrlImgInstagram(@Query("ig_url") String igUrl);
}
