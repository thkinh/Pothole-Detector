package com.example.doan;
public class FirestoreClass {
    public static class User {
        public String id;
        public String name;
        public String email;
        public String password;

        // No-argument constructor required for Firestore deserialization
        public User() {}

        public User(String i, String n, String e, String pass) {
            this.id = i;
            this.name = n;
            this.email = e;
            this.password = pass;
        }
    }

    public static class Pothole{
        Double Latitude;
        Double Longitude;
        String Region;

        public Pothole(Double latitude, Double longitude, String region) {
            Latitude = latitude;
            Longitude = longitude;
            Region = region;
        }
        public String getRegion() {
            return Region;
        }

        public Double getLongitude() {
            return Longitude;
        }

        public Double getLatitude() {
            return Latitude;
        }
    }

}
