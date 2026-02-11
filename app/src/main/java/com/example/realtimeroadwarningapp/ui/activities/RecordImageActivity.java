package com.example.realtimeroadwarningapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.realtimeroadwarningapp.databinding.ActivityRecordImageBinding;
import com.example.realtimeroadwarningapp.model.RecordModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordImageActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityRecordImageBinding binding;
    private ImageView ivHome, ivImage;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRecordImageBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        tvTitle = binding.tvTitle;
        ivHome = binding.ivHome;
        ivImage = binding.ivImage;

        RecordModel recordModel = getIntent().getParcelableExtra("RECORD_MODEL");

        tvTitle.setText(recordModel.getTime() + "肇事畫面");
        ivImage.setImageURI(Uri.parse(recordModel.getImagePath()));

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}