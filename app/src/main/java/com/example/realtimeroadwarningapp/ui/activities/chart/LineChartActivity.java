package com.example.realtimeroadwarningapp.ui.activities.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityBarChartBinding;
import com.example.realtimeroadwarningapp.databinding.ActivityLineChartBinding;
import com.example.realtimeroadwarningapp.model.AgeRangeModel;
import com.example.realtimeroadwarningapp.model.MonthGroupModel;
import com.example.realtimeroadwarningapp.ui.activities.AccidentInfoActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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

public class LineChartActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityLineChartBinding binding;
    private ImageView ivHome;
    private LineChart lineChart;
    private TextView tvSelectCity;
    private Button btnSearch;
    private List<MonthGroupModel> monthGroupModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLineChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        lineChart = binding.lineChart;
//        tvSelectCity = binding.tvSelectCity;
//        btnSearch = binding.btnSearch;

        setLineChart();


        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_info_line_chart.php").newBuilder()
                .build();

        executeSQL(url);

        setListener();
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
                    loadLineChartData();
                    runOnUiThread(() -> {
                        lineChart.invalidate();
                        lineChart.animateY(1400, Easing.EaseInOutQuad);//動畫
                    });
                }
            }
        });
    }

    private void loadLineChartData() {

        ArrayList<Entry> entries = new ArrayList<>();

        for (MonthGroupModel monthGroupModel : monthGroupModels) {
            entries.add(new Entry(Integer.parseInt(monthGroupModel.getMonth()),monthGroupModel.getCasualties()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries,"資料");
        lineDataSet.setLineWidth(3.0f);
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.bright_yarrow));
        lineDataSet.setDrawCircleHole(false);//圓點為實心(預設空心)
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.dark_blue));//線的顏色
        lineDataSet.setCircleRadius(4);//圓點大小

        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);

        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.setNoDataText("Data not Available");
        lineChart.getDescription().setEnabled(false);
    }

    private void parseJSON(String result) {

        if (result == null) return;
        monthGroupModels = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String month = jsonObject.getString("month");
                String deathToll = jsonObject.getString("death_toll");
                String numberOfInjury = jsonObject.getString("number_of_injury");
                int casualties = Integer.parseInt(deathToll) + Integer.parseInt(numberOfInjury);

                monthGroupModels.add(new MonthGroupModel(month, deathToll, numberOfInjury, casualties));
            }

            Log.e(TAG, "parseJSON: monthGroupModels" + monthGroupModels.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

//        btnSearch.setOnClickListener(view -> {
//            Dialog dialog = new Dialog(this);
//            dialog.setContentView(R.layout.dialog_searchable_spinner);
//            dialog.getWindow().setLayout(900, 1000);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.show();
//
//            final EditText etSearchCity = dialog.findViewById(R.id.et_search_city);
//            final ListView listView = dialog.findViewById(R.id.list_view);
//
//            String[] cityArray = getResources().getStringArray(R.array.city_array);
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                    android.R.layout.simple_list_item_1, cityArray);
//
//            listView.setAdapter(adapter);
//            etSearchCity.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    adapter.getFilter().filter(charSequence);
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });
//
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    tvSelectCity.setText(adapter.getItem(i));
//                    dialog.dismiss();
//                }
//            });
//        });
    }


    private void setLineChart(){
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);//X軸標籤顏色
        xAxis.setTextSize(12);//X軸標籤大小
        xAxis.setDrawGridLines(false);//不顯示每個座標點對應X軸的線 (預設顯示)

        YAxis yAxis = lineChart.getAxisRight();//獲取右側的軸線
        yAxis.setEnabled(false);//不顯示右側Y軸

    }
}