package com.example.realtimeroadwarningapp;

import com.example.realtimeroadwarningapp.model.RouteModel;

import java.util.List;

public interface DirectionInterface {
    void onDirectionsStart();
    void onDirectionsSuccess(List<RouteModel> route, String duration, String distance);
}
