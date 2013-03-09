package com.webservice.finddroid.utils;

import java.util.Properties;

import android.content.Context;
import android.util.Log;

/** 
 * <p>Description: [读取配置文件]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class ConfigProperties {
    private final static String LOGTAG = "ConfigProperties";
    private Properties props;
    private Context context;
    private static ConfigProperties instance;
    
    public ConfigProperties(Context context){
        this.context = context;
        props = loadProperties();
    }
    
    public static ConfigProperties getInsttance(Context context){
        if(instance == null){
            instance = new ConfigProperties(context);
        }
        return instance;
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("config", "raw",
                    context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
        }
        return props;
    }
    
    public String getPropertyValues(String propertyName, String defaultValue){
        String value = props.getProperty(propertyName, defaultValue);
        return value;
    }
}
