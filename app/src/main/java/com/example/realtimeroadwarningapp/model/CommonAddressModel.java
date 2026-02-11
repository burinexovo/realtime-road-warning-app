package com.example.realtimeroadwarningapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class CommonAddressModel implements Parcelable {

    private String title, address;
    private LatLng latLng;

    public CommonAddressModel(String title, String address, LatLng latLng) {
        this.title = title;
        this.address = address;
        this.latLng = latLng;
    }

    protected CommonAddressModel(Parcel in) {
        title = in.readString();
        address = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<CommonAddressModel> CREATOR = new Creator<CommonAddressModel>() {
        @Override
        public CommonAddressModel createFromParcel(Parcel in) {
            return new CommonAddressModel(in);
        }

        @Override
        public CommonAddressModel[] newArray(int size) {
            return new CommonAddressModel[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(address);
        parcel.writeParcelable(latLng, i);
    }
}
