package com.huaxin.library.http;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.lzy.okgo.OkGo;

import java.util.HashMap;

public class HttpUtils {
    /**
     * 获取视频推荐列表
     *
     * @param callback
     * @param <T>
     */
    public static <T> void getVideoList(JsonCallback<T> callback) {
        OkGo.<T>get(UrlConstants.GET_VIDEO_LIST).params(null).execute(callback);
    }

    /**
     * 获取banner广告
     *
     * @param type
     * @param callback
     * @param <T>
     */
    public static <T> void getBanner(int type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("type", type);
        OkGo.<T>post(UrlConstants.GET_BANNER).upJson(JSON.toJSONString(params)).execute(callback);

    }


    public static <T> void postData(String url, HashMap params, JsonCallback<T> callback) {
        OkGo.<T>post(url).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void getData(String url, JsonCallback<T> callback) {
        OkGo.<T>get(url).params(null).execute(callback);
    }

    public static <T> void getData(String url, HashMap params, JsonCallback<T> callback) {
        OkGo.<T>get(url).params(params).execute(callback);
    }


    /**
     * 获取视频详情
     *
     * @param id
     * @param callback
     * @param <T>
     */
    public static <T> void getVideoDetail(int id, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", id);
        OkGo.<T>post(UrlConstants.GET_VIDEO_DETAIL).upJson(JSON.toJSONString(params)).execute(callback);
    }


    public static <T> void getVideoByType(int mPage, int order_by, int mClassId, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("page_size", 10);
        params.put("page", mPage);
        params.put("class_id", mClassId);
        params.put("order_by", order_by);
        OkGo.<T>post(UrlConstants.GET_VIDEO_SEARCH).upJson(JSON.toJSONString(params)).execute(callback);
    }

    /**
     * 获取视频的分类列表
     *
     * @param callback
     * @param <T>
     */
    public static <T> void getVideoType(int type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("type", type);
        OkGo.<T>post(UrlConstants.GET_VIDEO_CLASS_LIST).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void getVideoType(JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        OkGo.<T>post(UrlConstants.GET_HOT_CLASS_LIST).upJson(JSON.toJSONString(params)).execute(callback);
    }

    /**
     * 视频推荐换一批
     *
     * @param id       类型ID
     * @param callback
     * @param <T>
     */
    public static <T> void getVideoFilter(int id, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("type", id);
        OkGo.<T>post(UrlConstants.GET_VIDEO_FILTER).upJson(JSON.toJSONString(params)).execute(callback);

    }


    /**
     * 获取猜你喜欢
     *
     * @param id
     * @param callback
     * @param <T>
     */
    public static <T> void getMaybeLike(int id, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", id);
        OkGo.<T>post(UrlConstants.VIDEO_MAYBE_LIKE).upJson(JSON.toJSONString(params)).execute(callback);

    }


    /**
     * @param id
     * @param deviceId
     * @param type       1:收藏 2：点赞
     * @param plate_type 1:视频 2：社区
     * @param callback
     * @param <T>
     */
    public static <T> void postLike(int id, String deviceId, int type, int plate_type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", id);
        params.put("mobile_code", deviceId);
        params.put("type", type);
        params.put("plate_type", plate_type);
        OkGo.<T>post(UrlConstants.POST__LIKE).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void postDisLike(int id, String deviceId, int type, int plate_type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", id);
        params.put("mobile_code", deviceId);
        params.put("type", type);
        params.put("plate_type", plate_type);
        OkGo.<T>post(UrlConstants.POST__DISLIKE).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void postDisLike(int id, String deviceId, int plate_type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", id);
        params.put("mobile_code", deviceId);
        params.put("plate_type", plate_type);
        OkGo.<T>post(UrlConstants.POST_Video_DISLIKE).upJson(JSON.toJSONString(params)).execute(callback);
    }

    /**
     * @param user_id
     * @param text
     * @param plate_id
     * @param callback
     * @param <T>
     */
    public static <T> void postCommend(int user_id, int pid, int puid, String text, int plate_id, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("plate_id", plate_id);
        params.put("user_id", user_id);
        params.put("pid", pid);
        params.put("puid", puid);
        params.put("text", text);
        OkGo.<T>post(UrlConstants.POST_COMMENT).upJson(JSON.toJSONString(params)).execute(callback);
    }
    public static <T> void postVideoCommend(int user_id, int pid, int puid, String text, int plate_id, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("plate_id", plate_id);
        params.put("user_id", user_id);
        params.put("pid", pid);
        params.put("puid", puid);
        params.put("text", text);
        OkGo.<T>post(UrlConstants.VIDEO_COMMENT).upJson(JSON.toJSONString(params)).execute(callback);
    }
    //旧版的
    public static <T> void getCommendList(int plate_id, int pageNum, int plate_type, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", plate_id);
        params.put("page_size", 10);
        params.put("page", pageNum);
        params.put("plate_type", plate_type);
        OkGo.<T>post(UrlConstants.COMMENT_LIST).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void getCommunityComment(int uid, int plate_id, int offset, String order_by, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", plate_id);
        if (uid != -1) {
            params.put("uid", uid);
        }
        params.put("offset", offset);
        params.put("order_by", order_by);
        OkGo.<T>post(UrlConstants.COMMUNITY_COMMENT).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void getVideoComment(int uid, int plate_id, int offset, String order_by, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("id", plate_id);
        if (uid != -1) {
            params.put("uid", uid);
        }
        params.put("offset", offset);
        params.put("order_by", order_by);
        OkGo.<T>post(UrlConstants.COMMENT_LIST).upJson(JSON.toJSONString(params)).execute(callback);
    }

    public static <T> void getCommunitySubComment(int plate_id, int offset, int pid, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("plate_id", plate_id);
        params.put("offset", offset);
        params.put("pid", pid);
        OkGo.<T>post(UrlConstants.COMMUNITY_SUB_COMMENT).upJson(JSON.toJSONString(params)).execute(callback);
    }
    public static <T> void getVideoSubComment(int plate_id, int offset, int pid, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("plate_id", plate_id);
        params.put("offset", offset);
        params.put("pid", pid);
        OkGo.<T>post(UrlConstants.VIDEO_SUB_COMMENT).upJson(JSON.toJSONString(params)).execute(callback);
    }


    /**
     * 登录
     *
     * @param number
     * @param code
     * @param callback
     * @param <T>
     */
    public static <T> void login(Context mContext, String countryCode, String number, String code, JsonCallback<T> callback) {
        HashMap params = new HashMap<>();
        params.put("country_code", countryCode);
        params.put("mobile", number);
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        params.put("verification_code", code);
        OkGo.<T>post(UrlConstants.PERSON_LOGIN).upJson(JSON.toJSONString(params)).execute(callback);

    }
}
