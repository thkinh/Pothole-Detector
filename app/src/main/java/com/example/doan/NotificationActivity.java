package com.example.doan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.doan.api.auth.AuthManager;
import com.example.doan.model.NotificationAdapter;
import com.example.doan.model.Pothole;
import com.example.doan.model.AppUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
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

        Log.d(TAG, "Calling fetchPotholes");
        fetchPotholes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotificationStates();
    }

    private void saveNotificationStates() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < notificationList.size(); i++) {
            String notification = notificationList.get(i);
            boolean isRead = !notification.contains("Chưa đọc: ");
            editor.putBoolean("notification_" + i, isRead);
        }
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationStates();
    }

    private void loadNotificationStates() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        for (int i = 0; i < notificationList.size(); i++) {
            boolean isRead = sharedPreferences.getBoolean("notification_" + i, false);
            if (isRead) {
                String notification = notificationList.get(i).replace("Chưa đọc: ", "");
                notificationList.set(i, notification);
            }
        }
        updateNotifications(tabUnread.isSelected());
    }

    private void fetchPotholes() {
        Log.d(TAG, "fetchPotholes called");
        AppUser currentUser = AuthManager.getInstance().getAccount();
        if (currentUser != null && currentUser.getUsername() != null) {
            String username = currentUser.getUsername();
            ApiClient.getPotholes(username, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "API Success: " + response);
                    List<Pothole> potholes = Pothole.parsePotholes(response);
                    notificationList.clear();
                    for (Pothole pothole : potholes) {
                        String message = "Chưa đọc: Pothole detected at " + pothole.getLocation().getCity() +
                                " (Lat: " + pothole.getLocation().getLatitude() +
                                ", Long: " + pothole.getLocation().getLongitude() +
                                ") on " + pothole.getDateFound() + " at " + pothole.getTimeFound() +
                                " with severity " + pothole.getSeverity();
                        notificationList.add(message);
                    }
                    runOnUiThread(() -> updateNotifications(tabUnread.isSelected()));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "API Error: " + e.getMessage());
                }
            });
        } else {
            Log.e(TAG, "Current user is null or username is not available");
        }
    }

    private void updateNotifications(boolean showUnread) {
        Log.d(TAG, "Updating notifications, showUnread: " + showUnread);
        filteredNotificationList.clear();
        for (String notification : notificationList) {
            if (!showUnread || notification.contains("Chưa đọc")) {
                filteredNotificationList.add(notification);
            }
        }
        Log.d(TAG, "Filtered notifications size: " + filteredNotificationList.size());
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