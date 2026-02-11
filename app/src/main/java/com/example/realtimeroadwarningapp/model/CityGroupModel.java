package com.example.realtimeroadwarningapp.model;

public class CityGroupModel {
    private String city;
    private int deathToll;
    private int numberOfInjury;
    private int casualties;

    public CityGroupModel(String city, int deathToll, int numberOfInjury, int casualties) {
        this.city = city;
        this.deathToll = deathToll;
        this.numberOfInjury = numberOfInjury;
        this.casualties = casualties;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getDeathToll() {
        return deathToll;
    }

    public void setDeathToll(int deathToll) {
        this.deathToll = deathToll;
    }

    public int getNumberOfInjury() {
        return numberOfInjury;
    }

    public void setNumberOfInjury(int numberOfInjury) {
        this.numberOfInjury = numberOfInjury;
    }

    public int getCasualties() {
        return casualties;
    }

    public void setCasualties(int casualties) {
        this.casualties = casualties;
    }
}
