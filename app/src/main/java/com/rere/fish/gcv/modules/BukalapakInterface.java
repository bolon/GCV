package com.rere.fish.gcv.modules;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Android dev on 5/11/17.
 */

public interface BukalapakInterface {
    @GET("list")
    Call<Object> getListProduct(@Header("Authorization") String token, @Query("cursor") int cursor, @Query("size") int size);
}
