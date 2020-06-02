package com.huaxin.library.entity;

import java.io.Serializable;
import java.util.List;

public class LabelClassify {
    /**
     * name : 情趣
     * circleList : [{"id":16,"name":"青楼名妓","description":"衣带渐宽终不悔，为伊消得人憔悴","avatar":"http://app.yuyingbaobei.com/upload/images/20200120/f29f6388b2e47dbaa6a23e6075e9c4de73791.png"},{"id":17,"name":"與眾不同","description":"所有的爱都是为爱而做.","avatar":"http://app.yuyingbaobei.com/upload/images/20200120/1c9f73594cd80451b30737bfa6fcccd371916.png"}]
     */

    private String name;
    private List<LabelDetail> circleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LabelDetail> getLabelList() {
        return circleList;
    }

    public void setCircleList(List<LabelDetail> circleList) {
        this.circleList = circleList;
    }

}
