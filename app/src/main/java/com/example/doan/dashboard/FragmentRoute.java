package com.example.doan.dashboard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doan.R;
import com.example.doan.adapter.RouteAdapter;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.Pothole;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class FragmentRoute extends Fragment {

    private RouteAdapter routeAdapter;
    private List<Pothole.Location> locationList = new ArrayList<>();
    private HashSet<String> streetSet = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.listroute);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        routeAdapter = new RouteAdapter(getContext(), locationList);
        recyclerView.setAdapter(routeAdapter);

        // Fetch data and update the list
        fetchPotholeData();

        return view;
    }

    private void fetchPotholeData() {
        PotholeManager.getInstance().getALLPotholes(new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                for (Pothole pothole : potholes) {
                    String street = pothole.getLocation().getStreet();
                    if (!streetSet.contains(street)) {
                        streetSet.add(street);
                        locationList.add(pothole.getLocation());
                    }
                }
                routeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Log the error message
                Log.e("FragmentRoute", "Failed to fetch pothole data: " + errorMessage);

                // Show a toast message to the user
                Toast.makeText(getContext(), "Failed to load data. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
}