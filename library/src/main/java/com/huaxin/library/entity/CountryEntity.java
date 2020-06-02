package com.huaxin.library.entity;


import com.colin.indexview.entity.BaseEntity;

/**
 * @author: laohu on 2016/12/25
 * @site: http://ittiger.cn
 */

public class CountryEntity implements BaseEntity {
    private String countryName;
    private String phoneCode;


    public CountryEntity(String cityName, String phoneCode) {
        countryName = cityName;
        this.phoneCode = phoneCode;
    }

    @Override
    public String getIndexField() {

        return countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
