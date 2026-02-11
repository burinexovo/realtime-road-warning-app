package com.example.realtimeroadwarningapp.utils.cluster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AccidentClusterItem implements ClusterItem {
    private LatLng latLng;
    private String title;
    private String snippet;
    private int type;

    public AccidentClusterItem(LatLng latLng) {
        this.latLng = latLng;
    }

    public AccidentClusterItem(LatLng latLng, int type) {
        this.latLng = latLng;
        this.type = type;
    }

    public AccidentClusterItem(LatLng latLng, String title, String snippet, int type) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.type = type;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getType() {
        return type;
    }
}
