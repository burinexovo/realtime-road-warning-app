package com.example.realtimeroadwarningapp.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityImageBinding;
import com.example.realtimeroadwarningapp.model.RecordModel;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityImageBinding binding;
    private Button btnSend;
    private ImageView ivImage, ivHome;
    private RecordModel recordModel;
    private String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnSend = binding.btnSend;
        ivImage = binding.ivImage;
        ivHome = binding.ivHome;

        recordModel = getIntent().getParcelableExtra("RECORD_MODEL");
        Log.e(TAG, "recordModel: " + recordModel.getAddress());
        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        ivImage.setOnClickListener(view -> {
            ImagePicker.Companion.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        btnSend.setOnClickListener(view -> {
            if (imagePath.isEmpty()) {
                Toast.makeText(this, "請拍攝或選擇肇事畫面", Toast.LENGTH_SHORT).show();
                return;
            }

            HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_record.php")
                    .newBuilder()
                    .addQueryParameter("datetime", recordModel.getTime())
                    .addQueryParameter("name", recordModel.getName())
                    .addQueryParameter("phone", recordModel.getPhone())
                    .addQueryParameter("email", recordModel.getEmail())
                    .addQueryParameter("numOfPassengers", recordModel.getNumOfPassengers())
                    .addQueryParameter("injuryStatus", recordModel.getInjuryStatus())
                    .addQueryParameter("carStatus", recordModel.getCarStatus())
                    .addQueryParameter("causeOfAccident", recordModel.getCauseOfAccident())
                    .addQueryParameter("licensePlateNumber", recordModel.getLicensePlate())
                    .addQueryParameter("latitude", String.valueOf(recordModel.getLatitude()))
                    .addQueryParameter("longitude", String.valueOf(recordModel.getLongitude()))
                    .addQueryParameter("occurrenceAddress", recordModel.getAddress())
                    .addQueryParameter("imagePath", imagePath)
                    .build();

            executeAccidentRecord(url);
            btnSend.setEnabled(false);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ivImage.setImageURI(uri);

            String imagePath = uri.toString();
            Log.e(TAG, "onActivityResult: " + imagePath);

            this.imagePath = imagePath;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, "onActivityResult: error image didn't load");
        }
    }

    private void executeAccidentRecord(HttpUrl url) {

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

                    runOnUiThread(() -> {
                        Dialog dialog = new Dialog(ImageActivity.this);
                        dialog.setContentView(R.layout.dialog_submitted);
                        dialog.getWindow().setLayout(900, 1300);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.show();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            dialog.dismiss();
                            Intent intent = new Intent(ImageActivity.this, RecordsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }, 1500);
                    });
                }
            }
        });
    }
}

