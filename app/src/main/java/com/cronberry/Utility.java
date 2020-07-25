package com.cronberry;

import android.app.Activity;

import com.google.firebase.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utility {

    private static Retrofit retrofitObj = null;

    public static WebAPI getRetrofitObj(Activity act) {
        int BITBUDDY_TIMEOUT = 1;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .readTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(BITBUDDY_TIMEOUT, TimeUnit.MINUTES)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new Interceptor(){

                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();
        retrofitObj = new Retrofit.Builder()
                .baseUrl("https://api.cronberry.com/cronberry/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitObj.create(WebAPI.class);
    }
}
