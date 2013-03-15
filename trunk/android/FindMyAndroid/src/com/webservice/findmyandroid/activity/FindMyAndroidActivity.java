package com.webservice.findmyandroid.activity;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.webservice.findmyandroid.R;
import com.webservice.findmyandroid.util.Constants;
import com.webservice.findmyandroid.util.FindDroidPreferenceManager;

public class FindMyAndroidActivity extends Activity {
    private final static String TAG = "FindMyAndroidActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        showTip();
        try{
            //验证设备是否支持GCM
            GCMRegistrar.checkDevice(this);
            //验证程序的manifest包含了在开始编写Android程序中所有符合要求的描述(这个方法只有你在开发程序的时候需要)
            GCMRegistrar.checkManifest(this);
            final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
            if (regId == null || "".equals(regId)) {
                Log.d(TAG, "Not registered, showing register page");
                Intent intent = new Intent(FindMyAndroidActivity.this, UserRegisterActivity.class);
                startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
            }else{
                //only for test
                Log.d(TAG, "Already registered, registerId: " + regId);
    //            GCMRegistrar.unregister(getApplicationContext());
    //            Log.d(TAG, "unregistered.");
    //            Intent intent = new Intent(FindMyAndroidActivity.this, UserRegisterActivity.class);
    //            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
                setContentView(R.layout.activity_find_my_android_activity);
            }
        }catch(Exception e){
            Log.e(TAG, "系统异常，" + e.getMessage());
            TextView tv = (TextView) findViewById(R.id.mainTextView);
            tv.setText("抱歉！您的设备不支持GCM服务。请升级您的Android系统版本或者是在手机中使用Google账户登录Google Play商店。");
            setContentView(R.layout.activity_find_my_android_activity);
        }
    }
    /**
     * <p>Discription:[当用户注册页面返回时触发]</p>
     * @param requestCode
     * @param resultCode
     * @param data
     * @author:大牙
     * @update:2013-3-13
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                  String tempUserName = FindDroidPreferenceManager.getString(Constants.DROID_USERNAME, "");
                  String tempUserPass = FindDroidPreferenceManager.getString(Constants.DROID_PASSWORD, "");
                  if(!StringUtils.isEmpty(tempUserName) && !StringUtils.isEmpty(tempUserPass)){
                      String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
                      try{
                          if (regId == null || "".equals(regId)) {
                              Log.d(TAG, "Not registered, registering...");
                              GCMRegistrar.register(getApplicationContext(), Constants.SENDER_ID);
                          }else{
                              setContentView(R.layout.activity_find_my_android_activity);
                          }
                      }catch(Exception e){
                          Log.e(TAG, "手机不支持GCM注册！");
                          TextView tv = (TextView) findViewById(R.id.mainTextView);
                          tv.setText("抱歉！您的设备不支持GCM服务。请升级您的Android系统版本或者是在手机中使用Google账户登录Google Play商店。");
                          setContentView(R.layout.activity_find_my_android_activity);
                          return;
                      }
                  }else{
                      Intent intent = new Intent(FindMyAndroidActivity.this, UserRegisterActivity.class);
                      startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
                  }
            }else{
                Intent intent = new Intent(FindMyAndroidActivity.this, UserRegisterActivity.class);
                startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
            }
        }else{
            Intent intent = new Intent(FindMyAndroidActivity.this, UserRegisterActivity.class);
            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
        }
    }
    /**
     * <p>Discription:[用户注册成功/失败之后显示的提示]</p>
     * @author:大牙
     * @update:2013-3-13
     */
    private void showTip(){
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                boolean bool = bundle.getBoolean("success");
                if(!bool){
                    String msg = bundle.getString("msg");
                    if(msg != null){
                        TextView mainView = (TextView) findViewById(R.id.mainTextView);
                        mainView.setText(msg);
                    }
                }
            }
        }
    }
}
