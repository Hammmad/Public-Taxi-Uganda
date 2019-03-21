package com.company.coder.publicTaxi.modles;

public class Owner {

    private String name;
    private String phone;
    private String addres;
    private String password;
    private String imageUrl;
    private String pushKey;

    public Owner() {
    }

    public Owner(String name, String phone, String addres, String password, String imageUrl, String pushKey) {
        this.name = name;
        this.phone = phone;
        this.addres = addres;
        this.password = password;
        this.imageUrl = imageUrl;
        this.pushKey = pushKey;
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

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
