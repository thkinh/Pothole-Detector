package com.example.doan.api.potholes;

import com.example.doan.model.Pothole;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PotholeService {
    @GET("pothole/get")
    Call<List<Pothole>> getPotholes(@Query("user") String username);

    @POST("pothole/add")
    Call<Pothole> addPothole(@Body Pothole pothole);

    @GET("pothole/get/ALL")
    Call<List<Pothole>> getALLPotholes();

    @GET("pothole/findByLocation")
    Call<Pothole> findByLocation(@Query("latitude") Double latitude, @Query("longitude") Double longitude);

    @Multipart
    @POST("pothole/uploadImage")
    Call<ResponseBody> uploadPotholeImage(@Query("id") int potholeId, @Part MultipartBody.Part image);

    @GET("pothole/image")
    Call<ResponseBody> getPotholeImage(@Query("id") Integer potholeId);

    @DELETE("pothole/deleteDuplicate")
    Call<String> deletePothole (@Query("id") Integer potholeId);

    @DELETE("pothole/deleteDuplicateByLL")
    Call<String> deletePotholeByLL (@Query("lat") Double latitude, @Query("long") Double longitude);

}
