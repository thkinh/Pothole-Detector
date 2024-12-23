package com.example.doan.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitInstance {
    private static final String BASE_URL = "http://171.249.144.189:8080/api/";
    private static Retrofit retrofit;

    private RetrofitInstance() {}

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitInstance.class) {
                if (retrofit == null) {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }
}
