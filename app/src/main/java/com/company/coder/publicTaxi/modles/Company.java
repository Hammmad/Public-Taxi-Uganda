package com.company.coder.publicTaxi.modles;

public class Company {

    private String address;
    private String email;
    private String fax;
    private String name;
    private String phone;
    private String pushKey;
    private String region;
    private String webUrl;

    public Company() {
    }

    public Company(String address, String email, String fax, String name, String phone, String pushKey, String region, String webUrl) {
        this.address = address;
        this.email = email;
        this.fax = fax;
        this.name = name;
        this.phone = phone;
        this.pushKey = pushKey;
        this.region = region;
        this.webUrl = webUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String toString() {
        return name;
    }
}
