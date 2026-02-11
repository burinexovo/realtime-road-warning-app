package com.example.realtimeroadwarningapp.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.realtimeroadwarningapp.LocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FusedLocationService {
    private final String TAG = this.getClass().getSimpleName();

    private final Context context;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationRequest locationRequest;
    private final LocationListener locationListener;

    private final LocationCallback locationCallback = new LocationCallback() {
        String cityTemp = "";

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                locationListener.onLocationUpdated(location);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String city = getGeoLocateCity(latLng);
                if (!cityTemp.equals(city)) {
                    locationListener.onCityChanged(city);
                }
                cityTemp = city;
            }
        }
    };

    public FusedLocationService(Context context, int priority, LocationListener locationListener) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(8000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(priority);
        this.context = context;
        this.locationListener = locationListener;
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                // have a location
//                myLocation = location;
                Log.e(TAG, "getLastLocation onSuccess location: " + location);
                Log.e(TAG, "getLastLocation onSuccess getLongitude: " + location.getLongitude());
                Log.e(TAG, "getLastLocation onSuccess getLatitude: " + location.getLatitude());

                locationListener.onLastLocationResult(location);
            } else {
                Log.e(TAG, "getLastLocation onSuccess: location was null");
            }
        });

        locationTask.addOnFailureListener(e -> {
            Log.e(TAG, "getLastLocation onFailure: " + e.getLocalizedMessage());
        });
    }

    // location to city
    private String getGeoLocateCity(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate IOException: " + e.getLocalizedMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            String city = address.getAddressLine(0).substring(5, 8);
            Log.d(TAG, "getGeoLocateCity: " + city);
            return city;
        }
        return "";
    }

    // check settings and start location updates
    public void start() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            // start location updates
            Log.e(TAG, "checkSettingsAndStartLocationUpdates onSuccess");
            startLocationUpdates();
        });

        locationSettingsResponseTask.addOnFailureListener(e -> {
            Log.e(TAG, "checkSettingsAndStartLocationUpdates onFailure: " + e.getLocalizedMessage());
        });
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e(TAG, "startLocationUpdates");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        Log.e(TAG, "stopLocationUpdates");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void setLowPower() {
        stopLocationUpdates();
        Log.e(TAG, "setPowerPriority location priority: PRIORITY_LOW_POWER");
        locationRequest.setPriority(Priority.PRIORITY_LOW_POWER);
        startLocationUpdates();
    }

    public void setHighAccuracy() {
        stopLocationUpdates();
        Log.e(TAG, "setPowerPriority location priority: PRIORITY_HIGH_ACCURACY");
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }
}
