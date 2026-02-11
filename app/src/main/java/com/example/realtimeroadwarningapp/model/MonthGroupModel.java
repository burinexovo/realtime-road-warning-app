package com.example.realtimeroadwarningapp.model;

public class MonthGroupModel {
    private String month;
    private String deathToll;
    private String numberOfInjury;
    private int casualties;

    public MonthGroupModel(String month, String deathToll, String numberOfInjury, int casualties) {
        this.month = month;
        this.deathToll = deathToll;
        this.numberOfInjury = numberOfInjury;
        this.casualties = casualties;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDeathToll() {
        return deathToll;
    }

    public void setDeathToll(String deathToll) {
        this.deathToll = deathToll;
    }

    public String getNumberOfInjury() {
        return numberOfInjury;
    }

    public void setNumberOfInjury(String numberOfInjury) {
        this.numberOfInjury = numberOfInjury;
    }

    public int getCasualties() {
        return casualties;
    }

    public void setCasualties(int casualties) {
        this.casualties = casualties;
    }
}
