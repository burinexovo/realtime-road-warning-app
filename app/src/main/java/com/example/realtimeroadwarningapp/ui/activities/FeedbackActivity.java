package com.example.realtimeroadwarningapp.ui.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityFeedbackBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedbackActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ImageView ivHome;
    private EditText etNickname, etEmail, etFeedback;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedbackBinding binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        etNickname = binding.etNickname;
        etEmail = binding.etEmail;
        etFeedback = binding.etFeedback;
        btnSend = binding.btnSend;

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnSend.setOnClickListener(view -> {
            if (isValidDetails()){
                executeSQL();
                btnSend.setEnabled(false);
            }
        });
    }

    private void executeSQL() {
        String nickname = etNickname.getText().toString();
        String email = etEmail.getText().toString();
        String content = etFeedback.getText().toString();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        Log.e(TAG, "btnSend: " + nickname + ", " + email + ", " + content + ", " + currentTime);

        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "feedback.php").newBuilder()
                .addQueryParameter("nickname", nickname)
                .addQueryParameter("email", email)
                .addQueryParameter("content", content)
                .addQueryParameter("datetime", currentTime)
                .build();

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
                        Dialog dialog = new Dialog(FeedbackActivity.this);
                        dialog.setContentView(R.layout.dialog_thank_for_the_feedback);
                        dialog.getWindow().setLayout(900, 1300);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.show();
                        new Handler(Looper.getMainLooper()).postDelayed(() ->{
                            dialog.dismiss();
                            onBackPressed();
                        }, 1500);
                    });

                }
            }
        });
    }

    private boolean isValidDetails() {
        if (etNickname.getText().toString().trim().isEmpty() ||
                etFeedback.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            Toast.makeText(this, "電子信箱格式錯誤", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}