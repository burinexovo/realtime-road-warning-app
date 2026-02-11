package com.example.realtimeroadwarningapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.realtimeroadwarningapp.ui.activities.MainActivity;
import com.example.realtimeroadwarningapp.utils.NotificationUtil;
import com.example.realtimeroadwarningapp.utils.TextToSpeechUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationUtil notificationUtil = new NotificationUtil(context);
        TextToSpeechUtil textToSpeechUtil = new TextToSpeechUtil(context);

        if (intent == null) {
            Log.e(TAG, "onReceive: intent is null");
        } else {
            Log.e(TAG, "onReceive: intent" + intent);
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent == null) {
            Log.e(TAG, "onReceive: geofencingEvent is null");
            return;
        } else {
            Log.e(TAG, "onReceive: geofencingEvent in not null");
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        if (geofenceList != null) {
            Log.e(TAG, "onReceive: geofenceList.size()" + geofenceList.size());
            for (Geofence geofence : geofenceList) {
                Log.e(TAG, "onReceive: " + geofence.getRequestId());
            }
        }

        // Trigger point
        Location location = geofencingEvent.getTriggeringLocation();

        String street = getGeoLocate(new LatLng(location.getLatitude(), location.getLongitude()), context);
        Log.e(TAG, "onReceive: " + street);


        int transitionType = geofencingEvent.getGeofenceTransition();
        Log.e(TAG, "onReceive: transitionType " + transitionType);

        //
//        String body = "前方兩百公尺" + street + "為易肇事路段請小心行駛";
        String body = "前方兩百公尺" + "中正路" + "為易肇事路段請小心行駛";

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
//                notificationUtil.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MainActivity.class);
                notificationUtil.sendHighPriorityNotification("警告", body, MainActivity.class);
                textToSpeechUtil.speak(body);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                notificationUtil.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MainActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
//                notificationUtil.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MainActivity.class);
                break;
        }


    }

    private String getGeoLocate(LatLng latLng, Context context) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate IOException: " + e.getLocalizedMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            String street = address.getThoroughfare();
            Log.d(TAG, "getGeoLocateCity: " + street);
            return street;
        }
        return "";
    }


}