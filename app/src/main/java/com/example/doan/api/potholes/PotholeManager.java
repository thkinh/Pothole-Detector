package com.example.doan.api.potholes;

import android.util.Log;

import com.example.doan.api.RetrofitInstance;
import com.example.doan.model.AppUser;
import com.example.doan.model.Pothole;

import java.io.IOException;
import java.util.List;
import com.example.doan.api.potholes.*;
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
                if (response.isSuccessful()){
                    callBack.onSuccess(response.body());
                }
                else {
                    callBack.onFailure(response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                callBack.onFailure(t.getMessage());
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


    public interface GetPotholeCallBack{
        void onSuccess(List<Pothole> potholes);
        void onFailure(String errorMessage);
    }

    public interface AddPotholeCallBack{
        void onSuccess(Pothole pothole);
        void onFailure(String errorMessage);
    }

}
