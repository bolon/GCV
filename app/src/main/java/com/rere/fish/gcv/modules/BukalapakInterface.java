package com.rere.fish.gcv.modules;

import com.rere.fish.gcv.result.product.ResponseBL;

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

    //https://api.bukalapak.com/v2/products.json?keywords=fixie&page=2&per_page=20
    @GET("products.json")
    Call<ResponseBL> getListProducts(@Query("keywords") String keywords, @Query("page") int page, @Query("per_page") int perPage);
}
