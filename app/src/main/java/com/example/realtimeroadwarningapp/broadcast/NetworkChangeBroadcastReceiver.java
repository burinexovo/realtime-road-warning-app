package com.example.realtimeroadwarningapp.broadcast;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.ui.activities.MapsActivity;
import com.example.realtimeroadwarningapp.utils.ConnectionDetectorUtil;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectionDetectorUtil.isConnectedToInternet(context)) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_check_internet);
            dialog.getWindow().setLayout(900, 1300);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();

            final Button btnRetry = dialog.findViewById(R.id.btn_retry);

            btnRetry.setOnClickListener(view -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}