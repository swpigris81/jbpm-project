package com.webservice.jbpm.service;

import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;

public interface IJbpmService {
    /**
     * <p>Discription:[与JBPM服务端断开连接]</p>
     * @author 大牙-小白
     * @throws Exception 
     * @update 2012-9-5 大牙-小白 [变更描述]
     */
    public void disconnectJbpmServer() throws Exception;
    
    /**
     * <p>Discription:[连接流程JBPM服务]</p>
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public void connectJbpmServer() throws Exception;
    /**
     * <p>Discription:[获取当前任务ID]</p>
     * @return
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public Long getTaskId(String ... processName) throws NamingException;
    
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
    public Task getFirstTask(Map<String, Object> param, String processId, String... processName) throws Exception;
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
    public void assignTaskToUser(String taskId, String userName, String targetUserName, String... processName) throws Exception;
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
    public void startTask(String userName, List<String> roleList, String taskId, String... processId) throws Exception;
    
    /**
     * <p>Discription:[完成工作任务]</p>
     * @param userName 用户名
     * @param taskId 任务ID
     * @param resultMap 对该任务完成的结果，如批准通过/不通过等
     * @param processId 流程图名字地址
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(String userName, String taskId, Map resultMap, String... processId) throws Exception;
    /**
     * <p>Discription:[获取待办任务]</p>
     * @param user 用户
     * @param group 用户组
     * @param processName 流程图名字
     * @return 待办任务
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public List<TaskSummary> getAssignedTaskByUserOrGroup(String user, List<String> group, String... processName) throws Exception;
    /**
     * <p>Discription:[获取流程中的变量]</p>
     * @param name 变量名称
     * @param processSessionId 流程回话ID
     * @param processInstanceId 流程ID
     * @param processName 流程图
     * @return 变量值
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public Object getInstanceVariable(String name, int processSessionId, long processInstanceId, String... processName) throws Exception;
}
