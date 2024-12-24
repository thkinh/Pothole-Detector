package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.Pothole;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private Context context;
    private List<Pothole.Location> locations;

    public RouteAdapter(Context context, List<Pothole.Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Pothole.Location location = locations.get(position);
        holder.txtRouteName.setText(location.getStreet());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView txtRouteName;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRouteName = itemView.findViewById(R.id.txt_routename);
        }
    }
}
