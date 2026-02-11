package com.example.realtimeroadwarningapp.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityAccidentRecordBinding;
import com.example.realtimeroadwarningapp.model.RecordModel;
import com.example.realtimeroadwarningapp.ui.fragments.map.MapsFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccidentRecordActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityAccidentRecordBinding binding;
    private FrameLayout flLoading;
    private ImageView ivHome;
    private TextView tvLoading;
    private EditText etName, etPhone, etEmail, etNumberOfPassengers, etInjuryStatus,
            etCarStatus, etCauseOfAccident, etLicensePlate, etMyAddress;
    private ProgressBar progressBar;
    private Button btnSelectImage, btnSend;

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        progressBar.setVisibility(View.VISIBLE);
                        tvLoading.setVisibility(View.VISIBLE);
                        flLoading.setVisibility(View.VISIBLE);
                        setUiEnabled(false);

                        Uri uri = result.getData().getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //create a FirebaseVisionImage object from a Bitmap object
                        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                        //Get an instance of FirebaseVision
                        FirebaseVision firebaseVision = FirebaseVision.getInstance();
                        //Create an instance of FirebaseVisionTextRecognizer
                        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
                        //Create a task to process the image
                        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

                        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                String text = firebaseVisionText.getText();
                                text = text.replaceAll("\\s+","");
                                text = text.replaceAll("[^a-zA-Z&\\d]", "");
                                Log.e(TAG, "onSuccess: " + text);

                                String licensePlateNumber = text;

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    tvLoading.setVisibility(View.INVISIBLE);
                                    flLoading.setVisibility(View.INVISIBLE);

                                    // Query SQL
                                    HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "member_profile.php")
                                            .newBuilder()
                                            .addQueryParameter("licensePlateNumber", licensePlateNumber)
                                            .build();
                                    executeMemberData(url);

                                    // setText
                                    runOnUiThread(() -> {
                                        etLicensePlate.setText(licensePlateNumber);
                                        setUiEnabled(true);
                                    });

                                }, 1000);
                            }
                        });

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    tvLoading.setVisibility(View.INVISIBLE);
                                    flLoading.setVisibility(View.INVISIBLE);

                                    runOnUiThread(() -> {
                                        setUiEnabled(true);
                                    });

                                    Toast.makeText(AccidentRecordActivity.this, "車牌無法辨識", Toast.LENGTH_SHORT).show();
                                }, 1000);
                            }
                        });


                    }
                }
            });
    private Double latitude, longitude;

    private void setUiEnabled(boolean isEnable) {
        etName.setEnabled(isEnable);
        etPhone.setEnabled(isEnable);
        etEmail.setEnabled(isEnable);
        etNumberOfPassengers.setEnabled(isEnable);
        etLicensePlate.setEnabled(isEnable);
        btnSelectImage.setClickable(isEnable);
        etInjuryStatus.setEnabled(isEnable);
        etCarStatus.setEnabled(isEnable);
        etCauseOfAccident.setEnabled(isEnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccidentRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        etName = binding.etName;
        etPhone = binding.etPhone;
        etEmail = binding.etEmail;
        etNumberOfPassengers = binding.etNumberOfPassengers;
        etInjuryStatus = binding.etInjuryStatus;
        etCarStatus = binding.etCarStatus;
        etCauseOfAccident = binding.etCauseOfAccident;
        etLicensePlate = binding.etLicensePlate;
        btnSelectImage = binding.btnSelectImage;
        btnSend = binding.btnSend;
        progressBar = binding.progressBar;
        tvLoading = binding.tvLoading;
        flLoading = binding.flLoading;
        etMyAddress = binding.etMyAddress;

        etMyAddress.setEnabled(false);

        Location myLocation = MapsFragment.getInstance().getMyLocation();
        latitude = myLocation.getLatitude();
        longitude = myLocation.getLongitude();

        String address = geoLocate(new LatLng(latitude, longitude));

        etMyAddress.setText(address);

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        btnSelectImage.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            someActivityResultLauncher.launch(intent);

            ImagePicker.Companion.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        btnSend.setOnClickListener(view -> {
            if (isValidDetails()) {
                Log.e(TAG, "setListener: OK");
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                String numOfPassengers = etNumberOfPassengers.getText().toString();
                String injuryStatus = etInjuryStatus.getText().toString();
                String carStatus = etCarStatus.getText().toString();
                String causeOfAccident = etCauseOfAccident.getText().toString();
                String licensePlate = etLicensePlate.getText().toString();
                String address = etMyAddress.getText().toString();

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());

                RecordModel recordModel = new RecordModel();
                recordModel.setTime(currentTime);
                recordModel.setName(name);
                recordModel.setPhone(phone);
                recordModel.setEmail(email);
                recordModel.setNumOfPassengers(numOfPassengers);
                recordModel.setInjuryStatus(injuryStatus);
                recordModel.setCarStatus(carStatus);
                recordModel.setCauseOfAccident(causeOfAccident);
                recordModel.setLicensePlate(licensePlate);
                recordModel.setLatitude(String.valueOf(latitude));
                recordModel.setLongitude(String.valueOf(longitude));
                recordModel.setAddress(address);

                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("RECORD_MODEL", recordModel);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);
            flLoading.setVisibility(View.VISIBLE);
            setUiEnabled(false);

            Uri uri = data.getData();

            String imagePath = uri.toString();
            Log.e(TAG, "onActivityResult: " + imagePath);

            progressBar.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);
            flLoading.setVisibility(View.VISIBLE);
            setUiEnabled(false);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //create a FirebaseVisionImage object from a Bitmap object
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            //Get an instance of FirebaseVision
            FirebaseVision firebaseVision = FirebaseVision.getInstance();
            //Create an instance of FirebaseVisionTextRecognizer
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
            //Create a task to process the image
            Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String text = firebaseVisionText.getText();
                    text = text.replaceAll("\\s+","");
                    text = text.replaceAll("[^a-zA-Z&\\d]", "");
                    Log.e(TAG, "onSuccess: " + text);

                    String licensePlateNumber = text;

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        tvLoading.setVisibility(View.INVISIBLE);
                        flLoading.setVisibility(View.INVISIBLE);

                        // Query SQL
                        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "member_profile.php")
                                .newBuilder()
                                .addQueryParameter("licensePlateNumber", licensePlateNumber)
                                .build();
                        executeMemberData(url);

                        // setText
                        runOnUiThread(() -> {
                            etLicensePlate.setText(licensePlateNumber);
                            setUiEnabled(true);
                        });

                    }, 1000);
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        tvLoading.setVisibility(View.INVISIBLE);
                        flLoading.setVisibility(View.INVISIBLE);

                        runOnUiThread(() -> {
                            setUiEnabled(true);
                        });

                        Toast.makeText(AccidentRecordActivity.this, "車牌無法辨識", Toast.LENGTH_SHORT).show();
                    }, 1000);
                }
            });

        } else {
            Log.e(TAG, "onActivityResult: error image didn't load");
        }
    }

    // location to address
    private String geoLocate(LatLng latLng) {
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
            return address;
        }
        return "";
    }


    private void executeMemberData(HttpUrl url) {

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
                    if (result.equals(getString(R.string.no_license_plate_information))) {
                        runOnUiThread(() ->{
                            Toast.makeText(AccidentRecordActivity.this, "查無車牌資料", Toast.LENGTH_SHORT).show();
                            etName.setText("");
                            etPhone.setText("");
                            etEmail.setText("");
                        });
                        return;
                    }
                    parseJSON(result);
                }
            }
        });
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            runOnUiThread(() -> {
                try {
                    etName.setText(jsonObject.getString("name"));
                    etEmail.setText(jsonObject.getString("email"));
                    etPhone.setText(jsonObject.getString("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidDetails() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();
        String numOfPassengers = etNumberOfPassengers.getText().toString();
        String injuryStatus = etInjuryStatus.getText().toString();
        String carStatus = etCarStatus.getText().toString();
        String causeOfAccident = etCauseOfAccident.getText().toString();
        String licensePlate = etLicensePlate.getText().toString();

        if (name.trim().isEmpty() || phone.trim().isEmpty() || email.trim().isEmpty() ||
                numOfPassengers.trim().isEmpty() || injuryStatus.trim().isEmpty() || carStatus.trim().isEmpty() ||
                causeOfAccident.trim().isEmpty() || licensePlate.trim().isEmpty()) {
            Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            Toast.makeText(this, "電子信箱格式錯誤", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(numOfPassengers) <= 0) {
            Toast.makeText(this, "乘車人數輸入錯誤", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}