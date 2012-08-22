package com.jbpm.test.client.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.drools.definition.process.Node;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.nutz.json.Json;

import com.jbpm.test.client.common.web.action.BaseAction;
import com.jbpm.test.client.db.model.LoginForm;
import com.jbpm.test.client.jbpm.humantask.JbpmService;
/**
 * <p>Description: [工作流测试类(WEB)]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$
 */
public class TestAction extends BaseAction {
    private LoginForm loginForm;
    private String userId;
    private String taskId;
    private String sessionId;
    
    private static JbpmService client = new JbpmService();
    
    static{
        //加载流程定义
        try {
            client.init();
        } catch (NamingException e) {
            e.printStackTrace();
        }
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
        if(this.taskId == null){
            return null;
        }
        List<String> groups = new ArrayList<String>();
        groups.add("cardCenter");
        LoginForm userInfo = (LoginForm) ServletActionContext.getRequest().getSession().getAttribute("userInfo");
        if(userId == null){
             userId = userInfo.getUserId();
        }
        List<TaskSummary> tasks = client.getAssignedTasks(new User(userId), groups);
        LOG.info(Json.toJson(tasks));
        
        TaskSummary task1 = null;
        Task task = client.getTaskById(NumberUtils.toLong(taskId));
        LOG.info("asdsad", task.getTaskData().getStatus().toString());
        Context ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            
            task1 = client.getTaskService(client.getKSession(-1)).getTasksAssignedAsPotentialOwner(userId, groups, "en-UK").get(0);
            
            LOG.info(Json.toJson(task1));
            
            System.out.println(client.getVariableValue("date", task.getTaskData().getProcessInstanceId(), client.getKSession(-1)));
            Node [] nodes = client.getNodeTriggered(task.getTaskData().getProcessInstanceId(), client.getKSession(-1));
            if(nodes != null && nodes.length>0){
                for(int i=0; i< nodes.length; i++){
                    System.out.println(nodes[i]);
                }
            }
            transactionManager.commit();
        }catch(Exception e){
            if(transactionManager != null){
                try {
                    transactionManager.rollback();
                } catch (IllegalStateException e1) {
                    e1.printStackTrace();
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (SystemException e1) {
                    e1.printStackTrace();
                }
            }
        } 
        return null;
    }
    /**
     * <p>Discription:[完成此任务，进入下一任务]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String nextTask(){
        if(this.taskId == null){
            return null;
        }
        List<String> groups = new ArrayList<String>();
        groups.add("cardCenter");
        LoginForm userInfo = (LoginForm) ServletActionContext.getRequest().getSession().getAttribute("userInfo");
        if(userId == null){
             userId = userInfo.getUserId();
        }
        
        Context ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            TaskSummary taskSummary = client.getTaskService(client.getKSession(-1)).getTasksAssignedAsPotentialOwner(userId, groups, "en-UK").get(0);
            LOG.info("用户：" + userId + "查询得到的自己的任务ID：" + taskSummary.getId());
            Task task = client.getTaskById(taskSummary.getId());
            LOG.info("此时任务状态为：" + task.getTaskData().getStatus());
            LOG.info("用户：" + userId + "开始执行自己的任务。。。");
            client.startTask(new User(userId), groups, taskSummary);
            task = client.getTaskById(taskSummary.getId());
            LOG.info("此时任务状态为：" + task.getTaskData().getStatus());
            LOG.info("用户：" + userId + "已经开始执行自己的任务。。。");
            LOG.info("用户：" + userId + "开始完成自己的任务。。。");
            client.completeTask(new User(userId), taskSummary);
            task = client.getTaskById(taskSummary.getId());
            LOG.info("此时任务状态为：" + task.getTaskData().getStatus());
            LOG.info("用户：" + userId + "已经完成自己的任务。。。");
            
            transactionManager.commit();
        }catch(Exception e){
            if(transactionManager != null){
                try {
                    transactionManager.rollback();
                } catch (IllegalStateException e1) {
                    e1.printStackTrace();
                } catch (SecurityException e1) {
                    e1.printStackTrace();
                } catch (SystemException e1) {
                    e1.printStackTrace();
                }
            }
        }
        
        
        
        return null;
    }
    
    
    /**
     * <p>Discription:[新建任务]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String newTask(){
        if(this.userId == null){
            return null;
        }
        //新任务的时候才需要startProcess
        //定义参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "李雷");
        params.put("userName", "测试用户");
        //启动流程
        client.startProcess(params);
        LOG.info("流程已启动，获取得到的taskID是：" + client.getTaskId());
        List<String> groups = new ArrayList<String>();
        groups.add("cardCenter");
        List<TaskSummary> tasks = client.getAssignedTasks(new User(userId), groups);
        LOG.info(Json.toJson(tasks));
        LOG.info("获取的当前任务：" + Json.toJson(tasks.get(0)));
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
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String taskId.
     */
    public String getTaskId() {
        return taskId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param taskId The taskId to set.
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
