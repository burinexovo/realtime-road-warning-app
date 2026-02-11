package com.example.realtimeroadwarningapp.model;

public class AccidentModel {
    private String id, region, passAway, injury, numberOfPieces;

    public AccidentModel(String region, String passAway, String injury, String numberOfPieces) {
        this.region = region;
        this.passAway = passAway;
        this.injury = injury;
        this.numberOfPieces = numberOfPieces;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPassAway() {
        return passAway;
    }

    public void setPassAway(String passAway) {
        this.passAway = passAway;
    }

    public String getInjury() {
        return injury;
    }

    public void setInjury(String injury) {
        this.injury = injury;
    }

    public String getNumberOfPieces() {
        return numberOfPieces;
    }

    public void setNumberOfPieces(String numberOfPieces) {
        this.numberOfPieces = numberOfPieces;
    }
}
