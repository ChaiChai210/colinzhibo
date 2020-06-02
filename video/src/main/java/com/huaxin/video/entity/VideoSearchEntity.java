package com.huaxin.video.entity;

public class VideoSearchEntity {


    /**
     * id : 47
     * title : 激情啪啪_04
     * thumb : /storage/my_video_cover/2020-01-29/87cad2c7120c24a3010469f51e7ebc99.jpg
     * video_href : /storage/video/2020-01-29/845c890272e7b99382bf71acbb8ee05e.mp4
     * play_count : 78
     * tag : 1,2,3
     * collection_count : 32
     * is_collection : true
     * comment_count : 15
     * tags : 欧美|女同|美臀
     *
     *             "play_count": 0,
     *             "intro": "這是視頻描述",
     *             "tag": "1,2,3",
     *             "tags": "欧美|女同|美臀"
     */

    private int id;
    private String title;
    private String thumb;
    private int play_count;
    private String tag;
    private String tags;

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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getPlay_count() {
        return play_count;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
