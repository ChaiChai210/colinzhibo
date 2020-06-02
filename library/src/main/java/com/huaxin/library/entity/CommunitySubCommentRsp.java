package com.huaxin.library.entity;

import com.huaxin.library.entity.ChildComment;

import java.util.List;

public class CommunitySubCommentRsp {

    /**
     * countNums : 2
     * data : [{"id":2,"uid":4,"plate_id":98,"text":"水电费水电费是的冯绍峰怪怪的","comment_date":1578934861,"created_at":"2020-01-15 14:34:33","pid":286,"like_nums":0,"puid":0,"userInfo":{"id":"4","nickname":"拼刺刀","phone":"12378977777","address":"","thumb":"http://q68stm6zf.bkt.clouddn.com/accounts/20200229/c64d1c7789e01c6039d5bee0e5f37e03.jpg","sex":"0"}},{"id":1,"uid":3,"plate_id":98,"text":"这难道是人事新骗术","comment_date":1588934861,"created_at":"2020-01-15 14:34:01","pid":286,"like_nums":0,"puid":0,"userInfo":{"id":"3","nickname":"断屌哥","phone":"13878945612","address":"","thumb":"http://q68stm6zf.bkt.clouddn.com/accounts/20200229/230f6446a712e23037654771b36a820e.jpg","sex":"0"}}]
     */

    private int countNums;
    private List<ChildComment> data;

    public int getCountNums() {
        return countNums;
    }

    public void setCountNums(int countNums) {
        this.countNums = countNums;
    }

    public List<ChildComment> getData() {
        return data;
    }

    public void setData(List<ChildComment> data) {
        this.data = data;
    }
}
