package com.huateng.jbpm.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

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
    private PoolingDataSource ds;
    private EntityManagerFactory emf;
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
        try{
            startDb();
        }catch(Exception e){
            log.error("JNDI数据库配置失败", e);
            throw new RuntimeException(e);
        }
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
    /**
     * <p>Discription:[开启JNDI数据服务]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startDb() throws NamingException {
        Context ctx = new InitialContext();
        ds = new PoolingDataSource();
//        DataSource ds1 = (DataSource) ctx.lookup("java:comp/env/jbpm-ds");
        
        ds.setUniqueName("jbpm-ds");
        //ds.setClassName("com.mysql.jdbc.Driver");
        //ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        ds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "jbpm5");
        ds.getDriverProperties().put("password", "jbpm5");
        ds.getDriverProperties().put("url", "jdbc:mysql://localhost:3306/jbpm5?useUnicode=true&amp;characterEncoding=UTF-8");
        ds.getDriverProperties().put("driverClassName","com.mysql.jdbc.Driver");
        ds.init();
        ctx.bind("jbpm-ds", ds);
        emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER,TransactionManagerServices.getTransactionManager());
    }
    
    public void stopServer() throws Exception {
        if(!isRunning()){
            return;
        }
        try{
            ds.close();
        }catch(Exception e){
            log.error("Exception while stopping dataSource " + e.getMessage());
            throw e;
        }
        try{
            emf.close();
        }catch(Exception e){
            log.error("Exception while stopping entity manager factory " + e.getMessage());
            throw e;
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
