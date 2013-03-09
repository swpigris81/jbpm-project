package com.webservice.finddroid.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.jpush.android.api.JPushInterface;

import com.webservice.finddroid.Constants;
import com.webservice.finddroid.R;
import com.webservice.finddroid.utils.CipherUtil;
import com.webservice.finddroid.utils.ConfigProperties;
import com.webservice.finddroid.utils.FindDroidPreferenceManager;
import com.webservice.finddroid.utils.HttpHelper;

public class UserRegisterActivity extends Activity {
    private final static String TAG = "UserRegisterActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        
        final EditText userNameText = (EditText) this.findViewById(R.id.userName);
        final EditText userPasswordText = (EditText) this.findViewById(R.id.userPassword);
        Button buttonOk = (Button) this.findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userNameText.getText() == null || "".equals(userNameText.getText().toString().trim())){
                    userNameText.setError("用户名不能为空！");
                    return;
                }
                if(userPasswordText.getText() == null || "".equals(userPasswordText.getText().toString().trim())){
                    userPasswordText.setError("用户密码不能为空！");
                    return;
                }
                String userName = userNameText.getText().toString();
                String userPassword = userPasswordText.getText().toString();
                
                //FindDroidPreferenceManager.commitString(Constants.DROID_USERNAME, userName);
                //FindDroidPreferenceManager.commitString(Constants.DROID_PASSWORD, userPassword);
                //String userInfo = userRegister(userName, userPassword);
                registerThread(userName, userPassword);
                //FindDroidPreferenceManager.commitString(Constants.DROID_USERINFO, userInfo);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    
    /**
     * <p>Discription:[监听返回按键]</p>
     * @param keyCode
     * @param event
     * @return
     * @author:小代
     * @update:2013-3-7
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            //返回按键
            EditText userNameText = (EditText) this.findViewById(R.id.userName);
            EditText userPasswordText = (EditText) this.findViewById(R.id.userPassword);
            if(userNameText.getText() == null || "".equals(userNameText.getText().toString().trim())){
                userNameText.setError("用户名不能为空！");
                return false;
            }
            if(userPasswordText.getText() == null || "".equals(userPasswordText.getText().toString().trim())){
                userPasswordText.setError("用户密码不能为空！");
                return false;
            }
            String userName = userNameText.getText().toString();
            String userPassword = userPasswordText.getText().toString();
            
            //FindDroidPreferenceManager.commitString(Constants.DROID_USERNAME, userName);
            //FindDroidPreferenceManager.commitString(Constants.DROID_PASSWORD, userPassword);
            //String userInfo = userRegister(userName, userPassword);
            registerThread(userName, userPassword);
            FindDroidPreferenceManager.commitString(Constants.DROID_USERINFO, "");
            setResult(RESULT_OK);
            finish();
            return super.onKeyDown(keyCode, event);
        }else{
            return false;
        }
    }
    
    public void registerThread(final String userName, final String pass){
        new Thread(new Runnable(){
            @Override
            public void run() {
                userRegister(userName, pass);
            }
            
        }).start();
    }
    /**
     * <p>Discription:[用户注册]</p>
     * @param userName 用户名
     * @param pass 密码
     * @return 用户信息
     * @author:大牙
     * @update:2013-3-9
     */
    private String userRegister(String userName, String pass){
        ConfigProperties properties = ConfigProperties.getInsttance(getApplicationContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("userName", userName);
        params.put("password", pass);
        String userInfo = "";
        try {
            userInfo = HttpHelper.post(properties.getPropertyValues("serverPath", "http://www.daichao.net/WebService"), params);
            Log.d(TAG, "用户注册成功，用户名：" + userName);
            Log.d(TAG, "开始注册JPush");
            //注册JPush
            JPushInterface.setAliasAndTags(UserRegisterActivity.this, userName, null);
            Log.d(TAG, "注册JPush完成，别名：" + userName);
        } catch (Exception e) {
            Log.e(TAG, "用户信息注册失败！" + e.getMessage());
        }
        return userInfo;
    }
}
