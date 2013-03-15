package com.webservice.findmyandroid;

import java.util.HashMap;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.webservice.findmyandroid.activity.FindMyAndroidActivity;
import com.webservice.findmyandroid.activity.LocationActivity;
import com.webservice.findmyandroid.util.Constants;
import com.webservice.findmyandroid.util.FindDroidPreferenceManager;
import com.webservice.findmyandroid.util.HttpHelper;

/**
 * @author Jason
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
    private static String TAG = "GCMIntentService";
    /**
     * <p>Discription:[当设备试图注册或注销时，但是GCM返回错误时此方法会被调用。通常此方法就是分析错误并修复问题而不会做别的事情。]</p>
     * @param context
     * @param errorId
     * @author:大牙
     * @update:2013-3-12
     */
    protected void onError(Context context, String errorId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "异常：" + errorId);
        if("AUTHENTICATION_FAILED".equals(errorId)){
            Log.d(TAG, "认证失败！");
            errorId += "，请检查您的网络是否正常！";
        }
        Intent intent = new Intent(context, FindMyAndroidActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        intent.putExtra("success", false);
        intent.putExtra("msg", "异常：" + errorId);
        context.startActivity(intent);
    }

    /**
     * <p>Discription:[当你的服务器发送了一个消息到GCM后会被调用，并且GCM会把这个消息传送到相应的设备。如果这个消息包含有效负载数据，它们的内容会作为Intent的extras被传送。]</p>
     * @param context
     * @param intent
     * @author:大牙
     * @update:2013-3-12
     */
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "收到消息触发");
        String sendUserName = intent.getStringExtra("sendUserName");
        Intent ni = new Intent(context, LocationActivity.class);
        ni.putExtra("sendUserName", sendUserName);
        ni.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        context.startActivity(ni);
    }

    /**
     * <p>Discription:[收到注册Intent后此方法会被调用, GCM分配的注册ID会做为参数传递到设备/应用程序对, 通常，你应该发送regid到你的服务器，这样服务器就可以根据这个regid发消息到设备上]</p>
     * @param context
     * @param regId
     * @author:大牙
     * @update:2013-3-12
     */
    protected void onRegistered(Context context, String regId) {
        Log.d(TAG, "用户注册的唯一编号是：" + regId + "，开始向服务器端发送注册信息");
        FindDroidPreferenceManager.commitString(Constants.DROID_REG_ID, regId);
        String tempUserName = FindDroidPreferenceManager.getString(Constants.DROID_USERNAME, "");
        String tempUserPass = FindDroidPreferenceManager.getString(Constants.DROID_PASSWORD, "");
        String androidAlias = FindDroidPreferenceManager.getString(Constants.DROID_ALIAS, "");
        regisThread(context, regId, tempUserName, tempUserPass, androidAlias);
        Log.d(TAG, "用户注册时触发");
    }

    /**
     * <p>Discription:[ 当设备从GCM注销时会被调用。通常你应该发送regid到服务器，这样就可以注销这个设备了]</p>
     * @param context
     * @param regId
     * @author:大牙
     * @update:2013-3-12
     */
    protected void onUnregistered(Context context, String regId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "用户注销时触发");
    }

    /**
     * <p>Discription:[ 当设备试图注册或注销时，但是GCM服务器无效时。GCM库会使用应急方案重试操作，除非这个方式被重写并返回false。这个方法是可选的并且只有当你想显示信息给用户或想取消重试操作的时候才会被重写。]</p>
     * @param context
     * @param errorId
     * @return true: GCM库会使用应急方案重试操作
     * @author:大牙
     * @update:2013-3-12
     */
    protected boolean onRecoverableError(Context context, String errorId) {
        // TODO Auto-generated method stub
        return super.onRecoverableError(context, errorId);
    }
    
    private void regisThread(final Context context, final String regisId, final String userName, final String password, final String androidAlias){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("userPass", password);
                params.put("regisId", regisId);
                params.put("androidAlias", androidAlias);
                try {
                    String userInfo = HttpHelper.post(Constants.SERVER_URL, params);
                    Log.d(TAG, "用户注册结果：" + userInfo);
                    Map<String, Object> resultMap = Json.fromJson(HashMap.class, Lang.inr(userInfo));
                    Boolean bool = (Boolean) resultMap.get("success");
                    Intent intent = new Intent(context, FindMyAndroidActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                    if(bool){
                        Log.d(TAG, "用户注册成功");
                        intent.putExtra("success", true);
                        context.startActivity(intent);
                    }else{
                        Log.d(TAG, "用户注册失败，失败原因：" + resultMap.get("msg"));
                        intent.putExtra("success", false);
                        intent.putExtra("msg", "用户注册失败，失败原因：" + resultMap.get("msg"));
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "用户注册失败，失败原因：" + e.getMessage());
                    GCMRegistrar.unregister(getApplicationContext());
                }
            }
        }).start();
    }
}
