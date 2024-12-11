package com.example.doan.api.auth;

import android.util.Log;
import com.example.doan.api.RetrofitInstance;
import com.example.doan.model.AppUser;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private static AuthManager instance;
    private final AuthService authService;
    private AppUser globalUserAccount;

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

    public AppUser simpleGETCALL() {
        Call<AppUser> call = authService.simpleGETbyID(1);
        call.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                //Checking for the response
                if (response.code() != 200){
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

    public void signIn(String email, String password, SignInCallback callback) {
        Call<AppUser> call = authService.signIn(email);
        call.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                if (response.code() == 200) {
                    Log.d("HTTP_Response", "200");
                    AppUser appUser = new AppUser(
                            response.body().getUsername(),
                            response.body().getEmail(),
                            response.body().getPassword());
                    // Check if the passwords match
                    if (appUser.getPassword().equals(password)) {
                        callback.onSuccess(appUser);
                    } else {
                        callback.onFailure("Incorrect password");
                    }
                } else {
                    callback.onFailure("Sign-in failed: " + response.message());
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
                    Log.e("API error: ", response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
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
                else {
                    callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
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
