package com.example.realtimeroadwarningapp.ui.activities;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.realtimeroadwarningapp.R;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private final int REQUEST_PERMISSIONS = 1;
    private final int REQUEST_BACKGROUND_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ArrayList<String> requestPermissions = new ArrayList<String>() {{
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        Log.e(TAG, "onCreate: permission array" + Arrays.toString(requestPermissions.toArray(new String[0])));

        if (requestPermissions.stream().anyMatch(i -> ActivityCompat.checkSelfPermission(this, i) == PERMISSION_DENIED)) {
            Log.e(TAG, "checkSelfPermission: permission denied");
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[0]), REQUEST_PERMISSIONS);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.e(TAG, "checkSelfPermission permission granted");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }, 1000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).noneMatch(i -> i == PERMISSION_DENIED)) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.e(TAG, "checkSelfPermission permission granted");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1000);
            } else {
                Dialog dialog = new Dialog(SplashActivity.this);
                dialog.setContentView(R.layout.dialog_request_permission);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(900, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final TextView tvDialogSummary = dialog.findViewById(R.id.tv_dialog_summary);
                final TextView tvDialogPath = dialog.findViewById(R.id.tv_dialog_path);
                final Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
                final TextView tvCancel = dialog.findViewById(R.id.tv_cancel);

                tvDialogSummary.setText(getString(R.string.permission_request_summary));
                tvDialogPath.setText(getString(R.string.setting_path));

                btnConfirm.setOnClickListener(view -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                    new Handler().postDelayed(this::finish, 1000);
                });

                tvCancel.setOnClickListener(view -> {
                    new Handler().postDelayed(this::finish, 1000);
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}