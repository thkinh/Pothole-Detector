package com.example.doan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.doan.R;

public class RouteFragment extends Fragment {

    private TextView txtDistance;
    private TextView txtTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_details, container, false);
        txtDistance = view.findViewById(R.id.txt_distance);
        txtTime = view.findViewById(R.id.txt_time);
        return view;
    }

    public void updateRouteInfo(String distance, String time) {
        txtDistance.setText(distance);
        txtTime.setText(time);
    }
}