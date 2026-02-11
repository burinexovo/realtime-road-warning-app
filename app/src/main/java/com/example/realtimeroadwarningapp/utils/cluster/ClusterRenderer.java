package com.example.realtimeroadwarningapp.utils.cluster;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.realtimeroadwarningapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterRenderer extends DefaultClusterRenderer<AccidentClusterItem> {

    private final Context context;
    private final IconGenerator iconGenerator;

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<AccidentClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.iconGenerator = new IconGenerator(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull AccidentClusterItem item, @NonNull MarkerOptions markerOptions) {
        // type -> icon
        if (item.getType() == 1) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.slow));
        }
    }

}
