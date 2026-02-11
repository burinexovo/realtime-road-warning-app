package com.example.realtimeroadwarningapp.ui.activities.chart;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.ActivityPieChartBinding;
import com.example.realtimeroadwarningapp.model.AgeRangeModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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

public class PieChartActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ActivityPieChartBinding binding;
    private PieChart pieChart;
    private TextView tvSelectCarType;
    private ImageView ivHome;
    private Button btnSearch;
    private ProgressBar progressBar;
    private List<AgeRangeModel> ageRangeModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPieChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        pieChart = binding.pieChart;
        tvSelectCarType = binding.tvSelectCarType;
        btnSearch = binding.btnSearch;
        progressBar = binding.progressBar;

        setPieChart();

        HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_info_pie_chart.php").newBuilder()
                .addQueryParameter("carType", "行人")
                .build();

        progressBar.setVisibility(View.VISIBLE);
        executeSQL(url);

        setListener();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            onBackPressed();
        });

        tvSelectCarType.setOnClickListener(view -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_car_type_spinner);
            dialog.getWindow().setLayout(900, 780);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            final ListView listView = dialog.findViewById(R.id.list_view);

            String[] carType = getResources().getStringArray(R.array.car_type_array);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, carType);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tvSelectCarType.setText(adapter.getItem(i));
                    dialog.dismiss();
                }
            });
        });

        btnSearch.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String carType = tvSelectCarType.getText().toString();

            HttpUrl url = HttpUrl.parse(getString(R.string.ip) + "accident_info_pie_chart.php")
                    .newBuilder()
                    .addQueryParameter("carType", carType)
                    .build();

            executeSQL(url);

        });
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
                    calculatePercentage();
                    loadPieChartData();
                    runOnUiThread(() -> {
                        pieChart.invalidate();
                        pieChart.animateY(1400, Easing.EaseInOutQuad);//動畫
                        pieChart.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            }
        });
    }

    private void setPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("今年度全國死傷率");
        pieChart.setCenterTextSize(16);
        pieChart.getDescription().setEnabled(false);

        List<Integer> colors = new ArrayList<>();

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.LIBERTY_COLORS) {
            colors.add(color);
        }

        List<String> labels = new ArrayList<>();
        labels.add("兒童(0-11歲)");
        labels.add("青少年(12-24歲)");
        labels.add("成年人(25-29歲)");
        labels.add("中壯年(30-64歲)");
        labels.add("老年人(65歲以上)");
        labels.add("不明");

        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors.get(i);
            entry.label = labels.get(i);
            entries.add(entry);
        }
        Legend legend = pieChart.getLegend();
        legend.setCustom(entries);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }

    private void loadPieChartData() {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colorsContainer = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colorsContainer.add(color);
        }

        for (int color : ColorTemplate.LIBERTY_COLORS) {
            colorsContainer.add(color);
        }

        for (int i = 0; i < ageRangeModels.size(); i++) {
            if (ageRangeModels.get(i).getPercentage() == 0.0) continue;
            colors.add(colorsContainer.get(i));
            pieEntries.add(new PieEntry(ageRangeModels.get(i).getPercentage(), ageRangeModels.get(i).getLabel()));
        }


        PieDataSet pieDataSet = new PieDataSet(pieEntries, null);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(12);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setMinAngleForSlices(30f);
    }

    private void calculatePercentage() {
        float totalCasualties = 0;
        for (AgeRangeModel ageRangeModel : ageRangeModels) {
            totalCasualties += ageRangeModel.getCasualties();
        }

        for (AgeRangeModel ageRangeModel : ageRangeModels) {
            float percentage = ageRangeModel.getCasualties() / totalCasualties;
            percentage = (float) (Math.round(percentage * 100.0) / 100.0);

            Log.e(TAG, "calculatePercentage: " + percentage);
            ageRangeModel.setPercentage(percentage);
        }
    }

    private void parseJSON(String result) {
        if (result.isEmpty()) return;
        ageRangeModels = new ArrayList<>();

        List<String> labels = new ArrayList<>();
        labels.add("兒童");
        labels.add("青少年");
        labels.add("成年人");
        labels.add("中壯年");
        labels.add("老年人");
        labels.add("不明");

        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String deathToll = jsonObject.getString("death_toll");
                String numberOfInjury = jsonObject.getString("number_of_injury");

                if (deathToll.equals("null")) deathToll = "0";
                if (numberOfInjury.equals("null")) numberOfInjury = "0";

                int casualties = Integer.parseInt(deathToll) + Integer.parseInt(numberOfInjury);

                ageRangeModels.add(new AgeRangeModel(Integer.parseInt(deathToll), Integer.parseInt(numberOfInjury), casualties, labels.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}