package com.example.doan.feature;

import android.hardware.SensorManager;

import com.example.doan.model.AppUser;

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


    private static Setting instance;
    private Setting(){
        weeklyReport = false;
        alertPreferences = false;
        isContributor = false;
        appLanguage = AppLanguage.vi;
        sensitivity = Sensitivity.LOW;
    };

    public static synchronized Setting getInstance(){
        if (instance == null){
            instance = new Setting();
        }
        return instance;
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
}
