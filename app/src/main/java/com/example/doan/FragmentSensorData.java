package com.example.doan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class FragmentSensorData extends Fragment {

    private TextView tvdt_mean, tvdt_sd, tvdt_phFound;

    public FragmentSensorData() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_sensor_data, container, false);

        tvdt_mean = rootView.findViewById(R.id.tvdt_meanZ);
        tvdt_sd = rootView.findViewById(R.id.tvdt_sdZ);
        tvdt_phFound = rootView.findViewById(R.id.tvdt_potholeFound);
        return rootView;
    }

    public void updateFound(Integer number){
        if (tvdt_phFound!=null){
            tvdt_phFound.setText(number.toString());
        }
    }

    public void updateMeanValue(String meanValue) {
        if (tvdt_mean != null) {
            tvdt_mean.setText(meanValue);
        }
    }

    // Method to update standard deviation value
    public void updateSdValue(String sdValue) {
        if (tvdt_sd != null) {
            tvdt_sd.setText(sdValue);
        }
    }

}
