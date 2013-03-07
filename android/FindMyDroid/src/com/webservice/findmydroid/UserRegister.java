package com.webservice.findmydroid;

import com.webservice.findmydroid.client.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserRegister extends Activity {

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
                
                SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
                Editor editor = sharedPrefs.edit();
                editor.putString(Constants.XMPP_USERNAME, userName);
                editor.putString(Constants.XMPP_PASSWORD, userPassword);
                if(editor.commit()){
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_user_register, menu);
//        return true;
//    }

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
            
            SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
            Editor editor = sharedPrefs.edit();
            editor.putString(Constants.XMPP_USERNAME, userName);
            editor.putString(Constants.XMPP_PASSWORD, userPassword);
            if(editor.commit()){
                setResult(RESULT_OK);
            }
            finish();
            return super.onKeyDown(keyCode, event);
        }else{
            return false;
        }
    }
}
