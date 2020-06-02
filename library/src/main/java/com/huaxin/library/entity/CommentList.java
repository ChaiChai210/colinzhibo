package com.huaxin.library.entity;

public class CommentList {

    /**
     * id : 18
     * uid : 3
     * plate_id : 1
     * text : 都发给你把你们每节课李
     * comment_date : 1578934861
     * plate_type : 1
     * created_at : 2020-01-15 17:27:51
     * updated_at : 2020-01-15 17:27:53
     * nickname : 断屌哥
     * avatar : storage/userIcon/2020-01-28/ab1f95a9729a7c92cb0627c598cf05cb.jpg
     */

    private int id;
    private int uid;
    private int plate_id;
    private String text;
    private int comment_date;
    private String plate_type;
    private String created_at;
    private String updated_at;
    private String nickname;
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPlate_id() {
        return plate_id;
    }

    public void setPlate_id(int plate_id) {
        this.plate_id = plate_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getComment_date() {
        return comment_date;
    }

    public void setComment_date(int comment_date) {
        this.comment_date = comment_date;
    }

    public String getPlate_type() {
        return plate_type;
    }

    public void setPlate_type(String plate_type) {
        this.plate_type = plate_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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
