package com.example.realtimeroadwarningapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordModel implements Parcelable {
    public static final Creator<RecordModel> CREATOR = new Creator<RecordModel>() {
        @Override
        public RecordModel createFromParcel(Parcel in) {
            return new RecordModel(in);
        }

        @Override
        public RecordModel[] newArray(int size) {
            return new RecordModel[size];
        }
    };
    private String time, name, phone, email, numOfPassengers, injuryStatus, carStatus, causeOfAccident,
            licensePlate, address, latitude, longitude, imagePath;

    public RecordModel() {

    }

    protected RecordModel(Parcel in) {
        time = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        numOfPassengers = in.readString();
        injuryStatus = in.readString();
        carStatus = in.readString();
        causeOfAccident = in.readString();
        licensePlate = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        imagePath = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(String numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
    }

    public String getInjuryStatus() {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus) {
        this.injuryStatus = injuryStatus;
    }

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

    public String getCauseOfAccident() {
        return causeOfAccident;
    }

    public void setCauseOfAccident(String causeOfAccident) {
        this.causeOfAccident = causeOfAccident;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(time);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(numOfPassengers);
        parcel.writeString(injuryStatus);
        parcel.writeString(carStatus);
        parcel.writeString(causeOfAccident);
        parcel.writeString(licensePlate);
        parcel.writeString(address);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(imagePath);
    }
}
