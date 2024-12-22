package com.example.doan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.doan.model.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ListView notificationListView;
    private NotificationAdapter adapterNotification;
    private List<String> notificationList;
    private List<String> filteredNotificationList;
    private Button tabAll, tabUnread;
    private LinearLayout tabContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_notification);

        notificationListView = findViewById(R.id.notification_list);
        tabAll = findViewById(R.id.tab_all);
        tabUnread = findViewById(R.id.tab_unread);
        tabContainer = findViewById(R.id.tab_container);
        ImageView backButton = findViewById(R.id.img_back);

        notificationList = new ArrayList<>();
        notificationList.add("Phát hiện ổ gà tại vị trí: 21.0285, 105.8542 vào lúc 21/12/2024 16:05");
        notificationList.add("Chưa đọc: Phát hiện ổ gà tại vị trí: 23.0285, 105.8542 vào lúc 21/12/2024 16:05");
        notificationList.add("Phát hiện ổ gà tại vị trí: 25.0285, 105.8542 vào lúc 21/12/2024 16:05");
        notificationList.add("Chưa đọc: Phát hiện ổ gà tại vị trí: 27.0285, 105.8542 vào lúc 21/12/2024 16:05");
        notificationList.add("Phát hiện ổ gà tại vị trí: 29.0285, 105.8542 vào lúc 21/12/2024 16:05");

        filteredNotificationList = new ArrayList<>(notificationList);
        adapterNotification = new NotificationAdapter(this, filteredNotificationList);
        notificationListView.setAdapter(adapterNotification);

        tabAll.setSelected(true);
        tabAll.setOnClickListener(v -> {
            setSelectedTab(true);
            updateNotifications(false);
        });

        tabUnread.setOnClickListener(v -> {
            setSelectedTab(false);
            updateNotifications(true);
        });

        backButton.setOnClickListener(v -> onBackPressed());

        notificationListView.setOnItemClickListener((parent, view, position, id) -> markNotificationAsRead(position));
    }

    private void updateNotifications(boolean showUnread) {
        filteredNotificationList.clear();
        for (String notification : notificationList) {
            if (!showUnread || notification.contains("Chưa đọc")) {
                filteredNotificationList.add(notification);
            }
        }
        adapterNotification.updateData(filteredNotificationList);
    }

    private void markNotificationAsRead(int position) {
        String notification = filteredNotificationList.get(position);
        if (notification.contains("Chưa đọc: ")) {
            notification = notification.replace("Chưa đọc: ", "");
            int originalIndex = notificationList.indexOf(filteredNotificationList.get(position));
            notificationList.set(originalIndex, notification);
            filteredNotificationList.set(position, notification);
            adapterNotification.notifyDataSetChanged();
        }
    }

    private void setSelectedTab(boolean isAllSelected) {
        tabAll.setSelected(isAllSelected);
        tabUnread.setSelected(!isAllSelected);

        int selectedTabColor = ContextCompat.getColor(this, R.color.selected_tab_color);
        int unselectedTabColor = ContextCompat.getColor(this, R.color.unselected_tab_color);

        tabAll.setBackgroundColor(isAllSelected ? selectedTabColor : unselectedTabColor);
        tabUnread.setBackgroundColor(isAllSelected ? unselectedTabColor : selectedTabColor);
    }
}