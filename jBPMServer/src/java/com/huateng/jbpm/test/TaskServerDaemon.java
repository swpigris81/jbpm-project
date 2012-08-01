package com.huateng.jbpm.test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

import com.huateng.jbpm.test.domain.UserInfoDomain;

/**
 * 服务器监听类
 * @author liubo
 *
 */
public class TaskServerDaemon {
    private Log log = LogFactory.getLog(TaskServerDaemon.class);
    
    private boolean running;
    private TaskServer taskServer;
    private Thread thread = null;
    
    public TaskServerDaemon() {
        this.running = false;
    }
    
    /**
     * 加载数据库
     * 启动服务监听
     */
    public void startServer() {
        if(isRunning())
            throw new IllegalStateException("Server is already started");
        this.running = true;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.task");//org.drools.task
        TaskService taskService = new TaskService(entityManagerFactory, SystemEventListenerFactory.getSystemEventListener());
        TaskServiceSession taskSession = taskService.createSession() ;
        UserInfoDomain userInfo = new UserInfoDomain();
        taskService.setUserinfo( userInfo);
        //初始化用户
        for (String userName : getDefaultUsers()) {
            taskSession.addUser(new User(userName));
        }
        //初始化用户组
        for(String group : getDefaultGroups()){
            taskSession.addGroup(new Group(group));
        }
        taskServer = new MinaTaskServer(taskService);
        thread = new Thread(taskServer);
        //Thread thread = new Thread(taskServer);
        thread.start();
    }

    public void stopServer() throws Exception {
        if(!isRunning()){
            return;
        }
        try{
            taskServer.stop();
        }catch(Exception e){
            log.error("Exception while stopping task server " + e.getMessage());
            throw e;
        }
        try{
            thread.interrupt();
        }catch(Exception e){
            log.error("Exception while stopping task server thread " + e.getMessage());
            throw e;
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    private String[] getDefaultUsers() {
        return new String[]{"salaboy", "translator", "reviewer", "Administrator"};
    }
    
    private String[] getDefaultGroups() {
        return new String[]{"cardCenter", "merCenter", "merReview", "cardCenter2", "cardReview"};
    }
}
