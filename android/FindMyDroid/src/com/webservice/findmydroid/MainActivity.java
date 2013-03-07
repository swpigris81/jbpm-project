package com.webservice.findmydroid;


import com.webservice.findmydroid.client.Constants;
import com.webservice.findmydroid.client.NotificationService;
import com.webservice.findmydroid.client.ServiceManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
    
    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        
        //判断手机中是否存在用户名，不存在则显示注册页面
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        String username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
        //String password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
        //用于测试时，增加true，否则去掉
        if(username == null || "".equals(username.trim())){
            //跳转到用户注册页面。
            Intent intent = new Intent(MainActivity.this, UserRegister.class);
            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
            //startActivity(intent);
        }else{
            Log.d("MainActivity", "当前手机中存储的用户名：" + username);
            setContentView(R.layout.activity_main);
            // Settings
            Button okButton = (Button) findViewById(R.id.btn_settings);
            okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ServiceManager.viewNotificationSettings(MainActivity.this);
                }
            });
            // Start the service
            ServiceManager serviceManager = new ServiceManager(this);
            if(!serviceManager.isServerRunning(NotificationService.SERVICE_NAME)){
                serviceManager.setNotificationIcon(R.drawable.notification);
                serviceManager.startService();
            }
        }
        Log.d("MainActivity", "测试使用");
    }
//
    //创建可选项菜单
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    /**
     * <p>Discription:[接收来自注册页面传回的值]</p>
     * @param requestCode
     * @param resultCode
     * @param data
     * @author:小代
     * @update:2013-3-7
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                /*取得来自B页面的数据，并显示到画面*/
                String name = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
                String pass = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
                if(name == null || "".equals(name.trim())){
                    //跳转到用户注册页面。
                    Intent intent = new Intent(MainActivity.this, UserRegister.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
                }else{
                    setContentView(R.layout.activity_main);
                    // Settings
                    Button okButton = (Button) findViewById(R.id.btn_settings);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            ServiceManager.viewNotificationSettings(MainActivity.this);
                        }
                    });
                    // Start the service
                    ServiceManager serviceManager = new ServiceManager(this);
                    if(!serviceManager.isServerRunning(NotificationService.SERVICE_NAME)){
                        serviceManager.setNotificationIcon(R.drawable.notification);
                        serviceManager.startService();
                    }
                }
                //Log.d("MainActivity", "用户提交的注册用户名：" + name + "密码：" + pass);
            }
        }
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return SharedPreferences sharedPrefs.
     */
    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param sharedPrefs The sharedPrefs to set.
     */
    public void setSharedPrefs(SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
    }
}
