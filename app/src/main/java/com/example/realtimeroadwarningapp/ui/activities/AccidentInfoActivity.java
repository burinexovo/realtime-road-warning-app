package com.example.realtimeroadwarningapp.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.adapter.AccidentAdapter;
import com.example.realtimeroadwarningapp.databinding.ActivityAccidentInfoBinding;
import com.example.realtimeroadwarningapp.model.AccidentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccidentInfoActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivHome;
    private TextView tvFromDate, tvToDate, tvSelectCity;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private ActivityAccidentInfoBinding binding;
    private AccidentAdapter adapter;
    private List<AccidentModel> accidentModels;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccidentInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accidentModels = new ArrayList<>();

        ivHome = binding.ivHome;
        tvFromDate = binding.tvFromDate;
        tvToDate = binding.tvToDate;
        tvSelectCity = binding.tvSelectCity;
        btnSearch = binding.btnSearch;
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressBar;


        // test
        initialize();

        setListener();

        setRecyclerView();
    }

    private void initialize() {
        String today = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                .format(new Date());

        tvFromDate.setText("2022/01/01");
        tvToDate.setText(today);
        tvSelectCity.setText("苗栗縣");
        tvToDate.setEnabled(true);
        executeSQL();
    }

    private void setRecyclerView() {
        adapter = new AccidentAdapter(accidentModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void executeSQL() {
        String fromDate = tvFromDate.getText().toString();
        String toDate = tvToDate.getText().toString();
        String city = tvSelectCity.getText().toString();

        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_info.php")
                .newBuilder()
                .addQueryParameter("fromDate", fromDate)
                .addQueryParameter("toDate", toDate)
                .addQueryParameter("city", city)
                .build();



        Log.e(TAG, "executeSQL url: " + url);
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
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            }
        });
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> { // onRefresh
            // notifyDataSetChanged
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 800);
            adapter.notifyDataSetChanged();
        });


        // init Calendar
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvFromDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AccidentInfoActivity.this,
                    R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month += 1;
                    String date = year + "/" + month + "/" + day;
                    tvFromDate.setText(date);
                    tvToDate.setText("");

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    tvToDate.setEnabled(true);
                }
            }, year, month, day);
            datePickerDialog.show();
        });

        tvToDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AccidentInfoActivity.this,
                    R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month += 1;
                    String date = year + "/" + month + "/" + day;
                    tvToDate.setText(date);
                }
            }, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        tvSelectCity.setOnClickListener(view -> {
            Dialog dialog = new Dialog(AccidentInfoActivity.this);
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            dialog.getWindow().setLayout(900, 1000);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            final EditText etSearchCity = dialog.findViewById(R.id.et_search_city);
            final ListView listView = dialog.findViewById(R.id.list_view);

            String[] cityArray = getResources().getStringArray(R.array.city_array);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(AccidentInfoActivity.this,
                    android.R.layout.simple_list_item_1, cityArray);

            listView.setAdapter(adapter);
            etSearchCity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tvSelectCity.setText(adapter.getItem(i));
                    dialog.dismiss();
                }
            });

        });

        btnSearch.setOnClickListener(view -> {
            String fromDate = tvFromDate.getText().toString();
            String toDate = tvToDate.getText().toString();
            String city = tvSelectCity.getText().toString();
            if (fromDate.isEmpty() || toDate.isEmpty() || city.isEmpty()) {
                Toast.makeText(AccidentInfoActivity.this, "請確保所有資料都選擇完畢", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());;

            // execute accident info
            executeSQL();
        });
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        try {
            JSONArray jsonArray = new JSONArray(result);
            accidentModels.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                AccidentModel accidentModel = new AccidentModel(
                        jsonObject.getString("region"),
                        jsonObject.getString("death_toll"),
                        jsonObject.getString("number_of_injury"),
                        jsonObject.getString("number_of_pieces")
                );
                accidentModels.add(accidentModel);
            }

            Log.e("jsonDecode", accidentModels.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}