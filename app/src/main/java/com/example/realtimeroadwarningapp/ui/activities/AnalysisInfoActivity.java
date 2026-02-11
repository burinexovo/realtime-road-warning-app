package com.example.realtimeroadwarningapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityAnalysisInfoBinding;
import com.example.realtimeroadwarningapp.ui.activities.chart.BarChartActivity;
import com.example.realtimeroadwarningapp.ui.activities.chart.LineChartActivity;
import com.example.realtimeroadwarningapp.ui.activities.chart.PieChartActivity;

public class AnalysisInfoActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityAnalysisInfoBinding binding;
    private ConstraintLayout clPieChart, clLineChart, clBarChart;
    private ImageView ivHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalysisInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        clPieChart = binding.clPieChart;
        clLineChart = binding.clLineChart;
        clBarChart = binding.clBarChart;

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        clPieChart.setOnClickListener(view -> {
            Intent intent = new Intent(this, PieChartActivity.class);
            startActivity(intent);
        });

        clLineChart.setOnClickListener(view -> {
            Intent intent = new Intent(this, LineChartActivity.class);
            startActivity(intent);
        });

        clBarChart.setOnClickListener(view -> {
            Intent intent = new Intent(this, BarChartActivity.class);
            startActivity(intent);
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}