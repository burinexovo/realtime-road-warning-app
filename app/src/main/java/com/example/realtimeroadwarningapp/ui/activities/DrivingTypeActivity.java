package com.example.realtimeroadwarningapp.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityDrivingTypeBinding;
import com.example.realtimeroadwarningapp.utils.Constants;

public class DrivingTypeActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ImageView ivHome;
    private ConstraintLayout clCar, clScooter;
    private RadioButton rbCar, rbScooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDrivingTypeBinding binding = ActivityDrivingTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        clCar = binding.clCar;
        clScooter = binding.clScooter;
        rbCar = binding.rbCar;
        rbScooter = binding.rbScooter;

        loadData();

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        clCar.setOnClickListener(view -> {
            if (!rbCar.isChecked() && rbScooter.isChecked()) {
                rbCar.setChecked(true);
                rbScooter.setChecked(false);
                setData(0);
            }
        });

        clScooter.setOnClickListener(view -> {
            if (!rbScooter.isChecked() && rbCar.isChecked()) {
                rbScooter.setChecked(true);
                rbCar.setChecked(false);
                setData(1);
            }
        });
    }

    private void setData(int carType) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.KEY_DRIVING_TYPE, carType);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        int carType = sharedPreferences.getInt(Constants.KEY_DRIVING_TYPE, 0);
        switch (carType) {
            case 0:
                rbCar.setChecked(true);
                break;
            case 1:
                rbScooter.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}