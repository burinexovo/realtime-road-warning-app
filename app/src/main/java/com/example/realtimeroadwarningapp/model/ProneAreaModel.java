package com.example.realtimeroadwarningapp.model;

import com.google.android.gms.maps.model.LatLng;

public class ProneAreaModel {
    private String id;
    private String city;
    private LatLng latLng;

    public ProneAreaModel(String id, String city, LatLng latLng) {
        this.id = id;
        this.city = city;
        this.latLng = latLng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
