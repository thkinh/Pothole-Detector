package com.example.doan.feature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Build;

import com.example.doan.model.UserDetails;
import com.google.gson.Gson;

import java.util.Locale;

public class Setting {
    public enum AppLanguage{
        en_US,
        vi
    }
    public enum Sensitivity{
        HIGH,
        MEDIUM,
        LOW
    }

    private boolean weeklyReport;
    private boolean alertPreferences;
    private boolean isContributor;
    private AppLanguage appLanguage;
    private Sensitivity sensitivity;
    private Double sensitiveConstance;
    private UserDetails userDetails;

    private static Setting instance;
    private Setting(){
        weeklyReport = false;
        alertPreferences = false;
        isContributor = false;
        appLanguage = AppLanguage.vi;
        sensitivity = Sensitivity.LOW;
        sensitiveConstance = 0.9055; //LOW
    };

    public void setSensitiveConstance(Double sensitiveConstance) {
        this.sensitiveConstance = sensitiveConstance;
    }

    public Double getSensitiveConstance() {
        return sensitiveConstance;
    }

    public static synchronized Setting getInstance(){
        if (instance == null){
            instance = new Setting();
        }
        return instance;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public int getSensitivity(){
        if (sensitivity == Sensitivity.LOW){
            return SensorManager.SENSOR_DELAY_NORMAL;
        }
        if (sensitivity == Sensitivity.HIGH){
            return SensorManager.SENSOR_DELAY_FASTEST;
        }
        if (sensitivity == Sensitivity.MEDIUM){
            return SensorManager.SENSOR_DELAY_GAME;
        }
        return -1;
    }

    public void setSensitivity(Sensitivity sensitivity) {
        this.sensitivity = sensitivity;
    }

    public AppLanguage getAppLanguage(){
        return appLanguage;
    }

    public void setAppLanguage(AppLanguage appLanguage) {
        this.appLanguage = appLanguage;
    }

    public boolean getWeeklyReport(){
        return weeklyReport;
    }

    public boolean getAlert(){
        return alertPreferences;
    }

    public boolean getIsContributor(){
        return isContributor;
    }

    public void setAlertPreferences(boolean set){
        alertPreferences = set;
    }

    public void setWeeklyReport(boolean weeklyReport){
        this.weeklyReport = weeklyReport;
    }

    public void setContributor(boolean contributor) {
        isContributor = contributor;
    }

    public void saveToPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert Setting instance to JSON
        Gson gson = new Gson();
        String json = gson.toJson(this);
        editor.putString("settings", json);
        editor.apply();
    }

    // Load settings from SharedPreferences
    public static void loadFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("settings", null);
        if (json != null) {
            Gson gson = new Gson();
            instance = gson.fromJson(json, Setting.class);
        } else {
            instance = new Setting();
        }
    }

    public void applyLanguage(Context context) {

        Locale locale;
        if (this.getAppLanguage() == AppLanguage.en_US){
            locale = Locale.US;
        }
        else {
            locale = new Locale("vi", "Vietnam");
        }
        // Set the language based on the user's selected preference
        Locale.setDefault(locale);

        // Get the resources and configuration
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        // Check Android version and apply the appropriate configuration method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For Android N (API 24) and above, use createConfigurationContext
            config.setLocale(locale);
            context.createConfigurationContext(config);  // Ensure the context is updated
        } else {
            // For versions below API 24, use the deprecated method
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        // Restart the activity to apply the language change
        restartActivity(context);
    }

    private void restartActivity(Context context) {
        Intent intent = ((Activity) context).getIntent();
        ((Activity) context).finish();
        context.startActivity(intent);
    }

}
