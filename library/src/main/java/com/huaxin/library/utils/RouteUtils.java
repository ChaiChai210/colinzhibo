package com.huaxin.library.utils;

public class RouteUtils {
    //获得home模块fragment
    public static final String Community_Fragment_Main = "/community/main";
    //获得video模块fragment
    public static final String Video_Fragment_Main = "/video/main";
    //获得我的模块fragment
    public static final String Mine_Fragment_Main = "/usercenter/main";
    //获得Me模块fragment
    public static final String Me_Fragment_Main = "/me/main";
    //跳转到登录页面
    public static final String Me_Login = "/chat/main/login";
    //跳转到测试页面
    public static final String Test = "/MyTest/main";
    //跳转到eventBus数据接收页面
    public static final String Me_EventBus = "/me/main/EventBus";
    //跳转到TextOne数据接收页面
    public static final String Me_TextOne = "/me/main/TextOne";
    //跳转到Test公用页面
    public static final String Me_Test = "/me/main/Test";
    //路由拦截
    public static final String Me_Test2 = "/me/main/Test2";
    //跳转到webview页面
    public static final String Me_WebView = "/me/main/WebView";

    //跳转到依赖注入页面
    public static final String Me_Inject = "/me/main/Inject";
    /**
     * 依赖注入使用，注意：必须实现SerializationService进行注册，
     */
    public static final String Home_Json_Service = "/huangxiaoguo/json";

    //跳转ForResult
    public static final String Chat_ForResult = "/chat/main/ForResult";
    //模块间服务调用，调用chat模块的接口
    public static final String Service_Chat = "/chat/service";
    //路由拦截
    public static final String Chat_Interceptor = "/chat/main/Interceptor";

    /**
     * 专门的分组，这里就叫做needLogin组，凡是在这个组下的，都会进行登录操作
     */
    public static final String NeedLogin_Test3 = "/needLogin/main/Test3";
}
