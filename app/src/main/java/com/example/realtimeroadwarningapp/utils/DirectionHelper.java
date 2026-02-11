package com.example.realtimeroadwarningapp.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.realtimeroadwarningapp.BuildConfig;
import com.example.realtimeroadwarningapp.DirectionInterface;
import com.example.realtimeroadwarningapp.model.RouteModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DirectionHelper {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String GOOGLE_API_KEY = BuildConfig.MAPS_API_KEY;
    private final String TAG = this.getClass().getSimpleName();
    private final DirectionInterface directionInterface;
    private final String origin;
    private final String destination;
    private final String avoid;

    public DirectionHelper(DirectionInterface directionInterface, String origin, String destination, String avoid) {
        this.directionInterface = directionInterface;
        this.origin = origin;
        this.destination = destination;
        this.avoid = avoid;
    }

    public void execute() {
        directionInterface.onDirectionsStart();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(createUrl())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e(TAG, "onResponse: " + result);
                    parseJSON(result);
                }
            }
        });
    }

    private HttpUrl createUrl() {
//        String url;
        HttpUrl url = null;
        if (avoid.equals("highways")) {
            url = HttpUrl.parse(DIRECTION_URL_API).newBuilder()
                    .addQueryParameter("origin", origin)
                    .addQueryParameter("destination", destination)
                    .addQueryParameter("avoid", "highways")
                    .addQueryParameter("mode", "driving")
                    .addQueryParameter("key", GOOGLE_API_KEY)
                    .build();
//            Log.e(TAG, "createUrl: avoid highways");
//            url = Uri.parse(DIRECTION_URL_API)
//                    .buildUpon()
//                    .appendQueryParameter("origin", origin)
//                    .appendQueryParameter("destination", destination)
//                    .appendQueryParameter("avoid", "highways")
//                    .appendQueryParameter("mode", "driving")
//                    .appendQueryParameter("key", GOOGLE_API_KEY)
//                    .toString();
        } else {
            url = HttpUrl.parse(DIRECTION_URL_API).newBuilder()
                    .addQueryParameter("origin", origin)
                    .addQueryParameter("destination", destination)
                    .addQueryParameter("mode", "driving")
                    .addQueryParameter("key", GOOGLE_API_KEY)
                    .build();
//            Log.e(TAG, "createUrl: don't avoid highways");
//            url = Uri.parse(DIRECTION_URL_API)
//                    .buildUpon()
//                    .appendQueryParameter("origin", origin)
//                    .appendQueryParameter("destination", destination)
//                    .appendQueryParameter("mode", "driving")
//                    .appendQueryParameter("key", GOOGLE_API_KEY)
//                    .toString();
        }


        Log.e(TAG, "createUrl: " + url);
//                .appendQueryParameter("destination", "24.551915, 120.816187")
//                .appendQueryParameter("origin", "24.542837, 120.836629")
        return url;
    }

    private void parseJSON(String result) {
        if (result == null) return;
        List<RouteModel> routeModels = new ArrayList<>();
        String distance = "";
        String duration = "";
        try {
            JSONObject jsonData = new JSONObject(result);
            JSONArray jsonRoutes = jsonData.getJSONArray("routes");
            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                RouteModel routeModel = new RouteModel();

                JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                duration = jsonDuration.getString("text");
                distance = jsonDistance.getString("text");
                routeModel.setDestinationAddress(jsonLeg.getString("end_address"));
                routeModel.setDestinationLatLng(new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")));
                routeModel.setOriginAddress(jsonLeg.getString("start_address"));
                routeModel.setOriginLatLng(new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng")));
                routeModel.setPoints(decodePolyLine(overview_polylineJson.getString("points")));

                routeModels.add(routeModel);
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: " + e.getMessage());
        }
        directionInterface.onDirectionsSuccess(routeModels, duration, distance);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}