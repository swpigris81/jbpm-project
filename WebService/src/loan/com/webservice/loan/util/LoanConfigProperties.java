package com.webservice.loan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webservice.system.util.ConfigProperties;

/** 
 * <p>Description: 请款流程配置文件</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class LoanConfigProperties {
    private static Log log = LogFactory.getLog(LoanConfigProperties.class);
    
    private static Properties props = null;
    
    private static Map<String, String> cashePropsMap = new HashMap<String, String>();
    
    private static String projectHomePath;
    
    private static String propertiesFile = "loan.properties";
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
     * <p>Discription:[系统初始化]</p>
     * @author:大牙
     * @update:2012-11-13
     */
    public static void systemInit() {
        props = new Properties();
        InputStream in = null;
        try{
            in = ConfigProperties.class.getClassLoader().getResourceAsStream(propertiesFile);
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException("无法加载配置文件");
            }
            projectHomePath = props.getProperty("project.home.path");
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }finally{
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ioe) {
                log.error("初始化配置文件异常" + ioe.getMessage(), ioe);
            }
        }
    }
}
