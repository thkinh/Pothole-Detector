package com.example.doan.api.auth;
import com.example.doan.model.AppUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService{
    @POST("user/add")
    Call<AppUser> add(@Body AppUser client);

    @GET("user/get")
    Call<AppUser> simpleGETbyID(@Query("id")Integer id);

    @GET("user/getByEmail")
    Call<AppUser> signIn(@Query("email") String email);
}
