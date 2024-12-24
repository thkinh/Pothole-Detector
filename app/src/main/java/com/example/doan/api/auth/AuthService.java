package com.example.doan.api.auth;
import com.example.doan.model.AppUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService{

    @GET("checkUserExists")
    Call<Boolean> checkUserExists(@Query("email") String email);

    @GET("user/SIRequest")
    Call<AppUser> SIRequest(@Query("email")String email, @Query("password")String password);

    @GET("user/get")
    Call<AppUser> simpleGETbyID(@Query("id")Integer id);

    @POST("user/add")
    Call<AppUser> add(@Body AppUser client);

    @GET("user/getByEmail")
    Call<AppUser> signIn(@Query("email") String email);

    @GET("user/get/ALL")
    Call<List<AppUser>> getALLUser();

    @POST("user/password/getVerify")
    Call<Integer> getVerifyCode(@Query("email") String email);

    @POST("user/password/OTPConfirm")
    Call<String> confirmCode(@Query("email") String email, @Query("code") String verifyCode);

    @POST("user/password/confirm")
    Call<AppUser> confirmPass(@Query("email") String email, @Query("password") String password);

    @POST("user/updateDistance")
    Call<Integer> updateDistance(@Query("id") Integer id, @Query("distance") Long distance);

}
