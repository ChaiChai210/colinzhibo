package com.huaxin.usercenter.entity;

import com.huaxin.library.db.Download;
import com.huaxin.library.db.WatchRecord;

import java.util.List;

public class DownloadListEntity {
    private String name;
    private List<Download> data;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(List<Download> data) {
        this.data = data;
    }

    public List<Download> getData() {
        return data;
    }
}
