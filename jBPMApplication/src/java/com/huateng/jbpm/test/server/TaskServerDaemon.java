package com.huateng.jbpm.test.server;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.impl.EnvironmentFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.huateng.jbpm.test.client.jbpm.humantask.MultipleUseJpaPersistenceContextManager;
import com.huateng.jbpm.test.server.domain.UserInfoDomain;

/**
 * 服务器监听类
 * @author liubo
 *
 */
public class TaskServerDaemon {
    private Log log = LogFactory.getLog(TaskServerDaemon.class);
    
    /** 工作流是否正在运行中 **/
    private boolean running;
    /** 工作流服务 **/
    private TaskServer taskServer;
    /** 工作流运行的线程 **/
    private Thread thread = null;
    /** JTA实体管理工厂 **/
    public static EntityManagerFactory jtaEmf;
    /** 非JTA实体管理工厂 **/
    private EntityManagerFactory noneJtaEmf;
    /** JNDI数据源(通过JAVA代码实现) **/
    private PoolingDataSource ds;
    
    public static Environment env;
    
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
        TaskService taskService = new TaskService(jtaEmf, SystemEventListenerFactory.getSystemEventListener());
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
//        ds.setUniqueName("jbpm-ds");
//        //ds.setClassName("com.mysql.jdbc.Driver");
//        //ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
//        ds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
//        ds.setMaxPoolSize(3);
//        ds.setAllowLocalTransactions(true);
//        ds.getDriverProperties().put("user", "payment");
//        ds.getDriverProperties().put("password", "123456");
//        ds.getDriverProperties().put("url", "jdbc:db2://192.168.0.108:50001/payment");
//        ds.getDriverProperties().put("driverClassName","com.ibm.db2.jcc.DB2Driver");
//        ds.init();
//        ctx.bind("jbpm-ds", ds);
        noneJtaEmf = Persistence.createEntityManagerFactory("org.jbpm.task");
        jtaEmf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        
        env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, jtaEmf);
        UserTransaction transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(transactionManager, null, transactionManager));
        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new MultipleUseJpaPersistenceContextManager(env));
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
            noneJtaEmf.close();
        }catch(Exception e){
            log.error("Exception while stopping entity manager factory " + e.getMessage());
            throw e;
        }
        try{
            if(jtaEmf != null){
                jtaEmf.close();
            }
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
