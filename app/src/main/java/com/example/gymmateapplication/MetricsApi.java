package com.example.gymmateapplication; // 🔴 CHANGE to your real package name

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MetricsApi {

    @FormUrlEncoded
    @POST("calc_metrics.php")
    Call<MetricsResponse> calculate(
            @Field("weight") float weight,
            @Field("height") float height,
            @Field("age") int age,
            @Field("gender") String gender
    );
}
