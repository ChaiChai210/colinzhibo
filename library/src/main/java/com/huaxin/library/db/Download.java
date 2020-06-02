package com.huaxin.library.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.liulishuo.okdownload.DownloadTask;

@Entity(tableName = "download")
public class Download {
    @PrimaryKey
    @NonNull
    private int id;
    private String intro;
    private String thumb;
    private String url;
    private String title;
    private String video_href;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Ignore
    private boolean checked;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo_href() {
        return video_href;
    }

    public void setVideo_href(String video_href) {
        this.video_href = video_href;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
