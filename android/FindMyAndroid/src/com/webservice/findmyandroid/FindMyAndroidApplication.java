package com.webservice.findmyandroid;

import com.webservice.findmyandroid.util.FindDroidPreferenceManager;

import android.app.Application;
import android.util.Log;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class FindMyAndroidApplication extends Application {
    private static final String TAG = "FindMyAndroidApplication";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        FindDroidPreferenceManager.init(this);
        super.onCreate();
    }
}
