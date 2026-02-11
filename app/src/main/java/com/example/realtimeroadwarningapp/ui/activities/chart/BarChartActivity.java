package com.example.realtimeroadwarningapp.ui.activities.chart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityBarChartBinding;
import com.example.realtimeroadwarningapp.model.CityGroupModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarChartActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityBarChartBinding binding;
    private ImageView ivHome;
    private BarChart barChart;
    private ProgressBar progressBar;
    private List<CityGroupModel> cityGroupModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        barChart = binding.barChart;
        progressBar = binding.progressBar;

        setBarChart();
        progressBar.setVisibility(View.VISIBLE);
        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_info_bar_chart.php")
                .newBuilder()
                .addQueryParameter("city", "基隆市")
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
                    loadBarChartData();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        barChart.setVisibility(View.VISIBLE);
                        barChart.invalidate();
                        barChart.animateXY(1000, 1000);
                    });
                }
            }
        });
    }

    private void loadBarChartData() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // sort
        Comparator<CityGroupModel> comparator = new Comparator<CityGroupModel>() {
            @Override
            public int compare(CityGroupModel o1, CityGroupModel o2) {
                if (o1.getCasualties() > o2.getCasualties()) {
                    return 1;
                } else if (o1.getCasualties() < o2.getCasualties()){
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        cityGroupModels.sort(comparator);

        for (int i = 0; i < cityGroupModels.size(); i++) {
            barEntries.add(new BarEntry(i, cityGroupModels.get(i).getCasualties()));
            labels.add(cityGroupModels.get(i).getCity());
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, null);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        barDataSet.setValueTextSize(14);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
//
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
//        xAxis.setTextSize(12);

//        barChart.getAxisLeft().setEnabled(false);
//        barChart.getAxisRight().setDrawGridLines(false);

//        pieData.setDrawValues(true);
//        pieData.setValueFormatter(new PercentFormatter(pieChart));
//        pieData.setValueTextSize(12);
//        pieData.setValueTextColor(Color.BLACK);
//
//        pieChart.setData(pieData);
//        pieChart.setMinAngleForSlices(30f);
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        cityGroupModels = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String city = jsonObject.getString("city");
                String deathToll = jsonObject.getString("death_toll");
                String numberOfInjury = jsonObject.getString("number_of_injury");

                int casualties = Integer.parseInt(deathToll) + Integer.parseInt(numberOfInjury);

                cityGroupModels.add(new CityGroupModel(city, Integer.parseInt(deathToll), Integer.parseInt(numberOfInjury), casualties));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBarChart() {
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setDrawGridLines(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}