package com.webservice.jpush.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import cn.jpush.api.DeviceEnum;
import cn.jpush.api.ErrorCodeEnum;
import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.webservice.common.action.BaseAction;
import com.webservice.system.user.service.IUserService;
import com.webservice.system.util.ConfigProperties;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class JPushAction extends BaseAction {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户密码
     */
    private String userPass;
    /**
     * 设备IMEI
     */
    private String phoneImei;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 用户服务
     */
    private IUserService userService;
    /**
     * Jpush推送客户端
     */
    private static JPushClient jpushClient = null;
    /**
     * 计数器
     */
    private static int sendNo = 0;
    
    static{
        jpushClient = new JPushClient(
                ConfigProperties.getProperties("jpushApiMasterSecret"),
                ConfigProperties.getProperties("jpushAppKey"),
                NumberUtils.toLong(
                        ConfigProperties.getProperties("jpushLiveTime"), 0),
                DeviceEnum.Android);
    }
    /**
     * <p>Discription:[用户列表]</p>
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public String userList(){
        List userList = userService.findImeiUserList(start, limit);
        int size = userService.findCountImeiUser();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("userList", userList);
        resultMap.put("totalCount", size);
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[消息推送]</p>
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public String push(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if((userName == null || "".equals(userName.trim())) && (phoneImei == null || "".equals(phoneImei.trim()))){
            resultMap.put("success", false);
            resultMap.put("msg", "用户名或者设备IMEI必须输入其中一个才能正常推送消息！");
        }else if(content == null || "".equals(content.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "请确保推送消息不为空！");
        }else{
            resultMap.put("success", true);
            MessageResult msgResult = null;
            if(userName.indexOf(",") > -1){
                String [] userArray = userName.split(",");
                for(String user : userArray){
                    sendNo ++;
                    //发送通知
                    //sendCustomMessageWithAlias
                    msgResult = jpushClient.sendNotificationWithAlias(sendNo, user, "测试标题", content);
                    
                    if(msgResult != null){
                        LOG.info("JPUSH服务器返回数据：" + msgResult.toString());
                        if(msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()){
                            //发送成功
                            resultMap.put(user, "消息发送成功");
                        }else{
                            LOG.info("发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
                            resultMap.put(user, "消息发送失败，失败原因：" + msgResult.getErrmsg());
                        }
                    }else{
                        LOG.info("无法获取服务器返回数据");
                        resultMap.put(user, "消息发送失败，失败原因：无法获取推送服务器返回的数据！");
                    }
                }
            }else{
            boolean bool = true;
                if(userName != null && !"".equals(userName.trim())){
                    sendNo ++;
                    bool = true;
                    //发送通知
                    //sendCustomMessageWithAlias
                    msgResult = jpushClient.sendNotificationWithAlias(sendNo, userName, "测试标题", content);
                }else{
                    sendNo ++;
                    bool = false;
                    //sendCustomMessageWithImei
                    msgResult = jpushClient.sendNotificationWithImei(sendNo, phoneImei, "测试标题", content);
                }
                if(msgResult != null){
                    LOG.info("JPUSH服务器返回数据：" + msgResult.toString());
                    if(msgResult.getErrcode() == ErrorCodeEnum.NOERROR.value()){
                        //发送成功
                        if(bool){
                            resultMap.put(userName, "消息发送成功");
                        }else{
                            resultMap.put(phoneImei, "消息发送成功");
                        }
                    }else{
                        LOG.info("发送失败， 错误代码=" + msgResult.getErrcode() + ", 错误消息=" + msgResult.getErrmsg());
                        if(bool){
                            resultMap.put(userName, "消息发送失败，失败原因：" + msgResult.getErrmsg());
                        }else{
                            resultMap.put(phoneImei, "消息发送失败，失败原因：" + msgResult.getErrmsg());
                        }
                    }
                }else{
                    LOG.info("无法获取服务器返回数据");
                    if(bool){
                        resultMap.put(userName, "消息发送失败，失败原因：无法获取推送服务器返回的数据！");
                    }else{
                        resultMap.put(phoneImei, "消息发送失败，失败原因：无法获取推送服务器返回的数据！");
                    }
                }
            }
        }
        return null;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return IUserService userService.
     */
    public IUserService getUserService() {
        return userService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userService The userService to set.
     */
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String userName.
     */
    public String getUserName() {
        return userName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userName The userName to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String userPass.
     */
    public String getUserPass() {
        return userPass;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userPass The userPass to set.
     */
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String phoneImei.
     */
    public String getPhoneImei() {
        return phoneImei;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param phoneImei The phoneImei to set.
     */
    public void setPhoneImei(String phoneImei) {
        this.phoneImei = phoneImei;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String content.
     */
    public String getContent() {
        return content;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param content The content to set.
     */
    public void setContent(String content) {
        if(content != null){
            this.content = content.replaceAll(";", "%3B");
        }else{
            this.content = content;
        }
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
}
