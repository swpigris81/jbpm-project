package com.webservice.jbpm.server.daemon;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
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

import com.webservice.jbpm.jpamanager.MultipleUseJpaPersistenceContextManager;
import com.webservice.jbpm.server.domain.UserInfoDomain;
import com.webservice.system.common.helper.SpringHelper;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.service.IRoleService;
import com.webservice.system.user.bean.UserInfo;
import com.webservice.system.user.service.IUserService;


/**
 * <p>Description: [JBPM服务启、停止]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
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
    private DataSource ds;
    /** 环境参数 **/
    public static Environment env;
    /** 用于查询所有用户 **/
    private IUserService userService;
    /** 用于查询所有角色 **/
    private IRoleService roleService;
    
    /**
     * <p>Discription:[初始化默认未启动]</p>
     * @coustructor 方法.
     */
    public TaskServerDaemon(){
        this.running = false;
        this.userService = (IUserService)SpringHelper.getBean("userService");
        this.roleService = (IRoleService)SpringHelper.getBean("roleService");
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
        List<UserInfo> users = this.userService.findAll();
        for(UserInfo user : users){
            taskSession.addUser(new User(user.getUserName()));
        }
        //for (String userName : getDefaultUsers()) {
        //    taskSession.addUser(new User(userName));
        //}
        //初始化用户组
        List<RoleInfo> groups = this.roleService.findAll();
        for(RoleInfo role : groups){
            taskSession.addGroup(new Group(role.getRoleName()));
        }
        //for(String group : getDefaultGroups()){
        //    taskSession.addGroup(new Group(group));
        //}
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
    public void startDb() throws Exception {
        Context ctx = new InitialContext();
        //ds = new PoolingDataSource();
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
//        noneJtaEmf = Persistence.createEntityManagerFactory("org.jbpm.task");
        jtaEmf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        
        env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, jtaEmf);
        ds = (DataSource) ctx.lookup("java:comp/env/jdbc/jbpm-ds");
        UserTransaction transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        try{
            transactionManager.begin();
            env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(transactionManager, null, null));
            env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new MultipleUseJpaPersistenceContextManager(env));
            transactionManager.commit();
        }catch(Exception e){
            transactionManager.rollback();
            throw e;
        }
    }
    /**
     * <p>Discription:[停止服务]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void stopServer() throws Exception {
        if(!isRunning()){
            return;
        }
//        try{
//            ds.close();
//        }catch(Exception e){
//            log.error("Exception while stopping dataSource " + e.getMessage());
//            throw e;
//        }
        try{
            if(noneJtaEmf != null){
                noneJtaEmf.close();
            }
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
    /**
     * <p>Discription:[默认用户]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    private String[] getDefaultUsers() {
        return new String[]{"salaboy", "translator", "admin", "reviewer", "Administrator"};
    }
    /**
     * <p>Discription:[默认用户组]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    private String[] getDefaultGroups() {
        return new String[]{"cardCenter", "merCenter", "merReview", "cardCenter2", "cardReview"};
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return IUserService userService.
     */
    public IUserService getUserService() {
        return userService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userService The userService to set.
     */
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return IRoleService roleService.
     */
    public IRoleService getRoleService() {
        return roleService;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param roleService The roleService to set.
     */
    public void setRoleService(IRoleService roleService) {
        this.roleService = roleService;
    }
    
}
