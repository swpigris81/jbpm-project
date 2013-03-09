package com.webservice.finddroid;

import com.webservice.finddroid.utils.FindDroidPreferenceManager;

import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/** 
 * <p>Description: [程序启动。初始化数据或者服务]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class FindDroidApplication extends Application {
    private static final String TAG = "FindDroidApplication";
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        //初始化持久层环境
        FindDroidPreferenceManager.init(getApplicationContext());
        
        JPushInterface.setDebugMode(true);  //设置开启日志,发布时请关闭日志
        JPushInterface.init(this);             // 初始化 JPush
    }

}
