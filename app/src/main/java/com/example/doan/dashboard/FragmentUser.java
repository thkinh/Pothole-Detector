package com.example.doan.dashboard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doan.R;
import com.example.doan.adapter.UserDetailAdapter;
import com.example.doan.model.UserDetail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FragmentUser extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<UserDetail> userDetailList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.listuser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestJsonData();
        return view;
    }

    public void requestJsonData() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = "http://34.42.117.215:8080/api/user/get/ALL";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    fetchTheData(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Error parsing JSON data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("API call error");
            }
        });
        requestQueue.add(stringRequest);
    }

    private void fetchTheData(JSONArray jsonArray) {
        userDetailList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject user = jsonArray.getJSONObject(i);
                userDetailList.add(new UserDetail(user.getString("username")));
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Error parsing JSON data");
            }
        }
        UserDetailAdapter adapter = new UserDetailAdapter(userDetailList, getContext());
        recyclerView.setAdapter(adapter);
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}