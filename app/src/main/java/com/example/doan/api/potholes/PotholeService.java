package com.example.doan.api.potholes;

import com.example.doan.model.Pothole;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PotholeService {
    @GET("pothole/get")
    Call<List<Pothole>> getPotholes(@Query("user") String username);


    @POST("pothole/add")
    Call<String> addPothole(@Body Pothole pothole);

}
