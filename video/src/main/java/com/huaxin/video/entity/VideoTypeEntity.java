package com.huaxin.video.entity;

public class VideoTypeEntity {

    /**
     * id : 1
     * title : 角色扮演
     * description : 極度吸引男性的性感女人
     * image : video_class/角色扮演.png
     */

    private int id;
    private String name;
    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
