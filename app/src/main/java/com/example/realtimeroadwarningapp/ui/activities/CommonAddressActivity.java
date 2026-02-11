package com.example.realtimeroadwarningapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.RecyclerViewClickInterface;
import com.example.realtimeroadwarningapp.adapter.CommonAddressAdapter;
import com.example.realtimeroadwarningapp.databinding.ActivityCommonAddressBinding;
import com.example.realtimeroadwarningapp.model.CommonAddressModel;
import com.example.realtimeroadwarningapp.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommonAddressActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    private final String TAG = this.getClass().getSimpleName();
    private ImageView ivHome;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button btnAddCommonAddress;
    private CommonAddressAdapter adapter;
    private List<CommonAddressModel> addressModels;
    private CommonAddressModel deletedCommonAddressModelTemp = null;

    private final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:
                    deletedCommonAddressModelTemp = addressModels.get(position);
                    addressModels.remove(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, deletedCommonAddressModelTemp.getTitle(), Snackbar.LENGTH_LONG)
                            .setAction("回復", view -> {
                                addressModels.add(position, deletedCommonAddressModelTemp);
                                adapter.notifyItemInserted(position);
                            }).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCommonAddressBinding binding = ActivityCommonAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivHome = binding.ivHome;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        recyclerView = binding.recyclerView;
        btnAddCommonAddress = binding.btnAddCommonAddress;
        TextView tvNoAddressDescription = binding.tvNoAddressDescription;

        loadData();

        setListener();

        // get parcel
        CommonAddressModel commonAddressModel = getIntent().getParcelableExtra("COMMON_ADDRESS_MODEL");
        if (commonAddressModel != null) addressModels.add(commonAddressModel);

        if (addressModels.size() > 0) {
            tvNoAddressDescription.setVisibility(View.INVISIBLE);
        } else {
            tvNoAddressDescription.setVisibility(View.VISIBLE);
        }

        setRecyclerView();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.KEY_COMMON_ADDRESS, null);
        Type type = new TypeToken<List<CommonAddressModel>>() {
        }.getType();
        addressModels = gson.fromJson(json, type);

        if (addressModels == null) addressModels = new ArrayList<>();
    }

    private void setRecyclerView() {
        adapter = new CommonAddressAdapter(addressModels, this, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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


        btnAddCommonAddress.setOnClickListener(view -> {
            startActivity(new Intent(this, MapsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void onItemClick(int position) {
//        Toast.makeText(this, addressModels.get(position).getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int position) {
//        Toast.makeText(this, addressModels.get(position).getAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setData();
    }

    private void setData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addressModels);
        editor.putString(Constants.KEY_COMMON_ADDRESS, json);
        editor.apply();
    }

}