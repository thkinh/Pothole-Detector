package com.example.doan;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentRoute extends Fragment {

    private ListAdapterRoute listAdapterRoute;
    private ArrayList<ListDataRoute> routeArrayList = new ArrayList<>();
    private ListDataRoute listDataRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        // Set up the route list
        int[] routeImageList = {R.drawable.img_pothole, R.drawable.img_pothole2, R.drawable.img_pothole};
        String[] routeNameList = {"Đặng Văn Cẩn", "Võ Văn Ngân", "Nguyễn Thị Thập"};
        String[] quantityList = {"6 potholes", "9 potholes", "3 potholes"};
        for (int i = 0; i < routeImageList.length; i++) {
            listDataRoute = new ListDataRoute(routeNameList[i], quantityList[i], routeImageList[i]);
            routeArrayList.add(listDataRoute);
        }
        listAdapterRoute = new ListAdapterRoute(getContext(), routeArrayList);
        ListView listViewRoute = view.findViewById(R.id.listroute);
        listViewRoute.setAdapter(listAdapterRoute);

        return view;
    }
}