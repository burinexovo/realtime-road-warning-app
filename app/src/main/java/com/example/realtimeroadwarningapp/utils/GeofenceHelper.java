package com.example.realtimeroadwarningapp.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import com.example.realtimeroadwarningapp.broadcast.GeofenceBroadcastReceiver;
import com.example.realtimeroadwarningapp.model.ProneAreaModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper extends ContextWrapper {

    private final String TAG = this.getClass().getSimpleName();
    private PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    // single point
    public GeofencingRequest getGeofencingRequest(Geofence geofence) {  // list < 100
        return new GeofencingRequest.Builder()
                .addGeofence(geofence) // Override two method, parameter 1 and more
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    // multi point
    public GeofencingRequest getGeofencingRequests(List<Geofence> geofences) {  // list < 100
        return new GeofencingRequest.Builder()
                .addGeofences(geofences)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    // single point
    public Geofence getGeofence(String id, LatLng latLng, float radius, int transitionType) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(id)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(5000) // test
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    // multi point
    public List<Geofence> getGeofences(List<ProneAreaModel> proneAreaModels, float radius, int transitionType) { //String id, LatLng latLng
        List<Geofence> geofences = new ArrayList<>();
        for (ProneAreaModel proneAreaModel : proneAreaModels) {
            LatLng latLng = proneAreaModel.getLatLng();
            Geofence geofence = new Geofence.Builder()
                    .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                    .setRequestId(proneAreaModel.getId())
                    .setTransitionTypes(transitionType)
                    .setLoiteringDelay(5000) // test
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();
            geofences.add(geofence);
        }
        return geofences;
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            pendingIntent = PendingIntent.getBroadcast
//                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
//        } else {
//            pendingIntent = PendingIntent.getBroadcast
//                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        }

        return pendingIntent;
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES"; // limit 100
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
                case GeofenceStatusCodes
                        .GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION:
                    return "GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION";
            }
        }
        return e.getLocalizedMessage();
    }
}
