package com.example.doan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.model.AppUser;
import java.util.List;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.UserDetailHolder> {

    private List<AppUser> userDetailList;
    private Context context;

    public UserDetailAdapter(List<AppUser> userDetailList, Context context) {
        this.userDetailList = userDetailList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailHolder holder, int position) {
        AppUser user = userDetailList.get(position);
        holder.userName.setText(user.getUsername());
        holder.txtDate.setText(user.getMostRecentPotholeDate() != null ? user.getMostRecentPotholeDate().toString() : "No data");
    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    public class UserDetailHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView txtDate;

        public UserDetailHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.txt_username);
            txtDate = itemView.findViewById(R.id.txt_date);
        }
    }
}