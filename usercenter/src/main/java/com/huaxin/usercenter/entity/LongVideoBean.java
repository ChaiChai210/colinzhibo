package com.huaxin.usercenter.entity;

public class LongVideoBean {

    /**
     * id : 318
     * title : Big Bellies #1_000
     * intro : 這是視頻描述
     * thumb : /storage/my_video_cover/2020-01-30/40ee902a01f8149a3e94be840716c1c2.jpg
     * video_href : /storage/video/2020-01-30/25b8a2ae88026e81c7c41209e039e48b.mp4
     * duration : 00:01:02.50
     * created_at : 2020-02-12 19:14:03
     */

    private int id;
    private String title;
    private String intro;
    private String thumb;
    private String video_href;
    private String duration;
    private String created_at;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
