package com.example.doan.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doan.R;

import java.util.ArrayList;

public class ListAdapterUser extends ArrayAdapter<ListDataUser> {
    public ListAdapterUser(@NonNull Context context, ArrayList<ListDataUser> dataArrayList) {
        super(context, R.layout.item_user, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListDataUser listData = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.img_avatar);
        TextView listName = view.findViewById(R.id.txt_username);
        //TextView listDate = view.findViewById(R.id.txt_date);

        listImage.setImageResource(listData.image);
        listName.setText(listData.name);
        //listDate.setText(listData.date);

        return view;
    }
}
