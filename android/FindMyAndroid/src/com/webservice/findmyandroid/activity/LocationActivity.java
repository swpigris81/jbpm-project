package com.webservice.findmyandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.webservice.findmyandroid.FindMyAndroidApplication;
import com.webservice.findmyandroid.R;

public class LocationActivity extends Activity {
    private static String TAG = "LocationActivity";
    private LocationClient mLocClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocClient = ((FindMyAndroidApplication)getApplication()).mLocationClient;
        setLocationOption();
        mLocClient.start();
        Log.d(TAG, "地理位置客户端已开始");
        //定位
        mLocClient.requestLocation();
        Log.d(TAG, "地理位置客户端请求定位");
        //mLocClient.requestPoi();
        //离线基站定位按钮
        //mLocClient.requestOfflineLocation();
        //setContentView(R.layout.activity_location);
        Log.d(TAG, "... LocationActiviry onCreate... pid=" + Process.myPid());
        finish();
    }

    @Override
    protected void onDestroy() {
        if(mLocClient != null){
            mLocClient.stop();
            Log.d(TAG, "地理位置客户端已停止");
        }
        super.onDestroy();
    }
    /**
     * <p>Discription:[设置相关参数]</p>
     * @author:大牙
     * @update:2013-3-13
     */
    private void setLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);               //打开gps
        //定位SDK可以返回bd09、bd09ll、gcj02三种类型坐标，若需要将定位点的位置通过百度Android地图 SDK进行地图展示，请返回bd09ll，将无偏差的叠加在百度地图上
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setServiceName("com.baidu.location.service_v2.9");
        //是否需要地址信息
        option.setPoiExtraInfo(true);   
        option.setAddrType("all");
        //设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
        //option.setScanSpan(3000);
        option.setScanSpan(0);
        //设置GPS优先
        option.setPriority(LocationClientOption.GpsFirst); 
        //设置网络优先
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPoiNumber(10);
        option.disableCache(true);
        mLocClient.setLocOption(option);
    }
}
