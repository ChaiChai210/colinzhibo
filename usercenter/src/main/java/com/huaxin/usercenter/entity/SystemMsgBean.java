package com.huaxin.usercenter.entity;

public class SystemMsgBean {

    /**
     * id : 1
     * title : 地方
     * contetnt : dfgfgfhgfhgghghghghgh
     * created_at : 2020-01-21 10:26:29
     * nickname : 系统消息
     * avatar : default.png
     */

    private int id;
    private String title;
    private String contetnt;
    private String created_at;
    private String nickname;
    private String avatar;

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

    public String getContetnt() {
        return contetnt;
    }

    public void setContetnt(String contetnt) {
        this.contetnt = contetnt;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
