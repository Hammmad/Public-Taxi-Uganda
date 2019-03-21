package com.company.coder.publicTaxi.modles;

public class User {

    private String name;
    private String contact;
    private String pushKey;
    private String lat;
    private String lng;
    private String password;

    public User() {
    }

    public User(String name, String contact, String pushKey, String lat, String lng, String password) {
        this.name = name;
        this.contact = contact;
        this.pushKey = pushKey;
        this.lat = lat;
        this.lng = lng;
        this.password = password;
    }

    public User(String name, String contact, String pushKey, String password) {
        this.name = name;
        this.contact = contact;
        this.pushKey = pushKey;
        this.password = password;
    }

    public User(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
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

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
