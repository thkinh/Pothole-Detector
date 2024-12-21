package com.example.doan.api.auth;

import android.util.Log;

import com.example.doan.api.RetrofitInstance;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeManager {
    private static PotholeManager instance;
    private final PotholeService potholeService;

    private PotholeManager(){
        this.potholeService = RetrofitInstance.getInstance().create(PotholeService.class);
    };

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
            }

            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                callBack.onFailure("API call failed: "+ t.getMessage());
            }
        });
    }



    public interface GetPotholeCallBack{
        void onSuccess(List<Pothole> potholes);
        void onFailure(String errorMessage);
    }
}
