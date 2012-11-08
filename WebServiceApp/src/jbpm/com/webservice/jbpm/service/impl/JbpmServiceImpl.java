package com.webservice.jbpm.service.impl;

import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;

import com.webservice.jbpm.client.service.JbpmSyncService;
import com.webservice.jbpm.service.IJbpmService;
/**
 * <p>Description: [工作流服务类]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class JbpmServiceImpl implements IJbpmService {
    private Log log = LogFactory.getLog(JbpmServiceImpl.class);
    /**
     * 工作流客户端
     */
    private static JbpmSyncService jbpmClient;
    private String[] processName;
    private Context ctx;
    private UserTransaction transactionManager;
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String[] processName.
     */
    public String[] getProcessName() {
        return processName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processName The processName to set.
     */
    public void setProcessName(String[] processName) {
        this.processName = processName;
    }

    public void init() throws Exception{
        log.info(processName);
        if(ctx == null){
            ctx = new InitialContext();
        }
        //if(transactionManager == null){
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        //}
        if(jbpmClient == null){
            jbpmClient = JbpmSyncService.getInstance();
        }
        try{
            transactionManager.begin();
            jbpmClient.setProcess(processName);
            jbpmClient.init();
            transactionManager.commit();
        }catch(Exception e){
            transactionManager.rollback();
            throw e;
        }
    }
    
    public void destory() throws Exception{
        log.info("销毁JBPMservice实例");
        if(jbpmClient != null){
            try {
                jbpmClient.stop();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    /**
     * <p>Discription:[与JBPM服务端断开连接]</p>
     * @author 大牙-小白
     * @throws Exception 
     * @update 2012-9-5 大牙-小白 [变更描述]
     */
    public void disconnectJbpmServer() throws Exception{
        try {
            log.info("与JBPM服务断开连接");
            jbpmClient.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    /**
     * <p>Discription:[连接流程JBPM服务]</p>
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public void connectJbpmServer() throws Exception{
        try {
            jbpmClient.connect();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * <p>Discription:[获取当前任务ID]</p>
     * @return
     * @author 大牙-小白
     * @throws NamingException 
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public synchronized Long getTaskId(String ... processName) throws Exception{
        //保证processId至少存在一个, 否则使用默认流程图
//        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
//            jbpmClient.setProcess(processName);
//        }
//        jbpmClient.init();
//        JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        Long taskId = -1L;
        try{
            transactionManager.begin();
            taskId = jbpmClient.getTaskId();
            transactionManager.commit();
        }catch(Exception e){
            transactionManager.rollback();
            throw e;
        }
        return taskId;
    }
    
    /**
     * <p>Discription:[启动流程，获取该流程的第一个任务]</p>
     * @param param 启动流程同时传入的参数
     * @param processId 流程图唯一ID
     * @param processName 流程图名称地址
     * @return
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-3 大牙-小白 [变更描述]
     */
    public Task getFirstTask(String userName, Map<String, Object> param, String processId, String... processName) throws Exception{
        if(processId == null || "".equals(processId.trim())){
            throw new Exception("流程图ID不能为空！");
        }
        //JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }else{
            throw new Exception("流程图不能为空！");
        }
        //jbpmClient.init();
        Task task = null;
        try{
            transactionManager.begin();
            if(param != null && !param.isEmpty()){
                jbpmClient.startProcess(processId, param);
            }else{
                jbpmClient.startProcess(processId, null);
            }
            List<TaskSummary> ts = getAssignedTaskByUserOrGroup(userName, null, processName);
            if(ts != null && !ts.isEmpty()){
                Long taskId = ts.get(0).getId();
                task = jbpmClient.getTaskById(taskId);
            }
            transactionManager.commit();
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
        return task;
    }
    /**
     * <p>Discription:[分配任务给指定用户]</p>
     * @param taskId 任务ID
     * @param userName 分配前任务所属用户
     * @param targetUserName 分配之后任务所属用户
     * @param processName 流程图
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-4 大牙-小白 [变更描述]
     */
    public void assignTaskToUser(String taskId, String userName, String targetUserName, String... processName) throws Exception{
        if(taskId == null || "".equals(taskId.trim())){
            return;
        }
        //JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }
        //jbpmClient.init();
        try{
            transactionManager.begin();
            jbpmClient.assignTaskToUser(NumberUtils.toLong(taskId), userName, targetUserName);
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
    }
    
    /**
     * <p>Discription:[开始任务]</p>
     * @param userName 用户
     * @param roleList 用户角色
     * @param taskId 任务ID
     * @param processName 任务所在流程图名字地址
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(String userName, List<String> roleList, String taskId, String... processName) throws Exception{
        if(taskId == null || "".equals(taskId.trim())){
            return;
        }
        //JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }
        //jbpmClient.init();
        try{
            transactionManager.begin();
            Task task = jbpmClient.getTaskById(NumberUtils.toLong(taskId));
            if(task != null){
                jbpmClient.startTask(new User(userName), roleList, task.getId());
            }else{
                throw new Exception("当前系统中不存在ID为：" + taskId + " 的工作流任务，请联系系统管理员.");
            }
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
    }
    
    /**
     * <p>Discription:[完成工作任务]</p>
     * @param userName 用户名
     * @param taskId 任务ID
     * @param resultMap 对该任务完成的结果，如批准通过/不通过等
     * @param processId 流程图名字地址
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(String userName, String taskId, Map resultMap, String... processName) throws Exception{
        if(taskId == null || "".equals(taskId.trim())){
            return;
        }
        //JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }
        //jbpmClient.init();
        try{
            transactionManager.begin();
            Task task = jbpmClient.getTaskById(NumberUtils.toLong(taskId));
            if(task != null){
                jbpmClient.completeTask(new User(userName), task.getId(), resultMap, null);
            }else{
                throw new Exception("当前系统中不存在ID为：" + taskId + " 的工作流任务，请联系系统管理员.");
            }
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }finally{
            //log.info("aaaaaaaaaaaaaaaaaa"+jbpmClient.getTaskId());
        }
    }
    
    public List<TaskSummary> getAssignedTaskByUserOrGroup(String user, List<String> group, String... processName) throws Exception{
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }
        //jbpmClient.init();
        try{
            transactionManager.begin();
            List<TaskSummary> list = null;
            if(group != null && !group.isEmpty()){
                list = jbpmClient.getAssignedTasks(new User(user), group);
            }else{
                list = jbpmClient.getAssignedTasks(new User(user));
            }
            transactionManager.commit();
            return list;
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
    }
    
    public Object getInstanceVariable(String name, int processSessionId, long processInstanceId, String... processName) throws Exception{
        //JbpmSyncService jbpmClient = JbpmSyncService.getInstance();
        //保证processId至少存在一个, 否则使用默认流程图
//        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
//            jbpmClient.setProcess(processName);
//        }
//        jbpmClient.init();
        Object variable = null;
        try{
            transactionManager.begin();
            variable = jbpmClient.getVariableValue(name, processInstanceId);//jbpmClient.getSessionInfo(processSessionId)
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
        return variable;
    }
    
    /**
     * <p>Discription:[设置流程变量]</p>
     * @param name 变量名称
     * @param value 值
     * @param processSessionId 会话ID
     * @param processInstanceId 流程ID
     * @param processName 流程图
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public void setInstanceVariable(String name, Object value, int processSessionId, long processInstanceId, String... processName) throws Exception{
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            //jbpmClient.setProcess(processName);
        }
        //jbpmClient.init();
        try{
            transactionManager.begin();
            jbpmClient.setVariableValue(name, value, processInstanceId);
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
    }
    /**
     * <p>Discription:[设置流程变量，该流程是刚刚启动的新流程]</p>
     * @param name 变量名称
     * @param value 值
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-14 大牙-小白 [变更描述]
     */
    public void setInstanceVariableForNewTask(String name, Object value) throws Exception{
        try{
            transactionManager.begin();
            jbpmClient.setVariableValue(name, value);
            transactionManager.commit();
        }catch(Exception e){
            log.error(e.getMessage(), e);
            transactionManager.rollback();
            throw e;
        }
    }
}
