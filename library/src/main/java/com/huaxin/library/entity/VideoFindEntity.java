package com.huaxin.library.entity;

public class VideoFindEntity {


    /**
     * id : 11
     * title : 【高清详解】中式性保健_按摩穴位治疗阳痿早泄
     * first_frame_image : /storage/video_cover/2020-01-27/ca017e6ff05e99bd0e35e50cd5247d86.jpg
     * media :
     * play_count : 99
     * is_collection : false
     */

    private int id;
    private String title;
    private String first_frame_image;
    private String media;
    private int play_count;
    private boolean is_collection;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst_frame_image() {
        return first_frame_image;
    }

    public void setFirst_frame_image(String first_frame_image) {
        this.first_frame_image = first_frame_image;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public int getPlay_count() {
        return play_count;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public boolean isIs_collection() {
        return is_collection;
    }

    public void setIs_collection(boolean is_collection) {
        this.is_collection = is_collection;
    }
}
