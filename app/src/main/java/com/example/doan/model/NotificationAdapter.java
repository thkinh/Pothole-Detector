package com.example.doan.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doan.R;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private List<String> notifications;

    public NotificationAdapter(Context context, List<String> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        }

        TextView notificationText = convertView.findViewById(R.id.notification_title);
        String notification = notifications.get(position);
        Log.d("NotificationAdapter", "Binding notification: " + notification);
        notificationText.setText(notification);

        return convertView;
    }

    public void updateData(List<String> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }
}