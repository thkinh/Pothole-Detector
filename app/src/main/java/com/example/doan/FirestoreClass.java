package com.example.doan;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreClass{

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

