package com.example.gymmateapplication; // 🔴 CHANGE to your real package name

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 🔴 CHANGE if using real phone (use your PC IP)
    private static final String BASE_URL = "http://192.168.1.5/gymmate_api/";

    private static Retrofit retrofit = null;

    public static MetricsApi getApi() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(MetricsApi.class);
    }
}
