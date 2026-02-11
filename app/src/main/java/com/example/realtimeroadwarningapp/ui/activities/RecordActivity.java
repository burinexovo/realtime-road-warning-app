package com.example.realtimeroadwarningapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.realtimeroadwarningapp.databinding.ActivityAccidentRecordBinding;
import com.example.realtimeroadwarningapp.databinding.ActivityRecordBinding;
import com.example.realtimeroadwarningapp.model.RecordModel;

public class RecordActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityRecordBinding binding;
    private ImageView ivHome;
    private TextView tvTitle;
    private EditText etName, etPhone, etEmail, etNumberOfPassengers, etInjuryStatus,
            etCarStatus, etCauseOfAccident, etLicensePlate, etMyAddress;
    private Button btnNext;
    private RecordModel recordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ivHome = binding.ivHome;
        tvTitle = binding.tvTitle;
        etName = binding.etName;
        etPhone = binding.etPhone;
        etEmail = binding.etEmail;
        etNumberOfPassengers = binding.etNumberOfPassengers;
        etInjuryStatus = binding.etInjuryStatus;
        etCarStatus = binding.etCarStatus;
        etCauseOfAccident = binding.etCauseOfAccident;
        etLicensePlate = binding.etLicensePlate;
        etMyAddress = binding.etMyAddress;
        btnNext = binding.btnNext;

        recordModel = getIntent().getParcelableExtra("RECORD_MODEL");
        if (recordModel == null) {
            Log.e(TAG, "onCreate: " + "recordModel is null");
            return;
        }

        tvTitle.setText(recordModel.getTime() + "肇事紀錄");
        etName.setText(recordModel.getName());
        etPhone.setText(recordModel.getPhone());
        etEmail.setText(recordModel.getEmail());
        etNumberOfPassengers.setText(recordModel.getNumOfPassengers());
        etInjuryStatus.setText(recordModel.getInjuryStatus());
        etCarStatus.setText(recordModel.getCarStatus());
        etCauseOfAccident.setText(recordModel.getCauseOfAccident());
        etLicensePlate.setText(recordModel.getLicensePlate());
        etMyAddress.setText(recordModel.getAddress());

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecordImageActivity.class);
            intent.putExtra("RECORD_MODEL", recordModel);
            startActivity(intent);
        });
    }
}