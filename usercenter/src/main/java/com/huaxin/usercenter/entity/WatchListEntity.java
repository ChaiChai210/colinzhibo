package com.huaxin.usercenter.entity;

import com.huaxin.library.db.WatchRecord;

import java.util.List;

public class WatchListEntity {
    private String name;
    private List<WatchRecord> data;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(List<WatchRecord> data) {
        this.data = data;
    }

    public List<WatchRecord> getData() {
        return data;
    }
}
