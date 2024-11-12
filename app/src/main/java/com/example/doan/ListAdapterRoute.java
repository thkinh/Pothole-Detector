package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapterRoute extends ArrayAdapter<ListDataRoute> {
    public ListAdapterRoute(@NonNull Context context, ArrayList<ListDataRoute> dataArrayList) {
        super(context, R.layout.item_route, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListDataRoute listData = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_route, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.img_pothole);
        TextView listName = view.findViewById(R.id.txt_routename);
        TextView listQuantity = view.findViewById(R.id.txt_quantity);

        listImage.setImageResource(listData.image);
        listName.setText(listData.name);
        listQuantity.setText(listData.quantity);

        return view;
    }
}
