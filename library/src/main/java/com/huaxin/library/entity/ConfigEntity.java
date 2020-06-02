package com.huaxin.library.entity;

public class ConfigEntity {


    /**
     * ios : {"latest_version":"1.01","force_update":false,"package_path":"fgdfgfdg.pak","update_info":"法山豆根地方地方不大"}
     * android : {"latest_version":"2.0.1","force_update":false,"package_path":"sfgsfgasdfg.ipo","update_info":"发嘎斯豆腐干大法官v贝多芬"}
     * image_server : web.yuyingbaobei.com
     * video_server : web.yuyingbaobei.com
     * live_streams_server :
     * promote_url :
     */

    private AndroidBean android;
    private String image_server;
    private String video_server;
    private String live_streams_server;
    private String promote_url;
    private String app_download;
    private String api_url;
    private String main_url;
    private String watermark;
    private String sq_img_server;
    private String sq_video_server;
    private String follow_potato_link;
    private String follow_telegram_link;
    private String follow_weibo_link;
    private String follow_github_link;

    public String getFollow_potato_link() {
        return follow_potato_link;
    }

    public void setFollow_potato_link(String follow_potato_link) {
        this.follow_potato_link = follow_potato_link;
    }

    public String getFollow_telegram_link() {
        return follow_telegram_link;
    }

    public void setFollow_telegram_link(String follow_telegram_link) {
        this.follow_telegram_link = follow_telegram_link;
    }

    public String getFollow_weibo_link() {
        return follow_weibo_link;
    }

    public void setFollow_weibo_link(String follow_weibo_link) {
        this.follow_weibo_link = follow_weibo_link;
    }

    public String getFollow_github_link() {
        return follow_github_link;
    }

    public void setFollow_github_link(String follow_github_link) {
        this.follow_github_link = follow_github_link;
    }

    public String getSq_img_server() {
        return sq_img_server;
    }

    public void setSq_img_server(String sq_img_server) {
        this.sq_img_server = sq_img_server;
    }

    public String getSq_video_server() {
        return sq_video_server;
    }

    public void setSq_video_server(String sq_video_server) {
        this.sq_video_server = sq_video_server;
    }

    public String getMain_url() {
        return main_url;
    }

    public void setMain_url(String main_url) {
        this.main_url = main_url;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public String getApp_download() {
        return app_download;
    }

    public void setApp_download(String app_download) {
        this.app_download = app_download;
    }

    public String getApi_url() {
        return api_url;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public AndroidBean getAndroid() {
        return android;
    }

    public void setAndroid(AndroidBean android) {
        this.android = android;
    }

    public String getImage_server() {
        return image_server;
    }

    public void setImage_server(String image_server) {
        this.image_server = image_server;
    }

    public String getVideo_server() {
        return video_server;
    }

    public void setVideo_server(String video_server) {
        this.video_server = video_server;
    }

    public String getLive_streams_server() {
        return live_streams_server;
    }

    public void setLive_streams_server(String live_streams_server) {
        this.live_streams_server = live_streams_server;
    }

    public String getPromote_url() {
        return promote_url;
    }

    public void setPromote_url(String promote_url) {
        this.promote_url = promote_url;
    }


    public static class AndroidBean {
        /**
         * latest_version : 2.0.1
         * force_update : false
         * package_path : sfgsfgasdfg.ipo
         * update_info : 发嘎斯豆腐干大法官v贝多芬
         */

        private String latest_version;
        private boolean force_update;
        private String package_path;
        private String update_info;
        private int version_code;

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getLatest_version() {
            return latest_version;
        }

        public void setLatest_version(String latest_version) {
            this.latest_version = latest_version;
        }

        public boolean isForce_update() {
            return force_update;
        }

        public void setForce_update(boolean force_update) {
            this.force_update = force_update;
        }

        public String getPackage_path() {
            return package_path;
        }

        public void setPackage_path(String package_path) {
            this.package_path = package_path;
        }

        public String getUpdate_info() {
            return update_info;
        }

        public void setUpdate_info(String update_info) {
            this.update_info = update_info;
        }
    }
}
