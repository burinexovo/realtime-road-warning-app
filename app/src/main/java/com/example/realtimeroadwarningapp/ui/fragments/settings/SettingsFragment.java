package com.example.realtimeroadwarningapp.ui.fragments.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.databinding.FragmentSettingsBinding;
import com.example.realtimeroadwarningapp.ui.activities.AboutActivity;
import com.example.realtimeroadwarningapp.ui.activities.CommonAddressActivity;
import com.example.realtimeroadwarningapp.ui.activities.DrivingTypeActivity;
import com.example.realtimeroadwarningapp.ui.activities.FeedbackActivity;
import com.example.realtimeroadwarningapp.ui.activities.WarningDistanceActivity;
import com.example.realtimeroadwarningapp.ui.fragments.map.MapsFragment;
import com.example.realtimeroadwarningapp.utils.Constants;


public class SettingsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private ConstraintLayout clDrivingType, clPowerSaving, clCommonAddress, clVolume, clMessage,
            clWarningDistance, clFeedback, clAbout;
    private Switch switchPowerSaving, switchVolume, switchMessage;
    private ImageView ivHome;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ivHome = binding.ivHome;

        clDrivingType = binding.clDrivingType;
        clPowerSaving = binding.clPowerSaving;
        clCommonAddress = binding.clCommonAddress;
        clVolume = binding.clVolume;
        clMessage = binding.clMessage;
        clWarningDistance = binding.clWarningDistance;
        clFeedback = binding.clFeedback;
        clAbout = binding.clAbout;

        switchPowerSaving = binding.switchPowerSaving;
        switchVolume = binding.switchVolume;
        switchMessage = binding.switchMessage;

        loadData();

        setListener();

        return root;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        boolean isSavePowerCheck = sharedPreferences.getBoolean(Constants.KEY_SAVE_POWER, false);
        boolean isVolumeCheck = sharedPreferences.getBoolean(Constants.KEY_VOLUME, true);
        boolean isMessageCheck = sharedPreferences.getBoolean(Constants.KEY_MESSAGE_NOTIFICATION, true);

        switchPowerSaving.setChecked(isSavePowerCheck);
        switchVolume.setChecked(isVolumeCheck);
        switchMessage.setChecked(isMessageCheck);
    }

    private void setData(String key, boolean isChecked) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private void setListener() {
        ivHome.setOnClickListener(view -> {
            requireActivity().finish();
        });

        clDrivingType.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), DrivingTypeActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        clPowerSaving.setOnClickListener(view -> {
            switchPowerSaving.setChecked(!switchPowerSaving.isChecked());
            setData(Constants.KEY_SAVE_POWER, switchPowerSaving.isChecked());
            // set MainActivity.java power priority
//            MainActivity.getInstance().setPowerPriority(switchPowerSaving.isChecked());
            MapsFragment.getInstance().setPowerPriority(switchPowerSaving.isChecked());
        });

        clCommonAddress.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), CommonAddressActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        clVolume.setOnClickListener(view -> {
            switchVolume.setChecked(!switchVolume.isChecked());
            setData(Constants.KEY_VOLUME, switchVolume.isChecked());
        });

        clMessage.setOnClickListener(view -> {
            switchMessage.setChecked(!switchMessage.isChecked());
            setData(Constants.KEY_MESSAGE_NOTIFICATION, switchMessage.isChecked());
        });

        clWarningDistance.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), WarningDistanceActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        clFeedback.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), FeedbackActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        clAbout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), AboutActivity.class));
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
