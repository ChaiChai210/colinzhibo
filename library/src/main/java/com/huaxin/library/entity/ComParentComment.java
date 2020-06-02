package com.huaxin.library.entity;

import java.util.List;

//评论的单条数据
public class ComParentComment {
    private int id;
    private int uid;
    private int plate_id;
    private String text;
    private int comment_date;
    private String created_at;
    private int pid;
    private int like_nums;
    private int isLike;

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getIsLike() {
        return isLike;
    }

    private int puid;
    private UserInfo userInfo;

    private int state;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    private SubListBean subList;

    public static class SubListBean {
        /**
         * countNums : 2
         * subData : [{"id":2,"uid":4,"plate_id":98,"text":"水电费水电费是的冯绍峰怪怪的","comment_date":1578934861,"created_at":"2020-01-15 14:34:33","pid":286,"like_nums":0,"puid":0,"userInfo":{"id":"4","nickname":"拼刺刀","phone":"12378977777","address":"","thumb":"http://q68stm6zf.bkt.clouddn.com/accounts/20200229/c64d1c7789e01c6039d5bee0e5f37e03.jpg","sex":"0"}},{"id":1,"uid":3,"plate_id":98,"text":"这难道是人事新骗术","comment_date":1588934861,"created_at":"2020-01-15 14:34:01","pid":286,"like_nums":0,"puid":0,"userInfo":{"id":"3","nickname":"断屌哥","phone":"13878945612","address":"","thumb":"http://q68stm6zf.bkt.clouddn.com/accounts/20200229/230f6446a712e23037654771b36a820e.jpg","sex":"0"}}]
         */

        private int countNums;
        private List<ChildComment> subData;

        public int getCountNums() {
            return countNums;
        }

        public void setCountNums(int countNums) {
            this.countNums = countNums;
        }

        public List<ChildComment> getSubData() {
            return subData;
        }

        public void setSubData(List<ChildComment> subData) {
            this.subData = subData;
        }

        public SubListBean(int countNums, List<ChildComment> subData) {
            this.countNums = countNums;
            this.subData = subData;
        }
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getLike_nums() {
        return like_nums;
    }

    public void setLike_nums(int like_nums) {
        this.like_nums = like_nums;
    }

    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public SubListBean getSubList() {
        return subList;
    }

    public void setSubList(SubListBean subList) {
        this.subList = subList;
    }
}
