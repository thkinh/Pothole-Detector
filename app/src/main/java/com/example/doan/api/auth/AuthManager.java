package com.example.doan.api.auth;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.example.doan.api.RetrofitInstance;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.AppUser;

import com.example.doan.model.Pothole;
import com.example.doan.model.UserDetails;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
                        response.body().getPassword(),
                        response.body().getDistanceTraveled());
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
                            response.body().getPassword(),
                            response.body().getDistanceTraveled());
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
                            response.body().getPassword(),
                            response.body().getDistanceTraveled());
                    appUser.setId(response.body().getId());
                    appUser.setDetails(response.body().getDetails());
                    setGlobalAccount(appUser);
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

    public void getUser (Integer id, GetUserCallBack callBack){
        Call<AppUser> appUserCall = authService.simpleGETbyID(id);
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

    public void confirmPassword(String email, String password, ConfirmPasswordCallBack callBack) {
        // Add logging to help with debugging
        Log.d("PasswordUpdate", "Attempting to update password for: " + email);

        // Format email with quotes if needed by your API
        String formattedEmail = email.startsWith("\"") ? email : "\"" + email + "\"";

        Call<AppUser> appUserCall = authService.confirmPass(formattedEmail, password);
        appUserCall.enqueue(new Callback<AppUser>() {
            @Override
            public void onResponse(Call<AppUser> call, Response<AppUser> response) {
                if (response.isSuccessful()) {
                    AppUser user = response.body();
                    Log.d("PasswordUpdate", "Password updated successfully");
                    callBack.onSuccess(user);
                } else {
                    // Better error handling for non-successful responses
                    String errorMsg = "Update password failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("PasswordUpdate", "Error response body: " + errorBody);
                            errorMsg += " - " + errorBody;
                        }
                    } catch (IOException e) {
                        Log.e("PasswordUpdate", "Failed to read error body", e);
                    }
                    Log.e("PasswordUpdate", errorMsg);
                    callBack.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AppUser> call, Throwable t) {
                String errorMsg = "API call failed: " + t.getMessage();
                Log.e("PasswordUpdate", "Network error", t);
                callBack.onFailure(errorMsg);
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

    public void checkUserExists(String email, CheckUserCallback callback) {
        if (userExists(email)) {
            callback.onUserExists();
        } else {
            callback.onUserNotFound();
        }
    }

    private boolean userExists(String email) {
        Call<Boolean> call = authService.checkUserExists(email);
        try {
            Response<Boolean> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface CheckUserCallback {
        void onUserExists();
        void onUserNotFound();
        void onFailure(String errorMessage);
    }

    public void getUserDetails(Integer id, GetDetailsCallBack callBack){
        Call<UserDetails> call = authService.getUserDetails(id);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.code() == 200){
                    callBack.onSuccess(response.body());
                }
                else {
                    Log.e("__FAILED:", response.raw().toString());
                }
            }
            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void updateUserDetails(Integer uID, UserDetails details, UpdateDetailsCallBack callBack){
        Call<UserDetails> call = authService.updateUserDetails(uID,details);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.code() == 200){
                    callBack.onSuccess(response.body());
                }
                else {
                    Log.e("__FAILED:", response.raw().toString());
                }
            }
            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void uploadProfileImage(int id, File imageFile, UploadImageCallBack callBack) {
        RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", imageFile.getName(), requestFile);

        Call<ResponseBody> call = authService.uploadImage(id, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        callBack.onSuccess("Image uploaded successfully.");
                    } catch (Exception e) {
                        callBack.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    String errorMessage = "Image upload failed: " + response.code() + " - " + response.message();
                    Log.e("__UPLOAD_FAILED:", ""+response.code());
                    callBack.onFailure(errorMessage);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = "Image upload failed: " + t.getMessage();
                Log.e("__ERROR:", errorMessage);
                callBack.onFailure(errorMessage);
            }
        });
    }

    public void fetchProfileImage(int userId, FetchImageCallBack callBack) {
        Call<ResponseBody> call = authService.getProfileImage(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        callBack.onSuccess(bitmap); // Pass the bitmap to the callback
                    } catch (Exception e) {
                        callBack.onFailure("Error processing image: " + e.getMessage());
                    }
                } else {
                    callBack.onFailure("Failed to fetch image: " + response.code() + " - " + response.message());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    /*public void fetchMostRecentPotholeDate(AppUser user, FetchMostRecentPotholeDateCallback callback) {
        PotholeManager.getInstance().getPotholes(user, new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                Date mostRecentDate = null;
                for (Pothole pothole : potholes) {
                    if (mostRecentDate == null || pothole.getDateFound().after(mostRecentDate)) {
                        mostRecentDate = new Date(pothole.getDateFound().getTime());
                    }
                }
                user.setMostRecentPotholeDate(new java.sql.Date(mostRecentDate.getTime()));
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    public interface FetchMostRecentPotholeDateCallback {
        void onSuccess(AppUser user);
        void onFailure(String errorMessage);
    }*/

    public interface GetUserCallBack{
        void onSuccess(AppUser user);
        void onFailure(String errorMessage);
    }

    public interface FetchImageCallBack {
        void onSuccess(Bitmap bitmap);
        void onFailure(String errorMessage);
    }

    public interface UploadImageCallBack {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface UpdateDetailsCallBack{
        void onSuccess(UserDetails details);
        void onFailure(String errorMessage);
    }

    public interface GetDetailsCallBack{
        void onSuccess(UserDetails details);
        void onFailure(String errorMessage);
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
