package com.huaxin.video.entity;

import java.util.List;

/**
 * 视频详情
 */
public class VideoClassifyDetailEntity {


    /**
     * current_page : 1
     * data : [{"id":340,"thumb":"/storage/my_video_cover/2020-01-30/584be896a777a9c061113c2def24344b.jpg","title":"Big Bellies #1_022","play_count":0,"intro":"這是視頻描述"},{"id":339,"thumb":"/storage/my_video_cover/2020-01-30/8bac1da79e086e59dea0291cc87600b4.jpg","title":"Big Bellies #1_021","play_count":0,"intro":"這是視頻描述"},{"id":338,"thumb":"/storage/my_video_cover/2020-01-30/d77d8d3ff1f622c00f59524efac93bdd.jpg","title":"Big Bellies #1_020","play_count":0,"intro":"這是視頻描述"},{"id":337,"thumb":"/storage/my_video_cover/2020-01-30/ca70d913a05553de760e52aeee279249.jpg","title":"Big Bellies #1_019","play_count":0,"intro":"這是視頻描述"},{"id":336,"thumb":"/storage/my_video_cover/2020-01-30/75421ac4ba88b8e78cb117d82ee7bdb7.jpg","title":"Big Bellies #1_018","play_count":0,"intro":"這是視頻描述"},{"id":335,"thumb":"/storage/my_video_cover/2020-01-30/c6ebc0b5be1c26a1c54e8b3aa5e3ae25.jpg","title":"Big Bellies #1_017","play_count":0,"intro":"這是視頻描述"},{"id":334,"thumb":"/storage/my_video_cover/2020-01-30/607e3126ce689219656b420b96d4c97b.jpg","title":"Big Bellies #1_016","play_count":0,"intro":"這是視頻描述"},{"id":333,"thumb":"/storage/my_video_cover/2020-01-30/a8badebb6bb96cf9d3ed4534529fb155.jpg","title":"Big Bellies #1_015","play_count":0,"intro":"這是視頻描述"},{"id":332,"thumb":"/storage/my_video_cover/2020-01-30/6a2aef0b5771cfb893b393a4694b4d08.jpg","title":"Big Bellies #1_014","play_count":0,"intro":"這是視頻描述"},{"id":331,"thumb":"/storage/my_video_cover/2020-01-30/7b7a34a500c7829e634d603b6af8ec11.jpg","title":"Big Bellies #1_013","play_count":0,"intro":"這是視頻描述"}]
     * first_page_url : http://web.yuyingbaobei.com/api/Video/Search?page=1
     * from : 1
     * last_page : 27
     * last_page_url : http://web.yuyingbaobei.com/api/Video/Search?page=27
     * next_page_url : http://web.yuyingbaobei.com/api/Video/Search?page=2
     * path : http://web.yuyingbaobei.com/api/Video/Search
     * per_page : 10
     * prev_page_url : null
     * to : 10
     * total : 261
     */

    private int current_page;
    private String first_page_url;
    private int from;
    private int last_page;
    private String last_page_url;
    private String next_page_url;
    private String path;
    private int per_page;
    private Object prev_page_url;
    private int to;
    private int total;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public void setFirst_page_url(String first_page_url) {
        this.first_page_url = first_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getLast_page_url() {
        return last_page_url;
    }

    public void setLast_page_url(String last_page_url) {
        this.last_page_url = last_page_url;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public Object getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(Object prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 340
         * thumb : /storage/my_video_cover/2020-01-30/584be896a777a9c061113c2def24344b.jpg
         * title : Big Bellies #1_022
         * play_count : 0
         * intro : 這是視頻描述
         */

        private int id;
        private String thumb;
        private String title;
        private int play_count;
        private String intro;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPlay_count() {
            return play_count;
        }

        public void setPlay_count(int play_count) {
            this.play_count = play_count;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }
    }
}
