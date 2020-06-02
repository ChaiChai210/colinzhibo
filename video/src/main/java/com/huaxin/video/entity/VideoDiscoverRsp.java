package com.huaxin.video.entity;

import java.util.List;

public class VideoDiscoverRsp {


    /**
     * current_page : 1
     * data : [{"id":328,"title":"Big Bellies #1_010","thumb":"/storage/my_video_cover/2020-01-30/f97c59fa38054e684328d5e4cd233887.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-30/88c3d185ecdbb4d1c0405dc3ea2356a0.mp4","play_count":0,"is_collection":true},{"id":334,"title":"Big Bellies #1_016","thumb":"/storage/my_video_cover/2020-01-30/607e3126ce689219656b420b96d4c97b.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-30/e23cc23c164f5bc13379fa4cab4def00.mp4","play_count":0,"is_collection":true},{"id":43,"title":"激情啪啪_00","thumb":"/storage/my_video_cover/2020-01-29/8661dbb70a3eb8c4205536f2e6257ba0.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/b04fa8283e2ee27c9465064cb4559c8d.mp4","play_count":0,"is_collection":false},{"id":44,"title":"激情啪啪_01","thumb":"/storage/my_video_cover/2020-01-29/cce7552ff57c21f0943768643627f9d7.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/b14db3812114c3a3977faaa9dc3e1fc3.mp4","play_count":0,"is_collection":false},{"id":45,"title":"激情啪啪_02","thumb":"/storage/my_video_cover/2020-01-29/3e95707457d940307bf0fd1856e211f6.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/7a3fb6d5e333d5af72c758d501b6d7da.mp4","play_count":0,"is_collection":false},{"id":46,"title":"激情啪啪_03","thumb":"/storage/my_video_cover/2020-01-29/3e95707457d940307bf0fd1856e211f6.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/919c7ab35e4474e87c9dd1b28f97cb2d.mp4","play_count":0,"is_collection":false},{"id":47,"title":"激情啪啪_04","thumb":"/storage/my_video_cover/2020-01-29/87cad2c7120c24a3010469f51e7ebc99.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/845c890272e7b99382bf71acbb8ee05e.mp4","play_count":78,"is_collection":false},{"id":48,"title":"激情啪啪_05","thumb":"/storage/my_video_cover/2020-01-29/205042c0e2374f1e6a706e1bf0e5e478.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/fab7309e5270c4a58d894addefb8f71d.mp4","play_count":0,"is_collection":false},{"id":49,"title":"激情啪啪_06","thumb":"/storage/my_video_cover/2020-01-29/82e629d7913572a5d55347cec2768808.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/a1ea9794b8f30b35deaaab9d165d2983.mp4","play_count":0,"is_collection":false},{"id":50,"title":"激情啪啪_07","thumb":"/storage/my_video_cover/2020-01-29/1a881957cd0d17f772d5930efeaece67.jpg","intro":"這是視頻描述","video_href":"/storage/video/2020-01-29/10bedbd95b09102ce4718b1dbfd62c6f.mp4","play_count":0,"is_collection":false}]
     * first_page_url : http://web.yuyingbaobei.com/api/Video/List/Discover?page=1
     * from : 1
     * last_page : 24
     * last_page_url : http://web.yuyingbaobei.com/api/Video/List/Discover?page=24
     * next_page_url : http://web.yuyingbaobei.com/api/Video/List/Discover?page=2
     * path : http://web.yuyingbaobei.com/api/Video/List/Discover
     * per_page : 10
     * prev_page_url : null
     * to : 10
     * total : 235
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
         * id : 328
         * title : Big Bellies #1_010
         * thumb : /storage/my_video_cover/2020-01-30/f97c59fa38054e684328d5e4cd233887.jpg
         * intro : 這是視頻描述
         * video_href : /storage/video/2020-01-30/88c3d185ecdbb4d1c0405dc3ea2356a0.mp4
         * play_count : 0
         * is_collection : true
         */

        private int id;
        private String title;
        private String thumb;
        private String intro;
        private String video_href;
        private int play_count;
        private String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        private boolean is_collection;

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

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
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

        public boolean isIs_collection() {
            return is_collection;
        }

        public void setIs_collection(boolean is_collection) {
            this.is_collection = is_collection;
        }
    }
}
