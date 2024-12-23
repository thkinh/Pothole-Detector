package com.example.doan.api.auth;

import android.util.Log;
import com.example.doan.api.RetrofitInstance;
import com.example.doan.model.AppUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private static AuthManager instance;
    private final AuthService authService;
    private AppUser globalUserAccount = null;

    private AuthManager() {
        this.authService = RetrofitInstance.getInstance().create(AuthService.class);
    }

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public AppUser getAccount()
    {
        return globalUserAccount;
    }
    public void setGlobalAccount(AppUser userFound) {
        this.globalUserAccount = userFound;
    }
    public AppUser simpleGETCALL() {
        Call<AppUser> call = authService.simpleGETbyID(1);
        call.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                //Checking for the response
                if (response.code() == 200){
                    Log.e("HTTP_RESPONSE", "Niceeeee");
                }
                    globalUserAccount = new AppUser(
                        response.body().getUsername(),
                        response.body().getEmail(),
                        response.body().getPassword());
                Log.d("Login Acticity", globalUserAccount.getUsername());
            }
            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                Log.e("SIMPLE GET CALL", t.getMessage().toString());
            }
        });
        return globalUserAccount;
    }
    public void signUp(AppUser appUser, SignUpCallback callback) {
        Call<AppUser> appUserCall = authService.add(appUser);
        appUserCall.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                if (response.isSuccessful() && response.body()!= null){
                    Log.d("HTTP_Response", "200");
                    AppUser appUser = new AppUser(
                            response.body().getUsername(),
                            response.body().getEmail(),
                            response.body().getPassword());
                    callback.onSuccess(appUser);
                }
                if (response.code() == 502){
                    callback.onFailure("This email already exists");
                }
                else {
                    Log.e("HTTP_Response", response.raw().toString());
                    callback.onFailure(response.message());
                }
            }
            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }
    public void signIn(String email, String password, SignInCallback callback) {
        Call<AppUser> call = authService.SIRequest(email, password);
        call.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                if (response.code() == 200) {
                    Log.d("HTTP_Response", "200");
                    AppUser appUser = new AppUser(
                            response.body().getUsername(),
                            response.body().getEmail(),
                            response.body().getPassword());
                    appUser.setId(response.body().getId());
                    callback.onSuccess(appUser);
                }
                else if (response.code() == 501) {
                    Log.d("HTTP Response", "501");
                    Log.e("HTTP message", "Incorrect password");
                    callback.onFailure("Incorrect password");
                }
                else {
                    callback.onFailure("Sign-in failed: " + response.message());
                    Log.e("API", response.message());
                }
            }
            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }
    public void getVerify(String email,  GetVerifyCallback callback){
        String trueEmail = "\"" + email + "\"";
        Call<Integer> call = authService.getVerifyCode(trueEmail);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()){
                    Log.d("HTTP_Response", "200");
                    callback.onSuccess(response.body());
                }
                else {
                    Log.e("API error: ", "errorCode: " +response.body());
                    callback.onFailure("Your email has a problem");
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }
    public void confirmOTP(String email, String verifyCode, ConfirmOTPCallback callback) {
        String trueEmail = "\"" + email + "\"";
        Call<String> call = authService.confirmCode(trueEmail, verifyCode);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        callback.onSuccess("OTP Confirmed");
                    }
                    else {
                        callback.onFailure("Unexpected response code: " + response.code());
                    }
                } else {
                    callback.onFailure("API call failed with error: " + response.body());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void confirmPassword(String email, String password, ConfirmPasswordCallBack callBack){
        Call<AppUser> appUserCall = authService.confirmPass(email, password);
        appUserCall.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                if (response.isSuccessful()){
                    AppUser user = response.body();
                    callBack.onSuccess(user);
                }
                else {
                    callBack.onFailure("Update password failed: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void getALL(GetALLCallBack callBack){
        Call<List<AppUser>> call = authService.getALLUser();
        call.enqueue(new Callback<List<AppUser>>() {
            @Override
            public void onResponse(Call<List<AppUser>> call, Response<List<AppUser>> response) {
                if (response.isSuccessful()){
                    callBack.onSuccess(response.body());
                }
                else {
                    callBack.onFailure(response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<List<AppUser>> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void updateDistance(Integer id, Long distance, UpdateDistanceCallBack callBack){
        Call<Integer> call = authService.updateDistance(id, distance);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 200){
                    callBack.onSuccess(response.body());
                }
                else {
                    Log.e("__HTTP RESPONSE", response.raw().toString());
                    callBack.onFailure(response.message());
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public interface UpdateDistanceCallBack{
        void onSuccess(Integer id);
        void onFailure(String errorMessage);
    }

    public  interface GetALLCallBack{
        void onSuccess(List<AppUser> fetchedUsers);
        void onFailure(String errorMessage);
    }

    public interface ConfirmPasswordCallBack{
        void onSuccess(AppUser user);
        void onFailure(String errorMessage);
    }

    public interface ConfirmOTPCallback{
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface GetVerifyCallback{
        void onSuccess(Integer id);
        void onFailure(String errorMessage);
    }

    public interface SignUpCallback{
        void onSuccess(AppUser user);
        void onFailure(String errorMessage);
    }

    public interface SignInCallback {
        void onSuccess(AppUser user);
        void onFailure(String errorMessage);
    }
}
