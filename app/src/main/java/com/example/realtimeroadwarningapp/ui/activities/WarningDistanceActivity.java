package com.example.realtimeroadwarningapp.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityWarningDistanceBinding;
import com.example.realtimeroadwarningapp.utils.Constants;

public class WarningDistanceActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ImageView ivHome;
    private ConstraintLayout cl200m, cl500m, cl1000m;
    private RadioButton rb200m, rb500m, rb1000m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWarningDistanceBinding binding = ActivityWarningDistanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        cl200m = binding.cl200m;
        cl500m = binding.cl500m;
        cl1000m = binding.cl1000m;
        rb200m = binding.rb200m;
        rb500m = binding.rb500m;
        rb1000m = binding.rb1000m;

        loadData();

        setListener();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        int distance = sharedPreferences.getInt(Constants.KEY_WARNING_DISTANCE, 200);
        switch (distance) {
            case 200:
                rb200m.setChecked(true);
                rb500m.setChecked(false);
                rb1000m.setChecked(false);
                break;
            case 500:
                rb200m.setChecked(false);
                rb500m.setChecked(true);
                rb1000m.setChecked(false);
                break;
            case 1000:
                rb200m.setChecked(false);
                rb500m.setChecked(false);
                rb1000m.setChecked(true);
                break;
        }
    }

    private void setData(int distance) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.KEY_WARNING_DISTANCE, distance);
        editor.apply();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        cl200m.setOnClickListener(view -> {
            if (!rb200m.isChecked() && (rb500m.isChecked() || rb1000m.isChecked())) {
                rb200m.setChecked(true);
                rb500m.setChecked(false);
                rb1000m.setChecked(false);
                setData(200);
            }
        });

        cl500m.setOnClickListener(view -> {
            if (!rb500m.isChecked() && (rb200m.isChecked() || rb1000m.isChecked())) {
                rb200m.setChecked(false);
                rb500m.setChecked(true);
                rb1000m.setChecked(false);
                setData(500);
            }
        });

        cl1000m.setOnClickListener(view -> {
            if (!rb1000m.isChecked() && (rb200m.isChecked() || rb500m.isChecked())) {
                rb200m.setChecked(false);
                rb500m.setChecked(false);
                rb1000m.setChecked(true);
                setData(1000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}