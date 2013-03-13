package com.webservice.gcm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.webservice.common.action.BaseAction;
import com.webservice.gcm.bean.GcmModel;
import com.webservice.gcm.service.GcmService;
import com.webservice.system.common.constants.Constants;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.bean.UserRole;
import com.webservice.system.role.service.IRoleService;
import com.webservice.system.role.service.IUserRoleService;
import com.webservice.system.user.bean.UserInfo;
import com.webservice.system.user.service.IUserService;
import com.webservice.system.util.CipherUtil;
import com.webservice.system.util.ConfigProperties;

/** 
 * <p>Description: [GoogleGCM服务器端]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class GoogleGcmAction extends BaseAction {
    private String regisId;
    private String regisIdArray;
    private String userName;
    private String userPass;
    private String messageContent;
    private String messageTitle;
    private String phoneImei;
    private String androidAlias;
    private String androidAliasArray;
    /**
     * 用户服务
     */
    private IUserService userService;
    /**
     * 角色服务
     */
    private IRoleService roleService;
    /**
     * 用户角色服务
     */
    private IUserRoleService userRoleService;
    /**
     * 系统默认角色名
     */
    private String systemRoleName = ConfigProperties.getProperties("defaultRoleName");
    /**
     * GCM服务
     */
    private GcmService gcmService;
    
    /**
     * <p>Discription:[Android程序用来发送从GCM收到的注册ID, 服务器就可以对注册ID和用户设备进行关联]</p>
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public String registration(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(userName == null || "".equals(userName.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "用户名不能为空！");
        }else if(regisId == null || "".equals(regisId.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "注册设备唯一ID不能为空！");
        }else{
            List list = gcmService.findByRegisterId(regisId);
            if(list != null && !list.isEmpty()){
                resultMap.put("success", false);
                resultMap.put("msg", "该设备唯一ID已经被注册过！");
            }else{
                List userList = userService.getUserByName(userName);
                if(userList != null && !userList.isEmpty()){
                    //用户已注册，判断其密码
                    String checkPass= "from UserInfo where userName = ? and password = ?";
                    userList = userService.findUserByPageCondition(checkPass, 0, 999999, new Object[]{userName, CipherUtil.generatePassword(userPass, userName)});
                    if(userList != null && !userList.isEmpty()){
                        //密码验证通过，允许每个用户注册多个设备
                        GcmModel model = new GcmModel();
                        model.setRegisId(regisId);
                        model.setUserName(userName);
                        model.setAndroidAlias(androidAlias);
                        gcmService.saveGcmRegis(model);
                        resultMap.put("success", true);
                        resultMap.put("msg", "用户设备注册成功！");
                    }else{
                        resultMap.put("success", false);
                        resultMap.put("msg", "用户密码错误！");
                    }
                }else{
                    //尚未注册用户，先注册用户
                    UserInfo user = new UserInfo();
                    user.setUserName(userName);
                    user.setPassword(CipherUtil.generatePassword(userPass, userName));
                    user.setPhoneImei(phoneImei);
                    this.userService.saveOrUpdate(user);
                    //为手机注册用户赋予角色权限
                    if(this.systemRoleName != null && !"".equals(this.systemRoleName)){
                        List roles = this.roleService.findRoleByName(systemRoleName);
                        if(roles!=null && roles.size()>0){
                            RoleInfo role = (RoleInfo) roles.get(0);
                            String roleId = role.getRoleId();
                            
                            UserRole userRole = new UserRole();
                            userRole.setUserId(user.getUserName());
                            userRole.setRoleId(roleId);
                            this.userRoleService.saveOrUpdate(userRole);
                        }
                    }
                    GcmModel model = new GcmModel();
                    model.setRegisId(regisId);
                    model.setUserName(userName);
                    model.setAndroidAlias(androidAlias);
                    gcmService.saveGcmRegis(model);
                    resultMap.put("success", true);
                    resultMap.put("msg", "用户设备注册成功！");
                }
            }
        }
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[服务器注销ID]</p>
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public String unRegistration(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(userName == null || "".equals(userName.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "用户名不能为空！");
        }else if(regisId == null || "".equals(regisId.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "注册设备唯一ID不能为空！");
        }else{
            List list = gcmService.findByRegisterId(regisId);
            if(list != null && !list.isEmpty()){
                gcmService.unRegistion(list);
                resultMap.put("success", true);
                resultMap.put("msg", "用户设备注销成功！");
            }else{
                resultMap.put("success", false);
                resultMap.put("msg", "该设备尚未注册！");
            }
        }
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[服务器消息推送]</p>
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public String gcmSender(){
        Sender sender = new Sender(Constants.GCM_API_KEY);
        Builder messageBuilder = new Message.Builder();
        messageBuilder.addData("title", messageTitle);
        messageBuilder.addData("message", messageContent);
        Message message = messageBuilder.build();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if(regisIdArray == null || "".equals(regisIdArray.trim())){
                resultMap.put("success", false);
                resultMap.put("msg", "请选择要推送消息的设备");
            }else{
                String [] regisIds = regisIdArray.split(",");
                String [] androidAlias = androidAliasArray.split(",");
                //多人推送
                List<String> devices = CollectionUtils.arrayToList(regisIds);
                //如果GCM服务器无效的话，有5次重发机会
                MulticastResult multicastResult = sender.send(message, devices, 5);
                List<Result> results = multicastResult.getResults();
                resultMap.put("success", true);
                resultMap.put("msg", "服务器推送结果：");
                List resultList = new ArrayList();
                for(int i=0, j=devices.size(); i<j; i++){
                    String regId = devices.get(i);
                    Result result = results.get(i);
                    String messageId = result.getMessageId();
                    if (messageId != null) {
                        LOG.info("Succesfully sent message to device: " + regId +
                                "; messageId = " + messageId + "; alias : " + androidAlias[i]);
                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            LOG.info("canonicalRegId " + canonicalRegId);
                        }
                        resultList.add(new String[]{androidAlias[i], "消息推送成功，返回的消息编号：" + messageId});
                    }else{
                        String error = result.getErrorCodeName();
                        if (error.equals(com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED)) {
                            //application has been removed from device - unregister it
                            LOG.info("Unregistered device: " + regId);
                            resultList.add(new String[]{androidAlias[i], "消息推送失败，失败原因：用户设备已将该应用删除！"});
                        }else{
                            LOG.info("Error sending message to " + regId + ": " + error);
                            resultList.add(new String[]{androidAlias[i], "消息推送失败，失败原因：" + error});
                        }
                    }
                }
                resultMap.put("result", resultList);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("msg", "服务器推送异常！" + e.getMessage());
        }
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[注册用户列表]</p>
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public String userList(){
        List userList = null;
        int size = 0;
        if(userName != null && !"".equals(userName.trim())){
            userList = gcmService.findMyDroidList(userName, start, limit);
            size = gcmService.findMyDroidList(userName, start, limit).size();
        }else{
            userList = gcmService.findRegisList(start, limit);
            size = gcmService.findRegisList(-1, -1).size();
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("userList", userList);
        resultMap.put("totalCount", size);
        writeMeessage(resultMap);
        return null;
    }
    
    public String getRegisId() {
        return regisId;
    }
    public void setRegisId(String regisId) {
        this.regisId = regisId;
    }
    public String getRegisIdArray() {
        return regisIdArray;
    }
    public void setRegisIdArray(String regisIdArray) {
        this.regisIdArray = regisIdArray;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPass() {
        return userPass;
    }
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    public String getMessageContent() {
        return messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public String getMessageTitle() {
        return messageTitle;
    }
    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
    public IUserService getUserService() {
        return userService;
    }
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    public GcmService getGcmService() {
        return gcmService;
    }
    public void setGcmService(GcmService gcmService) {
        this.gcmService = gcmService;
    }
    public String getAndroidAlias() {
        return androidAlias;
    }
    public void setAndroidAlias(String androidAlias) {
        this.androidAlias = androidAlias;
    }
    public IRoleService getRoleService() {
        return roleService;
    }
    public void setRoleService(IRoleService roleService) {
        this.roleService = roleService;
    }
    public IUserRoleService getUserRoleService() {
        return userRoleService;
    }
    public void setUserRoleService(IUserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }
    public String getSystemRoleName() {
        return systemRoleName;
    }
    public void setSystemRoleName(String systemRoleName) {
        this.systemRoleName = systemRoleName;
    }
    public String getPhoneImei() {
        return phoneImei;
    }
    public void setPhoneImei(String phoneImei) {
        this.phoneImei = phoneImei;
    }
    public String getAndroidAliasArray() {
        return androidAliasArray;
    }
    public void setAndroidAliasArray(String androidAliasArray) {
        this.androidAliasArray = androidAliasArray;
    }
    
}
