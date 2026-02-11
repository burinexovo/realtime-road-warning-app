package com.example.realtimeroadwarningapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ImageView ivHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}