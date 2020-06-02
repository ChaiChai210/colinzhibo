package com.huaxin.library.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.blankj.utilcode.util.SPUtils;
import com.huaxin.library.base.BaseApplication;
import com.tencent.mmkv.MMKV;

public class UrlConstants {
    private static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = BaseApplication.instance().getPackageManager().getApplicationInfo(BaseApplication.instance().getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static final String BASE_URL = getMetaDataString("SERVER_HOST");
    //正式环境
//    public static String BASE_URL = "http://api.huatdd.com/";
    //圖片服務器
    public static String IMAGE_SERVER = MMKV.defaultMMKV().decodeString(Constant.SP_IMAGE_PREFIX);
    //社區圖片服務器
    private static String BASE_API = BASE_URL + "api/";
    public static String TESTAPI = BASE_URL + "api/";

    public static String SYSTEM_CONGIG = BASE_API + "System/Config";
    public static String PUBLISH_TEXT = BASE_API + "Article/Create";

    //短信验证码
    public static String VERIFY_CODE = BASE_API + "Person/VerificationCode";
    //图片请求url的前缀
    public static String BASE_IMAGE = BASE_URL + "image/";
    //视频请求url的前缀
//    public static String BASE_VIDEO = BASE_URL + "video/";
    //获取视频列表
    public static String GET_VIDEO_LIST = BASE_API + "Video/List/Recommend";
    //获取视频页面的广告
    public static String GET_BANNER = BASE_API + "System/Advertisement";
    //视频发现
    public static String GET_VIDEO_FIND = BASE_API + "Video/List/Discover";
    //视频详情
    public static String GET_VIDEO_DETAIL = BASE_API + "Video/Detail";
    //根据分类获取视频
    public static String GET_VIDEO_SEARCH = BASE_API + "Video/Search";
    //获取视频分类列表
    public static String GET_VIDEO_CLASS_LIST = BASE_API + "Video/List/Class";
    public static String GET_HOT_CLASS_LIST = BASE_API + "Video/List/HotClass";
    //视频推荐换一批
    public static String GET_VIDEO_FILTER = BASE_API + "Video/Filter";
    public static String VIDEO_SEARCH = BASE_API + "Video/LikeSearch";
    public static String PLAY_STATISTICS = BASE_API + "Video/Count/Play";
    //视频推荐获取视频分类
    public static String GET_VIDEO_CLASS = BASE_API + "Video/RecommendClass";
    //发现视频收藏
    public static String VIDEO_COLLECTION = BASE_API + "Video/Collection";
    //发现视频取消收藏
    public static String VIDEO_CANCEL_COLLECTION = BASE_API + "Video/CancelCollection";
    //获取抖阴视频
    public static String VIDEO_TIKTOK = BASE_API + "Video/List/TikTok";
    //视频详情页面猜你喜欢
    public static String VIDEO_MAYBE_LIKE = BASE_API + "Video/List/MaybeYouLike";
    //视频点赞
    public static String VIDEO_LIKE = BASE_API + "Video/Like";
    //视频取消点赞
    public static String VIDEO_DIS_LIKE = BASE_API + "Video/Dislike";

    //视频详情评论列表
    public static String VIDEO_COMMENT_LIST = BASE_API + "Video/Comment/List";
    //视频详情发送评论
    public static String SEND_COMMENT = BASE_API + "Comment/Create";
    //    视频搜索
    public static String VIDEO_SEARCH_TAG = BASE_API + "Video/HotSearchTags";
    //社区-推荐
    public static String GET_COMMNUNITY_RECOMMEND = BASE_API + "Article/List";
    //社区-详情
    public static String GET_COMMNUNITY_DETAIL = BASE_API + "Article/Detail";
    //社区-推荐-点赞
    public static String POST__LIKE = BASE_API + "CollectionLike/Like";
    public static String ARTICLE_LIKE = BASE_API + "Comment/ArticleLike";
    public static String VideoLike = BASE_API + "Comment/VideoLike";
    public static String VIDEO_SUB_COMMENT = BASE_API + "Comment/SubList";
    //社区-推荐-取消点赞
    public static String POST__DISLIKE = BASE_API + "CollectionLike/Dislike";
    public static String POST_Video_DISLIKE = BASE_API + "StepLike/Like";

    public static String COMMENT_LIST = BASE_API + "Comment/List";
    //社区(视频)-详情-评论列表
    public static String COMMUNITY_COMMENT = BASE_API + "Comment/ArticleList";
    public static String COMMUNITY_SUB_COMMENT = BASE_API + "Comment/SubArticleList";
    //社区-详情-发布评论
    public static String VIDEO_COMMENT = BASE_API + "Comment/Create";
    public static String POST_COMMENT = BASE_API + "Comment/CreateArticle";
    //社区-搜索-热门标签
    public static String COMMUNITY_SEARCH_TAG = BASE_API + "Article/HotSearchTags";
    //社区-搜索-关键字搜索
    public static String COMMUNITY_SEARCH_RESULT = BASE_API + "Article/Search";
    //社区-圈子列表
    public static String COMMUNITY_CIRCLES = BASE_API + "Article/Club/List";
    //    上传视频
//    public static String COMMUNITY_UPLOAD_IMAGE = COMMUNITY_IMAGE_SERVER + ;
//    public static String COMMUNITY_UPLOAD_VIDEO = COMMUNITY_VIDEO_SERVER +  "/api/Article/Create";


    //图像验证码
    public static String CAPTCHA = BASE_API + "System/Captcha";
    //登录
    public static String PERSON_LOGIN = BASE_API + "Person/Login";
    public static String SYSTEM_MSG = BASE_API + "Person/Message";
    public static String PERSON_FOLLOW = BASE_API + "member/getFollowList";
    public static String PERSON_FEEDBACK = BASE_API + "member/getFeedbackType";
    public static String PERSON_ADD_FEEDBACK = BASE_API + "member/addFeedback";
    public static String UPDATE_PROFILE = BASE_API + "member/updateUserInfo";

    public static String UPDATE_AVATAR = IMAGE_SERVER + "/api/member/iconUpload";
    public static String PERSON_FANS = BASE_API + "member/getFansList";
    public static String PAY_LIST = BASE_API + "member/PayList";
    public static String MY_COLLECT_VIDEO = BASE_API + "member/getLike";
    public static String DELETE_COLLECT_VIDEO = BASE_API + "member/likeDel";
    //    我的帖子
    public static String MY_POST = BASE_API + "member/getArticleList";
    public static String PROFILE = BASE_API + "member/info";
    //支付
    public static String PAY = BASE_API + "Pay";
}
