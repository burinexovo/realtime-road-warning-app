package com.example.realtimeroadwarningapp.model;

public class AgeRangeModel {
    private int deathToll;
    private int numberOfInjury;
    private int casualties;
    private float percentage;
    private String label;

    public AgeRangeModel(int deathToll, int numberOfInjury, int casualties, String label) {
        this.deathToll = deathToll;
        this.numberOfInjury = numberOfInjury;
        this.casualties = casualties;
        this.label = label;
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

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
