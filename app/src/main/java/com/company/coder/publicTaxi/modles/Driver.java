package com.company.coder.publicTaxi.modles;

import android.os.Parcel;
import android.os.Parcelable;

public class Driver implements Parcelable {

    private String name;
    private String contact;
    private String image;
    private String password;
    private String vehicleUUID;
    private String lat;
    private String lng;
    private String pushKey;
    private String ownerUUID;

    public Driver() {
    }

    public Driver(String name, String contact, String image, String password, String vehicleUUID, String lat, String lng, String pushKey, String ownerUUID) {
        this.name = name;
        this.contact = contact;
        this.image = image;
        this.password = password;
        this.vehicleUUID = vehicleUUID;
        this.lat = lat;
        this.lng = lng;
        this.pushKey = pushKey;
        this.ownerUUID = ownerUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getVehicleUUID() {
        return vehicleUUID;
    }

    public void setVehicleUUID(String vehicleUUID) {
        this.vehicleUUID = vehicleUUID;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.contact);
        dest.writeString(this.image);
        dest.writeString(this.password);
        dest.writeString(this.vehicleUUID);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.pushKey);
        dest.writeString(this.ownerUUID);
    }

    protected Driver(Parcel in) {
        this.name = in.readString();
        this.contact = in.readString();
        this.image = in.readString();
        this.password = in.readString();
        this.vehicleUUID = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.pushKey = in.readString();
        this.ownerUUID = in.readString();
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel source) {
            return new Driver(source);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}
