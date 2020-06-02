package com.huaxin.library.entity;

/**
 * 视频详情
 */
public class VideoDetailEntity {


    /**
     * id : 45
     * title : 激情啪啪_02
     * thumb : /storage/my_video_cover/2020-01-29/3e95707457d940307bf0fd1856e211f6.jpg
     * video_href : /storage/video/2020-01-29/7a3fb6d5e333d5af72c758d501b6d7da.mp4
     * play_count : 0
     * created_at : 2020-01-29 17:35:09
     * is_like : false
     * like_rate : 0
     * comment_count : 0
     */

    private int id;
    private String title;
    private String thumb;
    private String video_href;
    private int play_count;
    private String created_at;
    private boolean is_collection;
    private float like_rate;
    private int comment_count;
    private String intro;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setIs_collection(boolean is_collection) {
        this.is_collection = is_collection;
    }

    public boolean isIs_collection() {
        return is_collection;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setLike_rate(float like_rate) {
        this.like_rate = like_rate;
    }

    public float getLike_rate() {
        return like_rate;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    @Override
    public String toString() {
        return "VideoDetailEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", thumb='" + thumb + '\'' +
                ", video_href='" + video_href + '\'' +
                ", play_count=" + play_count +
                ", created_at='" + created_at + '\'' +
                ", is_collection=" + is_collection +
                ", like_rate=" + like_rate +
                ", comment_count=" + comment_count +
                ", intro='" + intro + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
