package com.example.doan.model;

public class Pothole {
    private double longitude;
    private double lattitude;

    public double GetLongitude(){
        return longitude;
    }

    public double GetLattitude(){
        return lattitude;
    }

    public Pothole(double longitude, double lattitude){
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
}


