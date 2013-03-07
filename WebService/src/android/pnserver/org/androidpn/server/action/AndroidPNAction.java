package org.androidpn.server.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidpn.server.console.vo.SessionVO;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.presence.PresenceManager;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.xmpp.packet.Presence;

import com.webservice.common.action.BaseAction;
import com.webservice.system.common.constants.Constants;

/** 
 * <p>Description: [Android推送主控]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class AndroidPNAction extends BaseAction {
    private UserService pnUserService;
    private DataSourceTransactionManager transactionManager;
    private NotificationManager notificationManager = new NotificationManager();
    /**
     * <p>Discription:[显示Android主页]</p>
     * @return
     * @author:大牙
     * @update:2013-2-26
     */
    public String begin(){
        return SUCCESS;
    }
    /**
     * <p>Discription:[显示用户列表]</p>
     * @return
     * @author:大牙
     * @update:2013-2-26
     */
    public String userList(){
        PresenceManager presenceManager = new PresenceManager();
        List<User> userList = pnUserService.getUsers();
        for (User user : userList) {
            if (presenceManager.isAvailable(user)) {
                // Presence presence = presenceManager.getPresence(user);
                user.setOnline(Constants.ANDROID_ONLINE);
            } else {
                user.setOnline(Constants.ANDROID_OFFLINE);
            }
            // logger.debug("user.online=" + user.isOnline());
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("userList", userList);
        resultMap.put("totalCount", userList.size());
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[显示Session列表]</p>
     * @return
     * @author:大牙
     * @update:2013-2-26
     */
    public String sessionList(){
        ClientSession[] sessions = new ClientSession[0];
        sessions = SessionManager.getInstance().getSessions().toArray(sessions);
        List<SessionVO> voList = new ArrayList<SessionVO>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            for (ClientSession sess : sessions) {
                SessionVO vo = new SessionVO();
                vo.setUsername(sess.getUsername());
                vo.setResource(sess.getAddress().getResource());
                // Status
                if (sess.getStatus() == Session.STATUS_CONNECTED) {
                    vo.setStatus("CONNECTED");
                } else if (sess.getStatus() == Session.STATUS_AUTHENTICATED) {
                    vo.setStatus("AUTHENTICATED");
                } else if (sess.getStatus() == Session.STATUS_CLOSED) {
                    vo.setStatus("CLOSED");
                } else {
                    vo.setStatus("UNKNOWN");
                }
                // Presence
                if (!sess.getPresence().isAvailable()) {
                    vo.setPresence("Offline");
                } else {
                    Presence.Show show = sess.getPresence().getShow();
                    if (show == null) {
                        vo.setPresence("Online");
                    } else if (show == Presence.Show.away) {
                        vo.setPresence("Away");
                    } else if (show == Presence.Show.chat) {
                        vo.setPresence("Chat");
                    } else if (show == Presence.Show.dnd) {
                        vo.setPresence("Do Not Disturb");
                    } else if (show == Presence.Show.xa) {
                        vo.setPresence("eXtended Away");
                    } else {
                        vo.setPresence("Unknown");
                    }
                }
                vo.setClientIP(sess.getHostAddress());
                vo.setCreatedDate(sess.getCreationDate());
                voList.add(vo);
            }
            resultMap.put("success", true);
            resultMap.put("sessionList", voList);
            resultMap.put("totalCount", voList.size());
        }catch(Exception e){
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误！错误原因：" + e.getMessage());
        }
        writeMeessage(resultMap);
        return null;
    }
    /**
     * <p>Discription:[显示推送表单]</p>
     * @return
     * @author:大牙
     * @update:2013-2-26
     */
    public String notificationForm(){
        return SUCCESS;
    }
    /**
     * <p>Discription:[推送]</p>
     * @return
     * @author:大牙
     * @update:2013-2-26
     */
    public String notificationSend(){
        String broadcast = getRequest().getParameter("broadcast") == null ? "Y" :getRequest().getParameter("broadcast");
        String username = getRequest().getParameter("username");
        String title = getRequest().getParameter("title");
        String message = getRequest().getParameter("message");
        String uri = getRequest().getParameter("uri");
        String apiKey = Config.getString("apiKey", "");
        LOG.debug("apiKey=" + apiKey);
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try{
            //在线用户
            if (broadcast.equalsIgnoreCase("Y")) {
                notificationManager.sendBroadcast(apiKey, title, message, uri);
            //所有用户
            }else if (broadcast.equalsIgnoreCase("A")) {
                notificationManager.sendAllBroadcast(apiKey, title, message, uri);
            //指定用户
            }else {
                notificationManager.sendNotifications(apiKey, username, title,
                        message, uri);
            }
            
            resultMap.put("success", true);
            resultMap.put("msg", "Android消息推送成功！");
        }catch(Exception e){
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误！错误原因：" + e.getMessage());
        }
        writeMeessage(resultMap);
        return null;
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return UserService pnUserService.
     */
    public UserService getPnUserService() {
        return pnUserService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param pnUserService The pnUserService to set.
     */
    public void setPnUserService(UserService pnUserService) {
        this.pnUserService = pnUserService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return DataSourceTransactionManager transactionManager.
     */
    public DataSourceTransactionManager getTransactionManager() {
        return transactionManager;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param transactionManager The transactionManager to set.
     */
    public void setTransactionManager(
            DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
