package com.huaxin.library.entity;

import com.huaxin.library.entity.ComParentComment;

import java.util.List;
//评论回传字段
public class CommunityCommentRsp {

    /**
     * countNums : 1
     * pageSize : 10
     * subPageSize : 3
     * data : [{"id":2,"uid":1,"plate_id":2,"text":"这是评论内容","comment_date":1585460484,"created_at":"2020-03-29 13:41:24","pid":0,"like_nums":0,"puid":2,"userInfo":{"nickname":"aaas1","id":"1","thumb":"http://web.yuyingbaobei.com/storage/userIcon/2020-03-14/55ba780f4aecf5c53afce49d7ece37b9.png","address":"上海市","sex":"0","phone":"13888888881"},"subList":{"countNums":0,"subData":[]}}]
     */

    private int countNums;
    private int pageSize;
    private int subPageSize;
    private List<ComParentComment> data;

    public int getCountNums() {
        return countNums;
    }

    public void setCountNums(int countNums) {
        this.countNums = countNums;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSubPageSize() {
        return subPageSize;
    }

    public void setSubPageSize(int subPageSize) {
        this.subPageSize = subPageSize;
    }

    public List<ComParentComment> getData() {
        return data;
    }

    public void setData(List<ComParentComment> data) {
        this.data = data;
    }
}