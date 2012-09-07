package com.webservice.jbpm.service.impl;

import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.task.Task;
import org.jbpm.task.User;

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
    
    public void init(){
        if(jbpmClient == null){
            jbpmClient = JbpmSyncService.getInstance();
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
    public synchronized Long getTaskId(String ... processName) throws NamingException{
        //保证processId至少存在一个, 否则使用默认流程图
//        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
//            jbpmClient.setProcess(processName);
//        }
//        jbpmClient.init();
        return jbpmClient.getTaskId();
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
    public Task getFirstTask(Map<String, Object> param, String processId, String... processName) throws Exception{
        if(processId == null || "".equals(processId.trim())){
            throw new Exception("流程图ID不能为空！");
        }
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            jbpmClient.setProcess(processName);
        }else{
            throw new Exception("流程图不能为空！");
        }
        jbpmClient.init();
        InitialContext ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            if(param != null && !param.isEmpty()){
                jbpmClient.startProcess(processId, param);
            }else{
                jbpmClient.startProcess(processId, null);
            }
            Long taskId = jbpmClient.getTaskId();
            Task task = jbpmClient.getTaskById(taskId);
//            List<TaskSummary> tsl = jbpmClient.getAssignedTasks(new User(param.get("userName").toString()));
//            TaskSummary ts = null;
//            if(tsl != null){
//                ts = tsl.get(0);
//            }
//            Task task = null;
//            if(ts != null){
//                Long taskId = ts.getId();
//                task = jbpmClient.getTaskById(taskId);
//            }
            transactionManager.commit();
            return task;
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage(), e);
            if(transactionManager != null){
                transactionManager.rollback();
            }
        }
        return null;
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
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            jbpmClient.setProcess(processName);
        }
        jbpmClient.init();
        InitialContext ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            jbpmClient.assignTaskToUser(NumberUtils.toLong(taskId), userName, targetUserName);
            transactionManager.commit();
        }catch(Exception e){
            transactionManager.rollback();
            log.error(e.getMessage(), e);
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
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            jbpmClient.setProcess(processName);
        }
        jbpmClient.init();
        InitialContext ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            Task task = jbpmClient.getTaskById(NumberUtils.toLong(taskId));
            if(task != null){
                jbpmClient.startTask(new User(userName), roleList, task.getId());
            }else{
                throw new Exception("当前系统中不存在ID为：" + taskId + " 的工作流任务，请联系系统管理员.");
            }
            transactionManager.commit();
        }catch(Exception e){
            if(transactionManager != null){
                try {
                    transactionManager.rollback();
                } catch (IllegalStateException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                } catch (SecurityException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                } catch (SystemException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                }
            }
            log.error(e.getMessage(), e);
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
        //保证processId至少存在一个, 否则使用默认流程图
        if(processName != null && processName.length > 0 && !"".equals(processName[0].trim())){
            jbpmClient.setProcess(processName);
        }
        jbpmClient.init();
        InitialContext ctx = null;
        UserTransaction transactionManager = null;
        try{
            ctx = new InitialContext();
            transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            transactionManager.begin();
            Task task = jbpmClient.getTaskById(NumberUtils.toLong(taskId));
            if(task != null){
                jbpmClient.completeTask(new User(userName), task.getId(), resultMap, null);
            }else{
                throw new Exception("当前系统中不存在ID为：" + taskId + " 的工作流任务，请联系系统管理员.");
            }
            //log.info("aaaaaaaaaaaaaaaaaa"+jbpmClient.getTaskId());
            transactionManager.commit();
        }catch(Exception e){
            if(transactionManager != null){
                try {
                    transactionManager.rollback();
                } catch (IllegalStateException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                } catch (SecurityException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                } catch (SystemException e1) {
                    log.error(e1.getMessage(), e1);
                    throw e1;
                }
            }
            log.error(e.getMessage(), e);
            throw e;
        }finally{
            log.info("aaaaaaaaaaaaaaaaaa"+jbpmClient.getTaskId());
        }
    }
}
