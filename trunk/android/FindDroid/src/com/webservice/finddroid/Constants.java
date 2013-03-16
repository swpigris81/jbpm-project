package com.webservice.finddroid;

public class Constants {

    public static final String KEY_CHATTING = "chatting";
    public static final String KEY_IS_CHANNEL = "isChannel";
    public static final String KEY_HOST = "host";
    
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_ALL = "all";
    
    public static final String PATH_MAIN = "/main";
    public static final String PATH_CHATTING = "/chatting";
    public static final String PATH_UNREAD = "/api/unreadMessage";
    public static final String PATH_USER = "/api/user";

    public static final String PREF_CURRENT_CHATTING = "pushtalk_chatting";
    public static final String PREF_CURRENT_SERVER = "pushtalk_server";
    
    public static final String DROID_USERNAME = "DROID_USERNAME";
    public static final String DROID_PASSWORD = "DROID_PASSWORD";
    public static final String DROID_USERINFO = "DROID_USERINFO";
    public static final String DROID_REG_ID = "DROID_REG_ID";
    public static final String USER_REG_INFO = "USER_REG_INFO";
    
    
    public static final int START_ACTIVITY_FOR_RESULT_REQUEST_CODE = 0;
    
    /**
     * 基础主机.
     * 本地测试时：http://192.168.7.100:8081.
     * 部署在服务器上时：http://www.daichao.net
     */
    public static final String BASE_HOST = "http://www.daichao.net";
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = BASE_HOST + "/WebService/gcm/gcmRegister.action?method=registration";
    /**
     * 接收定位数据的URL
     */
    public static final String LOCATION_SERVER_URL = BASE_HOST + "/WebService/gcm/gcmLocation.action?method=gcmLocation";

}
