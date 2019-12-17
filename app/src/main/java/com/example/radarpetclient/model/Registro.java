package com.example.radarpetclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Registro implements Parcelable {

    public String imei;
    public String timeline;
    public String latitude;
    public String longitude;
    public String UUID;

    public Registro(String imei, String timeline, String latitude, String longitude, String UUID) {
        this.imei = imei;
        this.timeline = timeline;
        this.latitude = latitude;
        this.longitude = longitude;
        this.UUID = UUID;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
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

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    private Registro(Parcel from) {
        imei = from.readString();
        timeline = from.readString();
        latitude = from.readString();
        longitude = from.readString();
        UUID = from.readString();
    }

    public static final Parcelable.Creator<Registro>
    CREATOR = new Parcelable.Creator<Registro>() {

        @Override
        public Registro createFromParcel(Parcel source) {
            return new Registro(source);
        }

        @Override
        public Registro[] newArray(int size) {
            return new Registro[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imei);
        dest.writeString(timeline);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(UUID);
    }
}
