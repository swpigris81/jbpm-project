package com.webservice.findmyandroid.activity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;
import com.webservice.findmyandroid.R;
import com.webservice.findmyandroid.util.Constants;
import com.webservice.findmyandroid.util.FindDroidPreferenceManager;
import com.webservice.findmyandroid.util.HttpHelper;

public class UserRegisterActivity extends Activity {
    public final static String TAG = "UserRegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        
        final EditText userNameText = (EditText) this.findViewById(R.id.userName);
        final EditText userPasswordText = (EditText) this.findViewById(R.id.userPassword);
        final EditText androidAliasText = (EditText) this.findViewById(R.id.androidAliasText);
        Button buttonOk = (Button) this.findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                String androidAlias = androidAliasText.getText().toString();
                getRegisUserInfo(userName, userPassword, androidAlias);
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
            EditText androidAliasText = (EditText) this.findViewById(R.id.androidAliasText);
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
            String androidAlias = androidAliasText.getText().toString();
            getRegisUserInfo(userName, userPassword, androidAlias);
            return super.onKeyDown(keyCode, event);
        }else{
            return false;
        }
    }
    
    /**
     * <p>Discription:[持久化用户名及密码(覆盖操作)]</p>
     * @param userName 用户名
     * @param pass 密码
     * @param regId GCM注册号
     * @return 用户注册结果
     * @author:大牙
     * @update:2013-3-13
     */
    private void getRegisUserInfo(String userName, String pass, String androidAlias){
        //String tempUserName = FindDroidPreferenceManager.getString(Constants.DROID_USERNAME, "");
        //String tempUserPass = FindDroidPreferenceManager.getString(Constants.DROID_PASSWORD, "");
        FindDroidPreferenceManager.commitString(Constants.DROID_USERNAME, userName);
        FindDroidPreferenceManager.commitString(Constants.DROID_PASSWORD, pass);
        FindDroidPreferenceManager.commitString(Constants.DROID_ALIAS, androidAlias);
    }
}
