package com.company.coder.publicTaxi.modles;

import android.os.Parcel;
import android.os.Parcelable;

public class Vehicle implements Parcelable {

    private String mMake;
    private String mModel;
    private String mColor;
    private String mPlateNumber;
    private String mStartLat;
    private String mStartLng;
    private String mStartName;
    private String mEndLat;
    private String mEndLng;
    private String mEndName;
    private String mImgUrl;
    private String mOwnerUuid;
    private String mCompanyId;

    public Vehicle() {
    }

    public Vehicle(String mMake, String mModel, String mColor, String mPlateNumber, String mStartLat, String mStartLng, String mStartName, String mEndLat, String mEndLng, String mEndName, String mImgUrl, String mOwnerUuid, String mCompanyId) {
        this.mMake = mMake;
        this.mModel = mModel;
        this.mColor = mColor;
        this.mPlateNumber = mPlateNumber;
        this.mStartLat = mStartLat;
        this.mStartLng = mStartLng;
        this.mStartName = mStartName;
        this.mEndLat = mEndLat;
        this.mEndLng = mEndLng;
        this.mEndName = mEndName;
        this.mImgUrl = mImgUrl;
        this.mOwnerUuid = mOwnerUuid;
        this.mCompanyId = mCompanyId;
    }

    public String getmMake() {
        return mMake;
    }

    public void setmMake(String mMake) {
        this.mMake = mMake;
    }

    public String getmModel() {
        return mModel;
    }

    public void setmModel(String mModel) {
        this.mModel = mModel;
    }

    public String getmColor() {
        return mColor;
    }

    public void setmColor(String mColor) {
        this.mColor = mColor;
    }

    public String getmPlateNumber() {
        return mPlateNumber;
    }

    public void setmPlateNumber(String mPlateNumber) {
        this.mPlateNumber = mPlateNumber;
    }

    public String getmImgUrl() {
        return mImgUrl;
    }

    public void setmImgUrl(String mImgUrl) {
        this.mImgUrl = mImgUrl;
    }

    public String getmStartLat() {
        return mStartLat;
    }

    public void setmStartLat(String mStartLat) {
        this.mStartLat = mStartLat;
    }

    public String getmStartLng() {
        return mStartLng;
    }

    public void setmStartLng(String mStartLng) {
        this.mStartLng = mStartLng;
    }

    public String getmEndLat() {
        return mEndLat;
    }

    public void setmEndLat(String mEndLat) {
        this.mEndLat = mEndLat;
    }

    public String getmEndLng() {
        return mEndLng;
    }

    public void setmEndLng(String mEndLng) {
        this.mEndLng = mEndLng;
    }

    public String getmOwnerUuid() {
        return mOwnerUuid;
    }

    public void setmOwnerUuid(String mOwnerUuid) {
        this.mOwnerUuid = mOwnerUuid;
    }

    public String getmStartName() {
        return mStartName;
    }

    public void setmStartName(String mStartName) {
        this.mStartName = mStartName;
    }

    public String getmEndName() {
        return mEndName;
    }

    public void setmEndName(String mEndName) {
        this.mEndName = mEndName;
    }



    public String getmCompanyId() {
        return mCompanyId;
    }

    public void setmCompanyId(String mCompanyId) {
        this.mCompanyId = mCompanyId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMake);
        dest.writeString(this.mModel);
        dest.writeString(this.mColor);
        dest.writeString(this.mPlateNumber);
        dest.writeString(this.mStartLat);
        dest.writeString(this.mStartLng);
        dest.writeString(this.mStartName);
        dest.writeString(this.mEndLat);
        dest.writeString(this.mEndLng);
        dest.writeString(this.mEndName);
        dest.writeString(this.mImgUrl);
        dest.writeString(this.mOwnerUuid);
        dest.writeString(this.mCompanyId);
    }

    protected Vehicle(Parcel in) {
        this.mMake = in.readString();
        this.mModel = in.readString();
        this.mColor = in.readString();
        this.mPlateNumber = in.readString();
        this.mStartLat = in.readString();
        this.mStartLng = in.readString();
        this.mStartName = in.readString();
        this.mEndLat = in.readString();
        this.mEndLng = in.readString();
        this.mEndName = in.readString();
        this.mImgUrl = in.readString();
        this.mOwnerUuid = in.readString();
        this.mCompanyId = in.readString();
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel source) {
            return new Vehicle(source);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };
}
