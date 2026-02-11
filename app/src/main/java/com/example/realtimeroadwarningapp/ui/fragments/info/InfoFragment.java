package com.example.realtimeroadwarningapp.ui.fragments.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.realtimeroadwarningapp.databinding.FragmentInfoBinding;
import com.example.realtimeroadwarningapp.ui.activities.AccidentInfoActivity;
import com.example.realtimeroadwarningapp.ui.activities.AnalysisInfoActivity;
import com.example.realtimeroadwarningapp.ui.activities.RecordsActivity;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;
    private ConstraintLayout clAnalysisInfo, clRealtimeTrafficInfo, clRoadAccident, clAccidentRecord;
    private ImageView ivHome;
    private TextView tvMarquee;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        InfoViewModel infoViewModel =
                new ViewModelProvider(this).get(InfoViewModel.class);

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        clAnalysisInfo = binding.clAnalysisInfo;
        clRealtimeTrafficInfo = binding.clRealtimeTrafficInformation;
        clRoadAccident = binding.clRoadAccident;
        clAccidentRecord = binding.clAccidentRecord;
        ivHome = binding.ivHome;
        tvMarquee = binding.tvMarquee;

        tvMarquee.setSelected(true);
        setListener();
        return root;
    }

    private void setListener() {
        clAnalysisInfo.setOnClickListener(view -> {
           Intent intent = new Intent(getContext(), AnalysisInfoActivity.class);
           startActivity(intent);
        });

        clRealtimeTrafficInfo.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://168.thb.gov.tw/THB168"));
            startActivity(intent);
        });

        clRoadAccident.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AccidentInfoActivity.class);
            startActivity(intent);
        });

        clAccidentRecord.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RecordsActivity.class);
            startActivity(intent);
        });

        ivHome.setOnClickListener(view -> {
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}