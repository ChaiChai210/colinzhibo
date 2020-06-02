package com.huaxin.library.entity;

public class UserEntity {

    /**
     * id : 4
     * country_code : +86
     * mobile : 15629693234
     * name :
     * avatar : person/avatar.jpg
     * gender : -1
     * city : 未知的领域
     */

    private int id;
    private String country_code;
    private String mobile;
    private String name;
    private String avatar;
    private int gender;
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
