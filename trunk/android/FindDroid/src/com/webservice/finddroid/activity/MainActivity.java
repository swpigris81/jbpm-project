package com.webservice.finddroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.webservice.finddroid.Constants;
import com.webservice.finddroid.R;
import com.webservice.finddroid.utils.FindDroidPreferenceManager;
import com.webservice.finddroid.utils.StringUtils;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        //判断客户端是否已经注册
        String userName = FindDroidPreferenceManager.getString(Constants.DROID_USERNAME, "");
        if(userName != null && !StringUtils.isEmpty(userName)){
            setContentView(R.layout.activity_main);
        }else{
            //客户端未注册，跳转
            Intent intent = new Intent(MainActivity.this, UserRegisterActivity.class);
            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
        }
        super.onCreate(savedInstanceState);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finish();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }
    /**
     * <p>Discription:[注册页面返回]</p>
     * @param requestCode
     * @param resultCode
     * @param data
     * @author:大牙
     * @update:2013-3-9
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                String userName = FindDroidPreferenceManager.getString(Constants.DROID_USERNAME, "");
                if(userName == null || StringUtils.isEmpty(userName)){
                    Intent intent = new Intent(MainActivity.this, UserRegisterActivity.class);
                    startActivityForResult(intent, Constants.START_ACTIVITY_FOR_RESULT_REQUEST_CODE);
                }else{
                    Log.d(TAG, "用户注册成功，返回主页面");
                    setContentView(R.layout.activity_main);
                }
            }else{
                setContentView(R.layout.activity_main);
                TextView tv = (TextView) findViewById(R.id.textViewa);
                String msg = FindDroidPreferenceManager.getString(Constants.USER_REG_INFO, "");
                tv.setText("用户注册失败，" + msg);
            }
        }
    }
}
