package com.huaxin.video.entity;

import java.util.List;

public class VideoListEntity{


    private int id;
    private String name;
    private List<VideoEntity> data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VideoEntity> getData() {
        return data;
    }

    public void setData(List<VideoEntity> data) {
        this.data = data;
    }
}
