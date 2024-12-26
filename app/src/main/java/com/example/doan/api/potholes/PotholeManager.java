package com.example.doan.api.potholes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.doan.api.RetrofitInstance;
import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeManager {
    private static PotholeManager instance;
    private final PotholeService potholeService;

    private PotholeManager(){
        this.potholeService = RetrofitInstance.getInstance().create(PotholeService.class);
    }

    public static synchronized PotholeManager getInstance() {
        if (instance == null) {
            instance = new PotholeManager();
        }
        return instance;
    }
    public void getPotholes(AppUser user, GetPotholeCallBack callBack){
        Call<List<Pothole>> call =  potholeService.getPotholes(user.getUsername());
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null){
                    Log.d("HTTP_Response", "200");
                    callBack.onSuccess(response.body());
                }
                else if (response.body() == null){
                    Log.e("HTTP_Response", "null body");
                }
                else {
                    Log.d("HTTP_Response", response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                callBack.onFailure("API call failed: "+ t.getMessage());
            }
        });
    }


    public void getALLPotholes(GetPotholeCallBack callBack){
        Call<List<Pothole>> call = potholeService.getALLPotholes();
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null){
                    callBack.onSuccess(response.body());
                } else {
                    callBack.onFailure("Failed to fetch potholes: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                callBack.onFailure("API call failed: " + t.getMessage());
            }
        });
    }


    public void addPothole(Pothole pothole, AddPotholeCallBack callBack) {
        Call<Pothole> call = potholeService.addPothole(pothole);
        call.enqueue(new Callback<Pothole>() {
            @Override
            public void onResponse(Call<Pothole> call, Response<Pothole> response) {
                if (response.code() == 200) {
                    callBack.onSuccess(response.body());
                }
                else {
                    Log.e("__Failed",response.errorBody().toString());
                    Log.e("__Failed",""+response.code());
                    callBack.onFailure(response.message());
                }
            }
            @Override
            public void onFailure(Call<Pothole> call, Throwable t) {
                callBack.onFailure(t.getMessage());
            }
        });
    }

    public void getPotholeByLocation(Double latitude, Double longitude, FetchPotholeCallBack callback) {
        Call<Pothole> call = potholeService.findByLocation(latitude, longitude);
        call.enqueue(new Callback<Pothole>() {
            @Override
            public void onResponse(Call<Pothole> call, Response<Pothole> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error fetching pothole: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Pothole> call, Throwable t) {
                callback.onFailure("API call failed: " + t.getMessage());
            }
        });
    }

    public void uploadProtholeImage(int id, File imageFile, AuthManager.UploadImageCallBack callBack) {
        RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", imageFile.getName(), requestFile);
        Call<ResponseBody> call = potholeService.uploadPotholeImage(id, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse response or handle as needed
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

    public void fetchProfileImage(int userId, AuthManager.FetchImageCallBack callBack) {
        Call<ResponseBody> call = potholeService.getPotholeImage(userId);
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

    public interface FetchPotholeCallBack{
        void onSuccess(Pothole potholes);
        void onFailure(String errorMessage);
    }

    public interface GetPotholeCallBack{
        void onSuccess(List<Pothole> potholes);
        void onFailure(String errorMessage);
    }

    public interface AddPotholeCallBack{
        void onSuccess(Pothole pothole);
        void onFailure(String errorMessage);
    }

}
