package com.example.doan.model;

import java.sql.Date;
import java.sql.Time;

public class Pothole {


    private Integer id;

//    private Date dateFound;
//    private Time timeFound;
    private String severity;

    private Location location;


    private AppUser appUser;

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

//    public Date getDateFound() {
//        return dateFound;
//    }
//
//    public void setDateFound(Date dateFound) {
//        this.dateFound = dateFound;
//    }
//
//    public Time getTimeFound() {
//        return timeFound;
//    }
//
//    public void setTimeFound(Time timeFound) {
//        this.timeFound = timeFound;
//    }

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
    }
}