package com.huateng.jbpm.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.nutz.json.Json;

import com.huateng.common.web.action.BaseAction;
import com.huateng.db.model.LoginForm;
import com.huateng.jbpm.humantask.HumanTaskClient;
/**
 * <p>Description: [工作流测试类(WEB)]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$
 */
public class TestAction extends BaseAction {
    private LoginForm loginForm;
    private String userId;
    
    private static HumanTaskClient client = new HumanTaskClient();
    
    static{
        //加载流程定义
        //client.start();
        //启动流程
        //client.startProcess();
    }
    
    /**
     * <p>Discription:[用户登录]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String login(){
        if(loginForm != null && loginForm.getUserId() != null){
            ServletActionContext.getRequest().getSession().setAttribute("userInfo", loginForm);
        }
        return SUCCESS;
    }
    /**
     * <p>Discription:[登录用户获取任务]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String getTasks(){
        if(this.userId == null){
            return null;
        }
        //加载流程定义
        client.start(true);
        //定义参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", this.userId);
        params.put("userName", "测试用户");
        //启动流程
        client.startProcess(params);
        List<String> groups = new ArrayList<String>();
        groups.add("cardCenter");
        List<TaskSummary> tasks = client.getAssignedTasks(new User(userId), groups);
        LOG.info(Json.toJson(tasks));
        return null;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return LoginForm loginForm.
     */
    public LoginForm getLoginForm() {
        return loginForm;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param loginForm The loginForm to set.
     */
    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String userId.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
