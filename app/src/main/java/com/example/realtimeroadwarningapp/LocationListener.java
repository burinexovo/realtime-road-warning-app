package com.example.realtimeroadwarningapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public interface LocationListener {
    void onLocationUpdated(Location location);
    void onLastLocationResult(Location location);
    void onCityChanged(String city);
}
