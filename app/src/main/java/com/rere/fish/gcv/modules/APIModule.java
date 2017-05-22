package com.rere.fish.gcv.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Android dev on 5/11/17.
 */
@Module(
        complete = false,
        library = true
)

public class APIModule {
    public static final String ENDPOINT_BUKALAPAK = "https://api.bukalapak.com/v2/";
    public static final String ENDPOINT_OWNAPI = "http://192.168.2.59:8000/";
    public static final String ENDPOINT_GCV = "https://www.jtenaaaaun.com/tenunapi/";
    public static final String ACCESS_TOKEN_TEMP = "Jt3nuN_20161130";

    @Singleton
    @Provides
    Retrofit.Builder provideRetrofit(Call.Factory callFactory, Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .callFactory(callFactory);
    }

    @Singleton
    @Provides
    Call.Factory providesCallFactory() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Singleton
    @Provides
    Gson providesGson() {
        return new GsonBuilder().create();
    }

    @Singleton
    @Provides
    BukalapakInterface provideBukalapakService(Retrofit.Builder retrofitBuilder) {
        Retrofit retrofitInstance = retrofitBuilder.baseUrl(ENDPOINT_BUKALAPAK)
                .build();

        return retrofitInstance.create(BukalapakInterface.class);
    }

    @Singleton
    @Provides
    SelfServiceInterface provideSelfService(Retrofit.Builder retrofitBuilder) {
        Retrofit retrofitInstance = retrofitBuilder.baseUrl(ENDPOINT_OWNAPI)
                .build();

        return retrofitInstance.create(SelfServiceInterface.class);
    }

    @Singleton
    @Provides
    GCVInterface provideGCVService(Retrofit.Builder retrofitBuilder) {
        Retrofit retrofitInstance = retrofitBuilder.baseUrl(ENDPOINT_OWNAPI)
                .build();

        return retrofitInstance.create(GCVInterface.class);
    }
}
