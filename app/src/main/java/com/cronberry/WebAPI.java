package com.cronberry;

import java.util.HashMap;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WebAPI {

    @POST("campaign/register-audience-data")
    Call<LinkedHashMap<String, Object>> registerAudience(@Body HashMap<String, Object> map);
}
