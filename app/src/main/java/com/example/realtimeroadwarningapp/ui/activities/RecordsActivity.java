package com.example.realtimeroadwarningapp.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.RecyclerViewClickInterface;
import com.example.realtimeroadwarningapp.adapter.RecordAdapter;
import com.example.realtimeroadwarningapp.databinding.ActivityRecordBinding;
import com.example.realtimeroadwarningapp.databinding.ActivityRecordsBinding;
import com.example.realtimeroadwarningapp.model.RecordModel;
import com.example.realtimeroadwarningapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordsActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityRecordsBinding binding;
    private ImageView ivHome, ivAccount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button btnAddAccidentRecord;
    private List<RecordModel> recordModels;
    private RecordAdapter adapter;
    private String email;
    private TextView tvNoAddressDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ivHome = binding.ivHome;
        ivAccount = binding.ivAccount;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        recyclerView = binding.recyclerView;
        btnAddAccidentRecord = binding.btnAddCommonAddress;
        tvNoAddressDescription = binding.tvNoAddressDescription;

        loadData();

        if (!email.equals("")) {
            // execute sql
            HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "record.php")
                    .newBuilder()
                    .addQueryParameter("email", email)
                    .build();
            executeSQL(url);
        }

        setListener();

        recordModels = new ArrayList<>();
        setRecyclerView();
    }

    private void executeSQL(HttpUrl url) {
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
                    parseJSON(result);

                    runOnUiThread(() -> {
                        if (result.equals("null")) {
                            tvNoAddressDescription.setVisibility(View.VISIBLE);
                        } else {
                            tvNoAddressDescription.setVisibility(View.INVISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        Log.e(TAG, "onResponse: Data change doing");
                    });
                }
            }
        });
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        recordModels.clear();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                RecordModel recordModel = new RecordModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                recordModel.setTime(jsonObject.getString("datetime"));
                recordModel.setName(jsonObject.getString("name"));
                recordModel.setPhone(jsonObject.getString("phone"));
                recordModel.setEmail(jsonObject.getString("email"));
                recordModel.setNumOfPassengers(jsonObject.getString("number_of_passengers"));
                recordModel.setInjuryStatus(jsonObject.getString("injury_status"));
                recordModel.setCarStatus(jsonObject.getString("car_status"));
                recordModel.setCauseOfAccident(jsonObject.getString("cause_of_accident"));
                recordModel.setLicensePlate(jsonObject.getString("license_plate_number"));
                recordModel.setLatitude(jsonObject.getString("latitude"));
                recordModel.setLongitude(jsonObject.getString("longitude"));
                recordModel.setAddress(jsonObject.getString("occurrence_address"));
                recordModel.setImagePath(jsonObject.getString("image_path"));
                recordModels.add(recordModel);
            }
            Log.e(TAG, "parseJSON recordModels size: " + recordModels.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        email = sharedPreferences.getString(Constants.KEY_EMAIL, "");
    }

    private void setRecyclerView() {
        adapter = new RecordAdapter(recordModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        ivAccount.setOnClickListener(view -> {
            // show dialog to enter your email & set sharedPreference
            // confirm -> execute sql -> refresh recyclerView

            Dialog dialog = new Dialog(RecordsActivity.this);
            dialog.setContentView(R.layout.dialog_account);
            dialog.getWindow().setLayout(900, 800);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            final EditText etEmail = dialog.findViewById(R.id.et_email);
            final Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            final TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            etEmail.setText(email);

            btnConfirm.setOnClickListener(v -> {
                email = etEmail.getText().toString();
                dialog.dismiss();

                setData(Constants.KEY_EMAIL, email);

                HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "record.php")
                        .newBuilder()
                        .addQueryParameter("email", email)
                        .build();
                executeSQL(url);
            });

            tvCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
        });

        swipeRefreshLayout.setOnRefreshListener(() -> { // onRefresh
            // notifyDataSetChanged
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "record.php")
                        .newBuilder()
                        .addQueryParameter("email", email)
                        .build();
                executeSQL(url);
            }, 800);
            adapter.notifyDataSetChanged();

        });

        btnAddAccidentRecord.setOnClickListener(view -> {
            startActivity(new Intent(this, AccidentRecordActivity.class));
        });
    }

    private void setData(String key, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, email);
        editor.apply();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("RECORD_MODEL", recordModels.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {

    }
}