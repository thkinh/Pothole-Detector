package com.example.doan.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Pothole {

    private Integer id;
    private Date dateFound;
    private String timeFound;
    private String severity;
    private Location location;
    private AppUser appUser;
    private Integer userId;

    public Pothole() {};

    public Pothole( Date dateFound, String severity, Location location, AppUser appUser, Integer userId) {
        this.dateFound = dateFound;
        this.severity = severity;
        this.location = location;
        this.appUser = appUser;
        this.userId = userId;
    }

    public Pothole(String normal, String none, Location location, AppUser account, int userId) {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateFound() {
        return dateFound;
    }

    public void setDateFound(Date dateFound) {
        this.dateFound = dateFound;
   }

    public String getTimeFound() {
        return timeFound;
    }

    public void setTimeFound(String timeFound) {
        this.timeFound = timeFound;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static class Location {
        private Double latitude;
        private Double longitude;
        private String country;
        private String city;
        private String street;
        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public static List<Pothole> parsePotholes(String response) {
        List<Pothole> potholes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Pothole pothole = new Pothole();
                pothole.setId(jsonObject.getInt("id"));
                pothole.setDateFound(Date.valueOf(jsonObject.getString("dateFound")));
                pothole.setTimeFound(jsonObject.getString("timeFound"));
                pothole.setSeverity(jsonObject.getString("severity"));
                JSONObject locationObject = jsonObject.getJSONObject("location");
                Location location = new Location();
                location.setLatitude(locationObject.getDouble("latitude"));
                location.setLongitude(locationObject.getDouble("longitude"));
                location.setCountry(locationObject.getString("country"));
                location.setCity(locationObject.getString("city"));
                pothole.setLocation(location);
                potholes.add(pothole);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return potholes;
    }
}