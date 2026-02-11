package com.example.realtimeroadwarningapp.ui.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.realtimeroadwarningapp.LocationListener;
import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.broadcast.NetworkChangeBroadcastReceiver;
import com.example.realtimeroadwarningapp.databinding.ActivityMainBinding;
import com.example.realtimeroadwarningapp.model.ProneAreaModel;
import com.example.realtimeroadwarningapp.service.FusedLocationService;
import com.example.realtimeroadwarningapp.utils.ConnectionDetectorUtil;
import com.example.realtimeroadwarningapp.utils.Constants;
import com.example.realtimeroadwarningapp.utils.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static MainActivity instance;
    private final String TAG = this.getClass().getSimpleName();
//    private final String GEOFENCE_ID = "SOME_GEOFENCE_ID";  // test
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.d(TAG, "onLocationResult: " + location.toString());
            }
        }
    };
    private GeofencingClient geofencingClient;  // add Geofence
    private GeofenceHelper geofenceHelper;
    private FusedLocationService fusedLocationService;
    private NetworkChangeBroadcastReceiver networkChangeBroadcastReceiver;

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_info, R.id.navigation_map, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Add geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
//        LatLng latLng = new LatLng(24.551915, 120.816187); // test
//        addGeofence(latLng, RADIUS_200);

        // Keep updating my location
        int priority = isSavePower() ? Priority.PRIORITY_LOW_POWER : Priority.PRIORITY_HIGH_ACCURACY;

        fusedLocationService = new FusedLocationService(this, priority, this);

        networkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver();
    }

    private boolean isSavePower() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.KEY_SAVE_POWER, false);
    }


    // invoke by MapsFragment.java add geofence
//    public void addGeofence(String id, LatLng latLng, float radius) {
//        Geofence geofence = geofenceHelper.getGeofence(id, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
//                | Geofence.GEOFENCE_TRANSITION_DWELL); //| Geofence.GEOFENCE_TRANSITION_EXIT
//        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
//        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//                .addOnSuccessListener(unused -> {
//                    Log.e(TAG, "addGeofence onSuccess: Added geofence");
//                })
//                .addOnFailureListener(e -> {
//                    String errorMessage = geofenceHelper.getErrorString(e);
//                    Log.e(TAG, "addGeofence onFailure: " + errorMessage);
//                });
//    }

    public void addGeofences(List<ProneAreaModel> proneAreaModels, float radius) {
        //| Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT
        List<Geofence> geofences = geofenceHelper.getGeofences(proneAreaModels, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequests(geofences);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(unused -> {
                    Log.e(TAG, "addGeofence onSuccess: Added geofence");
                })
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
                    Log.e(TAG, "addGeofence onFailure: " + errorMessage);
                });
    }

    // invoke by MapsFragment.java remove geofence
    public void removeGeofence() {
        Log.e(TAG, "removeGeofence: remove geofence");
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.removeGeofences(pendingIntent);
    }

    public void removeGeofence(List<String> geofenceRequestIds) {
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.removeGeofences(geofenceRequestIds);
    }

    public void setPowerPriority(boolean isSavePower) {
        if (isSavePower) {
            fusedLocationService.setLowPower();
        } else {
            fusedLocationService.setHighAccuracy();
        }
    }

    // Override Method
    @Override
    public void onLocationUpdated(Location location) {
        Log.e(TAG, "onLocationUpdated: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onLastLocationResult(Location location) {
        Log.e(TAG, "onLastLocationResult: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onCityChanged(String city) {
        Log.e(TAG, "onCityChanged: " + city);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeBroadcastReceiver, filter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: start location updates");
        fusedLocationService.startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        fusedLocationService.start();
        fusedLocationService.stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeBroadcastReceiver);
    }
}