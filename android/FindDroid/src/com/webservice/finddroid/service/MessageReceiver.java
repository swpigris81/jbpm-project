package com.webservice.finddroid.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.webservice.finddroid.activity.LocationActivity;

/** 
 * <p>Description: [自定义消息接收器]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageReceiver";
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param arg0
     * @param arg1
     * @author:大牙
     * @update:2013-3-15
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras:  " + extras);
        String pushUserName = "";
        if(extras != null && !"".equals(extras.trim())){
            Map<String, Object> extraMap = Json.fromJson(HashMap.class, Lang.inr(extras));
            pushUserName = ObjectUtils.toString(extraMap.get("pushUserName"), "");
        }
        Log.d(TAG, "消息推送人：" + pushUserName);
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
          //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Intent ni = new Intent(context, LocationActivity.class);
            ni.putExtra("sendUserName", pushUserName);
            ni.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            context.startActivity(ni);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
            Intent ni = new Intent(context, LocationActivity.class);
            ni.putExtra("sendUserName", pushUserName);
            ni.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            context.startActivity(ni);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            
            //打开自定义的Activity
            //Intent i = new Intent(context, TestActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i);
            
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
    


}
