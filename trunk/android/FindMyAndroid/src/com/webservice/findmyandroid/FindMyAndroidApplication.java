package com.webservice.findmyandroid;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.webservice.findmyandroid.util.Constants;
import com.webservice.findmyandroid.util.FindDroidPreferenceManager;
import com.webservice.findmyandroid.util.HttpHelper;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class FindMyAndroidApplication extends Application {
    private static final String TAG = "FindMyAndroidApplication";
    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    public Vibrator mVibrator01;
    
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mLocationClient = new LocationClient( this );
        mLocationClient.registerLocationListener( myListener );
        FindDroidPreferenceManager.init(this);
        super.onCreate();
    }
    
    /**
     * <p>Description: [监听地理位置]</p>
     * @author  <a href="mailto: swpigris81@126.com">大牙</a>
     * @version v1.0
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String latitude = "";
            String lontitude = "";
            String radius = "";
            String addr = "";
            String poi = "";
            if(location != null){
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                latitude = String.valueOf(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                lontitude = String.valueOf(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                radius = String.valueOf(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation){
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                }else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    addr = location.getAddrStr();
                }
                sb.append("\nsdk version : ");
                sb.append(mLocationClient.getVersion());
                sb.append("\nisCellChangeFlag : ");
                sb.append(location.isCellChangeFlag());
                Log.i(TAG, sb.toString());
                sendLocationToServer(latitude, lontitude, radius, addr, poi);
            }
        }

        @Override
        public void onReceivePoi(BDLocation poiLocation) {
            String latitude = "";
            String lontitude = "";
            String radius = "";
            String addr = "";
            String poi = "";
            if(poiLocation != null){
                StringBuffer sb = new StringBuffer(256);
                sb.append("Poi time : ");
                sb.append(poiLocation.getTime());
                sb.append("\nerror code : "); 
                sb.append(poiLocation.getLocType());
                sb.append("\nlatitude : ");
                sb.append(poiLocation.getLatitude());
                latitude = String.valueOf(poiLocation.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(poiLocation.getLongitude());
                lontitude = String.valueOf(poiLocation.getLongitude());
                sb.append("\nradius : ");
                sb.append(poiLocation.getRadius());
                radius = String.valueOf(poiLocation.getRadius());
                if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                    sb.append("\naddr : ");
                    sb.append(poiLocation.getAddrStr());
                    addr = poiLocation.getAddrStr();
                }
                if(poiLocation.hasPoi()){
                    sb.append("\nPoi:");
                    sb.append(poiLocation.getPoi());
                    poi = poiLocation.getPoi();
                }else{              
                    sb.append("noPoi information");
                }
                Log.i(TAG, sb.toString());
                sendLocationToServer(latitude, lontitude, radius, addr, poi);
            }
        }
        /**
         * <p>Discription:[将地理位置定位信息发送到服务器端]</p>
         * @param latitude 维度
         * @param lontitude 经度
         * @param radius 半径（海拔）
         * @param addr 地理位置
         * @param poi 
         * @author:大牙
         * @update:2013-3-13
         */
        private void sendLocationToServer(final String latitude, final String lontitude, final String radius, final String addr, final String poi){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> params = new HashMap<String, String>();
                    String regId = FindDroidPreferenceManager.getString(Constants.DROID_REG_ID, "");
                    params.put("regisId", regId);
                    params.put("latitude", latitude);
                    params.put("lontitude", lontitude);
                    params.put("radius", radius);
                    params.put("addr", addr);
                    params.put("poi", poi);
                    try {
                        String postResult = HttpHelper.post(Constants.LOCATION_SERVER_URL, params);
                        Log.d(TAG, "数据发送结果：" + postResult);
                    } catch (Exception e) {
                        Log.e(TAG, "数据发送失败，" + e.getMessage());
                    }
                }
            }).start();
        }
    }
    
    public class NotifyLister extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance){
            mVibrator01.vibrate(1000);
        }
    }
}
