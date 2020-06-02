package com.huaxin.video.entity;

public class TiktokEntity {


    /**
     * id : 248
     * title : 视频上传标题
     * thumb : http://154.83.15.136:8823/storage/video_cover_2/20200403_513d16c066d36674df816d1a7e7028ff.jpg
     * intro : 视频上传描述
     * video_href : http://154.83.15.136:8823/storage/video_2_2/2020-04-03/513d16c066d36674df816d1a7e7028ff.mp4
     * play_count : 0
     * tag : 1,2,3
     * duration : 00:00:32.81
     * needcoin : 0
     * collection_count : 1
     * is_collection : true
     * comment_count : 3
     * tags : 欧美|女同|美臀
     */

    private int id;
    private String title;
    private String thumb;
    private String intro;
    private String video_href;
    private int play_count;
    private String tag;
    private String duration;
    private int needcoin;
    private int collection_count;
    private boolean is_collection;
    private int comment_count;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getVideo_href() {
        return video_href;
    }

    public void setVideo_href(String video_href) {
        this.video_href = video_href;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getNeedcoin() {
        return needcoin;
    }

    public void setNeedcoin(int needcoin) {
        this.needcoin = needcoin;
    }

    public int getCollection_count() {
        return collection_count;
    }

    public void setCollection_count(int collection_count) {
        this.collection_count = collection_count;
    }

    public boolean isIs_collection() {
        return is_collection;
    }

    public void setIs_collection(boolean is_collection) {
        this.is_collection = is_collection;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
