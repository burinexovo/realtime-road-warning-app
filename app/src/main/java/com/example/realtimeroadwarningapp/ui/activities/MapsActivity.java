package com.example.realtimeroadwarningapp.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.realtimeroadwarningapp.BuildConfig;
import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityMapsBinding;
import com.example.realtimeroadwarningapp.model.CommonAddressModel;
import com.example.realtimeroadwarningapp.service.FusedLocationService;
import com.example.realtimeroadwarningapp.ui.fragments.map.MapsFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private static final float DEFAULT_ZOOM = 16f;
    private final String TAG = this.getClass().getSimpleName();
    private ImageView ivHome, ivMyLocation;
    private TextView tvDescription;
    private Button btnAddCommonAddress;
    private GoogleMap mMap;
    private FusedLocationService fusedLocationService;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLocation;
    private LatLng searchLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        ivMyLocation = binding.ivMyLocation;
        tvDescription = binding.tvDescription;
        btnAddCommonAddress = binding.btnAddCommonAddress;

        setListener();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        initAutocompleteSupportFragment();

    }

    // Method
    private void getLastLocation() {
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

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                // have a location
                myLocation = location;
                Log.e(TAG, "getLastLocation onSuccess location: " + location);
                Log.e(TAG, "getLastLocation onSuccess getLongitude: " + location.getLongitude());
                Log.e(TAG, "getLastLocation onSuccess getLatitude: " + location.getLatitude());

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM);
            } else {
                Log.e(TAG, "getLastLocation onSuccess: location was null");
            }
        });

        locationTask.addOnFailureListener(e -> {
            Log.e(TAG, "getLastLocation onFailure: " + e.getLocalizedMessage());
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void animateCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void addMarker(LatLng latLng) {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addMarker(LatLng latLng, String address) {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(address);
        mMap.addMarker(markerOptions).showInfoWindow();
    }

    // Address to location
    private void geoLocate(String result) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocationName(result, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate IOException: " + e.getLocalizedMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            Log.e(TAG, "geoLocate getAddress: " + address.toString());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            animateCamera(latLng, DEFAULT_ZOOM);
            addMarker(latLng, address.getAddressLine(0));
            tvDescription.setText(address.getAddressLine(0));
            searchLatLng = new LatLng(latLng.latitude, latLng.longitude);
        }
    }

    // location to address
    private void geoLocate(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate IOException: " + e.getLocalizedMessage());
        }

        if (addressList.size() > 0) {
            String address = addressList.get(0).getAddressLine(0);
            Log.e(TAG, "geoLocate getAddress: " + address);
            addMarker(latLng, address);
            tvDescription.setText(address);
            searchLatLng = new LatLng(latLng.latitude, latLng.longitude);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void initAutocompleteSupportFragment() {
        // Place Autocomplete
        String apiKey = getString(R.string.google_maps_API_key);
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAPS_API_KEY);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        String hint = getString(R.string.search_bar);
        autocompleteFragment.setHint(hint);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(24, 121),
                new LatLng(24, 121)
        ));

        autocompleteFragment.setCountry("TW");

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.e(TAG, "setOnPlaceSelectedListener onPlaceSelected: " + place.getId() + ", " +
                        place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());

                LatLng latLng = place.getLatLng();
                String address = place.getAddress();
                searchLatLng = place.getLatLng();
                tvDescription.setText(address);
                addMarker(latLng, address);
                animateCamera(latLng, DEFAULT_ZOOM);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "setOnPlaceSelectedListener onError: " + status);
            }
        });

    }


    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        ivMyLocation.setOnClickListener(view -> {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            animateCamera(latLng, DEFAULT_ZOOM);
        });

        btnAddCommonAddress.setOnClickListener(view -> {
            String address = tvDescription.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(this, "請先搜尋地址", Toast.LENGTH_SHORT).show();
                return;
            }

            Dialog dialog = new Dialog(MapsActivity.this);
            dialog.setContentView(R.layout.dialog_add_common_address);
            dialog.getWindow().setLayout(900, 800);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            final EditText etItemTitle = dialog.findViewById(R.id.et_item_title);
            final TextView tvDialogSummary = dialog.findViewById(R.id.tv_dialog_summary);
            final Button btnAdd = dialog.findViewById(R.id.btn_add);
            final TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            tvDialogSummary.setText(address);

            btnAdd.setOnClickListener(v -> {
                String itemTitle = etItemTitle.getText().toString();
                dialog.dismiss();

                if (itemTitle.isEmpty()) itemTitle = "Untitled";
                CommonAddressModel commonAddressModel = new CommonAddressModel(itemTitle, address, searchLatLng);

                // Send parcel model
                Intent intent = new Intent(this, CommonAddressActivity.class);
                intent.putExtra("COMMON_ADDRESS_MODEL", commonAddressModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });

            tvCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
        });

    }

    // Override Method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        geoLocate(latLng);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}