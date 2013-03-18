package com.webservice.jpush.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import cn.jpush.api.DeviceEnum;
import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.webservice.system.util.ConfigProperties;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class TestJPush {
    public static void main(String[] args) {
        JPushClient jpushClient = new JPushClient(
                ConfigProperties.getProperties("jpushApiMasterSecret"),
                ConfigProperties.getProperties("jpushAppKey"),
                NumberUtils.toLong(
                        ConfigProperties.getProperties("jpushLiveTime"), 0),
                DeviceEnum.Android);
        Map<String, Object> extraMap = new HashMap<String, Object>();
        extraMap.put("pushUserName", "admin");
        MessageResult msgResult = jpushClient.sendCustomMessageWithAlias(1, "测试用户", "测试标题", "测试内容", null, extraMap);
        System.out.println("JPUSH服务器返回数据：" + msgResult.toString());
    }
}
