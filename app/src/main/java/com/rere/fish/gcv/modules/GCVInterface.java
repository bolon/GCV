package com.rere.fish.gcv.modules;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Android dev on 5/21/17.
 */

public interface GCVInterface {
    @POST("label")
    Call<BatchAnnotateImagesResponse> getLabelDetection();
}
