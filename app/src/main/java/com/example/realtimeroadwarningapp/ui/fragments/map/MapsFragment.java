package com.example.realtimeroadwarningapp.ui.fragments.map;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimeroadwarningapp.BuildConfig;
import com.example.realtimeroadwarningapp.DirectionInterface;
import com.example.realtimeroadwarningapp.LocationListener;
import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.RecyclerViewClickInterface;
import com.example.realtimeroadwarningapp.adapter.CommonAddressAdapter;
import com.example.realtimeroadwarningapp.databinding.FragmentMapsBinding;
import com.example.realtimeroadwarningapp.model.CommonAddressModel;
import com.example.realtimeroadwarningapp.model.ProneAreaModel;
import com.example.realtimeroadwarningapp.model.RouteModel;
import com.example.realtimeroadwarningapp.service.FusedLocationService;
import com.example.realtimeroadwarningapp.ui.activities.MainActivity;
import com.example.realtimeroadwarningapp.utils.Constants;
import com.example.realtimeroadwarningapp.utils.DirectionHelper;
import com.example.realtimeroadwarningapp.utils.cluster.AccidentClusterItem;
import com.example.realtimeroadwarningapp.utils.cluster.ClusterRenderer;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, RecyclerViewClickInterface,
        DirectionInterface, LocationListener {

    private static final float DEFAULT_ZOOM = 17;
    @SuppressLint("StaticFieldLeak")
    private static MapsFragment instance;
    private final String TAG = this.getClass().getSimpleName();
    private MapView mapView;
    private ImageView ivCommonAddress, ivMyLocation;
    private TextView tvDuration, tvDistance;
    private ConstraintLayout clDetail, clBottomTools;
    private Button btnDirection, btnNavigation;
    private Location myLocation;
    private LatLng targetLatLng = null;
    private String targetAddress = "";
    private Dialog commonAddressDialog;
    private List<CommonAddressModel> addressModels;
    private ClusterManager<AccidentClusterItem> clusterManager;
    private GoogleMap mMap;
    private FusedLocationService fusedLocationService;
    private Marker targetMarker;
    private AutocompleteSupportFragment autocompleteFragment;
    private List<ProneAreaModel> proneAreaModels;
    private List<Polyline> polylinePaths;
    private Boolean isFocusOnMyLocation = false;
    private String myCity;
    private int radius;

    public static MapsFragment getInstance() {
        return instance;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        FragmentMapsBinding binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        instance = this;

        ivCommonAddress = binding.ivCommonAddress;
        ivMyLocation = binding.ivMyLocation;
        btnDirection = binding.btnDirection;
        btnNavigation = binding.btnNavigation;
        tvDuration = binding.tvDuration;
        tvDistance = binding.tvDistance;
        clDetail = binding.clDetail;
        clBottomTools = binding.clBottomTools;

        // Map init
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        loadData();

        setListener();

        // Get my last location
        int priority = isSavePower() ? Priority.PRIORITY_LOW_POWER : Priority.PRIORITY_HIGH_ACCURACY;
        fusedLocationService = new FusedLocationService(requireContext(), priority, this);
        fusedLocationService.start();
        fusedLocationService.getLastLocation();

        initAutocompleteSupportFragment();
        return root;
    }

    // Method
    private void loadData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.KEY_COMMON_ADDRESS, null);
        Type type = new TypeToken<List<CommonAddressModel>>() {
        }.getType();
        addressModels = gson.fromJson(json, type);

        if (addressModels == null) addressModels = new ArrayList<>();

        radius = sharedPreferences.getInt(Constants.KEY_WARNING_DISTANCE, 200);
    }

    private boolean isSavePower() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.KEY_SAVE_POWER, false);
    }

    private String getCarType() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        int carType = sharedPreferences.getInt(Constants.KEY_DRIVING_TYPE, 0);
        if (carType == 0) {
            return "d";
        } else {
            return "l";
        }
    }

    private void initAutocompleteSupportFragment() {
        // Place Autocomplete
        String apiKey = getString(R.string.google_maps_API_key);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        }

        PlacesClient placesClient = Places.createClient(requireContext());

        // Initialize the AutocompleteSupportFragment
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        String hint = getString(R.string.search_bar);
        autocompleteFragment.setHint(hint);

        autocompleteFragment.setCountry("TW");
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(21, 121),
                new LatLng(25, 122)
        ));

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.e(TAG, "Place: " + place.getId() + ", " + place.getName() + ", " +
                        place.getLatLng() + ", " + place.getAddress());
                targetAddress = place.getAddress();
                targetLatLng = place.getLatLng();
                LatLng latLng = place.getLatLng();
                String address = place.getAddress();
                isFocusOnMyLocation = false;
                ivMyLocation.setImageResource(R.drawable.ic_round_gps);
                ivMyLocation.setBackgroundTintList(ContextCompat.
                        getColorStateList(requireContext(), R.color.white));
                Log.e(TAG, "onPlaceSelected animateCamera: " + latLng.toString());
                animateCamera(latLng, DEFAULT_ZOOM);
                addMarker(latLng, address);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        autocompleteFragment.requireView()
                .findViewById(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (targetMarker != null) targetMarker.remove();
                        if (targetLatLng != null) targetLatLng = null;
                        if (!targetAddress.isEmpty()) targetAddress = "";
                        autocompleteFragment.setText("");
                        clDetail.setVisibility(View.GONE);

                        if (polylinePaths != null) {
                            for (Polyline polyline : polylinePaths) {
                                polyline.remove();
                            }
                        }

                    }
                });

    }

    private void executeDirections(String origin, String destination, String avoid) {
        Log.e(TAG, "executeDirections avoid: " + avoid);
        new DirectionHelper(this, origin, destination, avoid).execute();
    }

    private void startNavigation(String destination, String carType) {
        try {
            Uri uri = Uri.parse("google.navigation:" + "q=" + destination + "&mode=" + carType);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "無法連線至 Google 地圖，請確認您的 Google 地圖是否安裝", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void animateCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void addProneAreaMarker(List<ProneAreaModel> proneAreaModels) {
        if (proneAreaModels == null) {
            return;
        } else {
            clusterManager.clearItems();
            Log.e(TAG, "addProneAreaMarker: " + "clear items");
        }
        for (ProneAreaModel proneAreaModel : proneAreaModels) {
            LatLng latLng = proneAreaModel.getLatLng();
            clusterManager.addItem(new AccidentClusterItem(latLng, 1));
        }
        clusterManager.cluster();
    }

    private void addMarker(LatLng latLng, String address) {
        if (targetMarker != null) targetMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(address);
        targetMarker = mMap.addMarker(markerOptions);
        targetMarker.showInfoWindow();
    }

    private void addMarkerCircle(List<ProneAreaModel> proneAreaModels, float radius) {
        if (proneAreaModels == null) return;
        for (ProneAreaModel proneAreaModel : proneAreaModels) {
            LatLng latLng = proneAreaModel.getLatLng();
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(radius);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(60, 255, 0, 0));
            circleOptions.strokeWidth(4);
            mMap.addCircle(circleOptions);
        }
    }

    private void executeSQL() {
        Log.e(TAG, "executeSQL: " + "doing");
        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_prone_area.php")
                .newBuilder()
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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

                    requireActivity().runOnUiThread(() -> {
                        // testing
//                        myCity = "苗栗縣";
                        myCity = "臺北市";
                        MainActivity.getInstance().removeGeofence();
                        List<ProneAreaModel> currentProneAreaModels = new ArrayList<>();
                        for (ProneAreaModel proneAreaModel : proneAreaModels) {
                            if (myCity.equals(proneAreaModel.getCity())) {
                                currentProneAreaModels.add(proneAreaModel);
                            }
                        }
                        MainActivity.getInstance().addGeofences(currentProneAreaModels, radius);
                        addProneAreaMarker(proneAreaModels);
                        addMarkerCircle(proneAreaModels, radius);
                    });
                }
            }
        });
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        try {
            JSONArray jsonArray = new JSONArray(result);
            proneAreaModels = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String city = jsonObject.getString("city");
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                LatLng latLng = new LatLng(latitude, longitude);
                proneAreaModels.add(new ProneAreaModel(id, city, latLng));
            }
            Log.e(TAG, "parseJSON accidentProneAreaList size: " + proneAreaModels.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPowerPriority(boolean isSavePower) {
        if (isSavePower) {
            fusedLocationService.setLowPower();
        } else {
            fusedLocationService.setHighAccuracy();
        }
    }

    public String getGeoLocateCity(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext());
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

    private void setListener() {
        ivCommonAddress.setOnClickListener(view -> {
            // show list dialog and startNavigation
            if (addressModels.isEmpty()) {
                Toast.makeText(getContext(), "目前沒有常用地址", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_common_address);
            dialog.getWindow().setLayout(900, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            CommonAddressAdapter adapter = new CommonAddressAdapter(addressModels, this, 1);

            RecyclerView recyclerView = dialog.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            recyclerView.setAdapter(adapter);
            commonAddressDialog = dialog;
        });

        ivMyLocation.setOnClickListener(view -> {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            animateCamera(latLng, DEFAULT_ZOOM);
            isFocusOnMyLocation = false;
            ivMyLocation.setImageResource(R.drawable.ic_round_gps);
            ivMyLocation.setBackgroundTintList(ContextCompat.
                    getColorStateList(requireContext(), R.color.white));
        });

        ivMyLocation.setOnLongClickListener(view -> {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            if (isFocusOnMyLocation) {
                isFocusOnMyLocation = false;
                ivMyLocation.setImageResource(R.drawable.ic_round_gps);
                ivMyLocation.setBackgroundTintList(ContextCompat.
                        getColorStateList(requireContext(), R.color.white));
            } else {
                animateCamera(latLng, DEFAULT_ZOOM);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    isFocusOnMyLocation = true;
                }, 1500);
                ivMyLocation.setImageResource(R.drawable.ic_round_gps_white);
                ivMyLocation.setBackgroundTintList(ContextCompat.
                        getColorStateList(requireContext(), R.color.dark_gray));
            }

            return true;
        });

        btnDirection.setOnClickListener(view -> {
            if (targetLatLng == null) {
                Toast.makeText(getContext(), "請先搜尋地址", Toast.LENGTH_SHORT).show();
                return;
            }
            String origin = myLocation.getLatitude() + ", " + myLocation.getLongitude();
            String destination = targetLatLng.latitude + ", " + targetLatLng.longitude;

            Log.e(TAG, "setListener origin and destination: " + origin + ", " + destination);
            String avoid;
            if (getCarType().equals("d")) {
                avoid = "";
            } else {
                avoid = "highways";
            }
            executeDirections(origin, destination, avoid);
        });

        btnNavigation.setOnClickListener(view -> {
            String carType = getCarType();
            if (targetAddress.isEmpty()) {
                Toast.makeText(getContext(), "請先搜尋地址", Toast.LENGTH_SHORT).show();
                return;
            }
            startNavigation(targetAddress, carType);
        });
    }

    // Override Method
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
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

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        initClusterManager();

        //test
//        LatLng latLng = new LatLng(24.551915, 120.816187); // 三角公園
//        addMarker(latLng, "測試點");
//        addMarkerCircle(latLng, RADIUS_200);
    }

    private void initClusterManager() {
        clusterManager = new ClusterManager<>(requireContext(), mMap);

        ClusterRenderer renderer = new ClusterRenderer(requireContext(), mMap, clusterManager);
        clusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(clusterManager);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.e(TAG, "onMyLocationClick: " + location.getLatitude() + ", " + location.getLatitude());
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        isFocusOnMyLocation = false;
        ivMyLocation.setImageResource(R.drawable.ic_round_gps);
        ivMyLocation.setBackgroundTintList(ContextCompat.
                getColorStateList(requireContext(), R.color.white));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onStart() {
        super.onStart();
        // execute accident prone area
        if (mMap != null) {
            mMap.clear();
            Log.e(TAG, "onStart: " + "mMap clear");
        }
        executeSQL();
        mapView.onStart();
//        Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // execute accident prone area
//        executeSQL();
        mapView.onResume();
//        Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        autocompleteFragment.setText("");

//        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        targetLatLng = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        Log.e(TAG, "onLowMemory: ");
    }

    @Override
    public void onItemClick(int position) {
        commonAddressDialog.dismiss();
        clDetail.setVisibility(View.GONE);
        targetAddress = "";
        targetLatLng = null;
        if (targetMarker != null) targetMarker.remove();
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
        isFocusOnMyLocation = false;
        ivMyLocation.setImageResource(R.drawable.ic_round_gps);
        ivMyLocation.setBackgroundTintList(ContextCompat.
                getColorStateList(requireContext(), R.color.white));


        Log.e(TAG, "onItemClick: " + addressModels.get(position).getTitle());

        LatLng latLng = addressModels.get(position).getLatLng();
        String address = addressModels.get(position).getAddress();
        autocompleteFragment.setText(address);

        targetAddress = address;
        targetLatLng = latLng;

        addMarker(latLng, address);
        animateCamera(latLng, DEFAULT_ZOOM);

        /*
        String origin = myLocation.getLatitude() + ", " + myLocation.getLongitude();
        String destination = targetLatLng.latitude + ", " + targetLatLng.longitude;

        Log.e(TAG, "setListener origin and destination: " + origin + ", " + destination);
        String avoid;
        if (getCarType().equals("d")) {
            avoid = "";
        } else {
            avoid = "highways";
        }
        executeDirections(origin, destination, avoid);*/
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onDirectionsStart() {
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionsSuccess(List<RouteModel> routeModels, String duration, String distance) {
        requireActivity().runOnUiThread(() -> {
            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
        });

        polylinePaths = new ArrayList<>();
        for (RouteModel routeModel : routeModels) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
//                    .color(Color.argb(255, 123, 123, 123))
                    .color(Color.BLUE)
                    .width(10);

            for (int i = 0; i < routeModel.getPoints().size(); i++) {
                polylineOptions.add(routeModel.getPoints().get(i));
            }
            requireActivity().runOnUiThread(() -> {
                polylinePaths.add(mMap.addPolyline(polylineOptions));
                tvDuration.setText(duration);
                tvDistance.setText(distance);
                clDetail.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        myLocation = location;
        Log.d(TAG, "onLocationUpdated: " + location.getLatitude() + ", " + location.getLongitude());
        if (isFocusOnMyLocation) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            animateCamera(latLng, DEFAULT_ZOOM);
        }

        // Setting Places API Location Bias
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(location.getLatitude(), location.getLongitude()),
                new LatLng(location.getLatitude(), location.getLongitude())
        ));
    }

    @Override
    public void onLastLocationResult(Location location) {
        myLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCamera(latLng, DEFAULT_ZOOM);
    }

    @Override
    public void onCityChanged(String city) {
        myCity = city;
        Log.e(TAG, "onCityChanged: " + myCity);
        // add geofence
        if (proneAreaModels == null) {
            Log.e(TAG, "onCityChanged: proneAreaModels == null");
            return;
        }
//
//        MainActivity.getInstance().removeGeofence();
//        for (ProneAreaModel proneAreaModel : proneAreaModels) {
//            if (myCity.equals(proneAreaModel.getCity())) {
//                MainActivity.getInstance().addGeofence(proneAreaModel.getId(), proneAreaModel.getLatLng(), radius);
//            }
//        }
    }



}