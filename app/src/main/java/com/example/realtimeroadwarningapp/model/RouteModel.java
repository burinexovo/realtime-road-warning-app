package com.example.realtimeroadwarningapp.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteModel {
    private String destinationAddress;
    private LatLng destinationLatLng;
    private String originAddress;
    private LatLng originLatLng;
    private List<LatLng> points;

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public void setOriginLatLng(LatLng originLatLng) {
        this.originLatLng = originLatLng;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public LatLng getDestinationLatLng() {
        return destinationLatLng;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public LatLng getOriginLatLng() {
        return originLatLng;
    }

    public List<LatLng> getPoints() {
        return points;
    }
}
