package com.example.doan.feature;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.doan.model.AppUser;
import com.google.gson.Gson;

public class UserPreferences {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_APP_USER = "AppUser";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Save AppUser object
    public void saveUser(AppUser user) {
        String userJson = gson.toJson(user); // Convert user object to JSON string
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_APP_USER, userJson);
        editor.apply();
    }

    // Retrieve AppUser object
    public AppUser getUser() {
        String userJson = sharedPreferences.getString(KEY_APP_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, AppUser.class); // Convert JSON string back to object
        }
        return null; // No user saved
    }

    // Clear user data (e.g., during logout)
    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_APP_USER); // Remove user entry
        editor.apply();
    }
}
