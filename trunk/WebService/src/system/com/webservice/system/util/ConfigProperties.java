package com.webservice.system.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * <p>Description: [配置文件操作]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class ConfigProperties {
    private static Log log = LogFactory.getLog(ConfigProperties.class);
    
    private static Properties props = null;
    
    private static Map<String, String> cashePropsMap = new HashMap<String, String>();
    
    private static String projectHomePath;
    
    private static String propertiesFile = "config.properties";
    /**
     * <p>Discription:[获取属性值]</p>
     * @param key 属性ID
     * @return 属性值
     * @author:大牙
     * @update:2012-11-13
     */
    public static String getProperties(String key) {
        if (props == null){
            systemInit();
        }
        String propsValue = cashePropsMap.get(key);
        if (propsValue == null) {
            propsValue = props.getProperty(key);
            cashePropsMap.put(key, propsValue);
        }
        return propsValue;
    }
    /**
     * <p>Discription:[获取INT类型数据]</p>
     * @param key 属性ID
     * @return 属性值
     * @author:大牙
     * @update:2012-11-13
     */
    public static int getIntProperties(String key) {
        return NumberUtils.toInt(getProperties(key), 0);
    }
    /**
     * <p>Discription:[获取工程根目录]</p>
     * @return
     * @author:大牙
     * @update:2012-11-13
     */
    public static String getProjectHomePath() {
        if (props == null){
            systemInit();
        }
        return projectHomePath;
    }
    /**
     * <p>Discription:[取得配置全路径（基路径+配置定义路径）]</p>
     * @param key
     * @return
     * @author:大牙
     * @update:2012-11-13
     */
    public static String getFullPathProperties(String key) {
        return projectHomePath + getProperties(key);
    }
    /**
     * <p>Discription:[读取config.properties文件]</p>
     * @author:大牙
     * @update:2012-11-13
     */
    public static void loadInitSystemProperties() {
        props = new Properties();
//        InputStream in = null;
        try{
//            in = ConfigProperties.class.getClassLoader().getResourceAsStream(propertiesFile);
//            if (in != null) {
//                props.load(in);
//            } else {
//                throw new RuntimeException("无法加载配置文件");
//            }
            
            props.load(new InputStreamReader(ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties"), "UTF-8"));
            
            
            projectHomePath = props.getProperty("project.home.path");
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }finally{
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ioe) {
//                log.error("初始化配置文件异常" + ioe.getMessage(), ioe);
//            }
        }
    }
    
    /**
     * 清除高速缓存
     */
    public static void cleanCashe() {
        cashePropsMap.clear();
    }
    /**
     * <p>Discription:[系统初始化]</p>
     * @author:大牙
     * @update:2012-11-13
     */
    public static void systemInit() {
        loadInitSystemProperties();
    }
    
    /**
     * 取得属性
     * @param key
     * @return
     */
    public static String getProperties(String key, Map<String, String> regexMap) {
        if (props == null){
            systemInit();
        }
        String propsValue = (String) cashePropsMap.get(key);
        if (propsValue == null) {
            propsValue = props.getProperty(key);
            cashePropsMap.put(key, propsValue);
        }
        if (regexMap != null && regexMap.size() > 0) {
            Set<String> keySet = regexMap.keySet();
            for (String regexKey : keySet) {
                propsValue = propsValue.replaceAll("\\$\\{" + regexKey + "}",
                        regexMap.get(regexKey));
            }
        }
        return propsValue;
    }
    
    /**
     * 取得配置全路径（基路径+配置定义路径）
     * 
     * @param key
     * @return
     */
    public static String getFullPathProperties(String key,
            Map<String, String> regexMap) {
        if (props == null) {
            systemInit();
        }
        return projectHomePath + getProperties(key, regexMap);
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String propertiesFile.
     */
    public static String getPropertiesFile() {
        return propertiesFile;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param propertiesFile The propertiesFile to set.
     */
    public static void setPropertiesFile(String propertiesFile) {
        ConfigProperties.propertiesFile = propertiesFile;
    }
    
    public static void main(String[] arg) {
        System.out.println(ConfigProperties.getProperties("defaultRoleName"));
    }
}
