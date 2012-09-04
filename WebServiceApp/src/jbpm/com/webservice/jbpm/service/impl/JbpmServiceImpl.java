package com.webservice.jbpm.service.impl;

import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;

import com.webservice.jbpm.client.service.JbpmService;
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
    private JbpmService jbpmClient = new JbpmService();
    /**
     * <p>Discription:[启动流程，获取该流程的第一个任务]</p>
     * @param param 启动流程同时传入的参数
     * @param processId 流程图为一ID
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
    
    public void assignTaskToUser(){
        
    }
    
    /**
     * <p>Discription:[开始任务]</p>
     * @param userName 用户
     * @param roleList 用户角色
     * @param taskId 任务ID
     * @param processId 任务所在流程图名字地址
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(String userName, List<String> roleList, String taskId, String... processId) throws Exception{
        if(taskId == null || "".equals(taskId.trim())){
            return;
        }
        //保证processId至少存在一个, 否则使用默认流程图
        if(processId != null && processId.length > 0 && !"".equals(processId[0].trim())){
            jbpmClient.setProcess(processId);
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
                TaskSummary taskSummary = new TaskSummary();
                taskSummary.setId(task.getId());
                jbpmClient.startTask(new User(userName), roleList, taskSummary);
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
    public void completeTask(String userName, String taskId, Map resultMap, String... processId) throws Exception{
        if(taskId == null || "".equals(taskId.trim())){
            return;
        }
        //保证processId至少存在一个, 否则使用默认流程图
        if(processId != null && processId.length > 0 && !"".equals(processId[0].trim())){
            jbpmClient.setProcess(processId);
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
                TaskSummary taskSummary = new TaskSummary();
                taskSummary.setId(task.getId());
                jbpmClient.completeTask(new User(userName), taskSummary, resultMap, null);
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
}
