package com.webservice.jbpm.client.service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalHumanTaskService;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

import bitronix.tm.TransactionManagerServices;

import com.webservice.jbpm.client.handler.SyncHumanTaskHandler;
import com.webservice.jbpm.process.audit.JPAFixProcessInstanceDbLog;
import com.webservice.jbpm.server.daemon.TaskServerDaemon;

/**
 * <p>Description: [同步处理流程]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class JbpmSyncService {
    private Log log = LogFactory.getLog(JbpmSyncService.class);
    
    private static final long DEFAULT_WAIT_TIME = 5000;
    private TaskClient client;
    private SyncTaskServiceWrapper syncTaskService;
    private WorkingMemoryInMemoryLogger logger;
    private EntityManagerFactory emf;
    private JPAFixProcessInstanceDbLog dbLog;
    private org.jbpm.task.service.TaskService taskService;
    private StatefulKnowledgeSession ksession;
    private ProcessInstance processInstance;
    private SyncHumanTaskHandler humanTaskHandler;
    
    private String[] process;
    private String hostIp = "127.0.0.1";
    private int port = 9123;
    /** session是否需要持久化 **/
    private boolean sessionPersistence = true;
    /** 当前是否已经连接到服务端 **/
    private boolean isConnected = false;
    /** 唯一实例 **/
    public static JbpmSyncService jbpmSyncService;
    
    /** 查询数据库起始行 **/
    private final static String FIRST_RESULT = "start";
    /** 查询数据库终止行 **/
    private final static String MAX_RESULTS = "limit";
    
    /**
     * <p>Discription:[获取JBPM客户端，使用默认的服务器端IP和端口]</p>
     * @coustructor 方法.
     */
    public JbpmSyncService(){
        //humanWorkHandler = new AsyncMinaHTWorkItemHandler(ksession);
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        syncTaskService = new SyncTaskServiceWrapper(client);
        this.hostIp = "127.0.0.1";
        this.port = 9123;
//        if(!JbpmSyncService.isConnected){
//            JbpmSyncService.isConnected = syncTaskService.connect("127.0.0.1", 9123);
//        }
    }
    
    /**
     * <p>Discription:[获取JBPM客户端，初始化服务器端IP和端口]</p>
     * @coustructor 方法.
     */
    public JbpmSyncService(String hostIp, int port){
        this.hostIp = hostIp;
        this.port = port;
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        syncTaskService = new SyncTaskServiceWrapper(client);
        //JbpmSyncService.isConnected = syncTaskService.connect(hostIp, port);
    }
    /**
     * <p>Discription:[获取唯一实例]</p>
     * @return
     * @author 大牙-小白
     * @update 2012-9-7 大牙-小白 [变更描述]
     */
    public static JbpmSyncService getInstance(){
        if(jbpmSyncService == null){
            jbpmSyncService = new JbpmSyncService();
        }
        if(!jbpmSyncService.isConnected()){
            jbpmSyncService.connect();
        }
        return jbpmSyncService;
    }
    
    /**
     * <p>Discription:[初始化实体管理工厂以及会话]</p>
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void init() throws Exception{
        //if(this.ksession == null){
            setUp();
            start(process);
        //}
    }
    
    /**
     * <p>Discription:[流程启动]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void setUp() {
        //emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        emf = TaskServerDaemon.jtaEmf;
    }
    /**
     * <p>Discription:[关闭流程]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void tearDown() throws Exception {
        if (sessionPersistence) {
            taskService = null;
            if (emf != null) {
                emf.close();
                emf = null;
            }
            // Clean up possible transactions
            Transaction tx = TransactionManagerServices.getTransactionManager().getCurrentTransaction();
            if( tx != null ) { 
                int testTxState = tx.getStatus();
                if(  testTxState != Status.STATUS_NO_TRANSACTION && 
                     testTxState != Status.STATUS_ROLLEDBACK &&
                     testTxState != Status.STATUS_COMMITTED ) { 
                    try { 
                        tx.rollback();
                    }
                    catch( Throwable t ) { 
                        // do nothing..
                    }
                }
            }
        }
    }
    /**
     * <p>Discription:[创建环境]</p>
     * @param emf 实体管理工厂
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Environment createEnvironment(EntityManagerFactory emf) throws NamingException {
//        Environment env = EnvironmentFactory.newEnvironment();
//        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
//        InitialContext ctx = new InitialContext();
//        UserTransaction transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
//        env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(transactionManager, null, transactionManager));
//        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new MultipleUseJpaPersistenceContextManager(env));
//        return env;
        return TaskServerDaemon.env;
    }
    /**
     * <p>Discription:[创建基础知识]</p>
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public KnowledgeBase createKnowledgeBase(String... process) {
        try{
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            if(process == null || process.length < 1 || process[0].equals("")){
                kbuilder.add(new ClassPathResource("ProcessTask.bpmn"), ResourceType.BPMN2);
            }else{
                for (String p: process) {
                    log.info(p);
                    log.info(new ClassPathResource(p));
                    //kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
                    kbuilder.add(new ClassPathResource(p), ResourceType.BPMN2);
                    //kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
                }
            }
            
            // Check for errors
            if (kbuilder.hasErrors()) {
                if (kbuilder.getErrors().size() > 0) {
                    for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                        //log.warn(error.toString());
                        log.error(error.getMessage());
                        System.err.println( error.toString() );
                    }
                    
                }
                throw new IllegalStateException("BPMN2 ERRORs");
            }
            return kbuilder.newKnowledgeBase();
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    /**
     * <p>Discription:[创建基础知识]</p>
     * @param resources 流程图资源
     * @return
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public KnowledgeBase createKnowledgeBase(Map<String, ResourceType> resources) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (Map.Entry<String, ResourceType> entry: resources.entrySet()) {
            kbuilder.add(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
        }
        return kbuilder.newKnowledgeBase();
    }
    /**
     * <p>Discription:[创建会话]</p>
     * @param kbase 基础知识
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @throws Exception 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) throws Exception {
        StatefulKnowledgeSession session = null;
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        Context ctx = new InitialContext();
        UserTransaction transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        try{
            if (sessionPersistence) {
                Environment env = createEnvironment(emf);
                session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);
                new JPAWorkingMemoryDbLogger(session);
                if (dbLog == null) {
                    dbLog = new JPAFixProcessInstanceDbLog(session.getEnvironment());
                }
            } else {
                Environment env = EnvironmentFactory.newEnvironment();
                env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
                session = kbase.newStatefulKnowledgeSession(conf, env);
                logger = new WorkingMemoryInMemoryLogger(session);
            }
        }catch(Exception e){
            throw e;
        }
        this.ksession = session;
        return session;
    }
    /**
     * <p>Discription:[创建会话]</p>
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession createKnowledgeSession(String... process) throws Exception {
        KnowledgeBase kbase = createKnowledgeBase(process);
        return createKnowledgeSession(kbase);
    }
    /**
     * <p>Discription:[恢复会话]</p>
     * @param ksession 会话
     * @param noCache 是否有缓存
     * @return
     * @throws SystemException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession, boolean noCache) throws SystemException {
        if (sessionPersistence) {
            int id = ksession.getId();
            KnowledgeBase kbase = ksession.getKnowledgeBase();
//            Transaction tx = TransactionManagerServices.getTransactionManager().getCurrentTransaction();
//            if( tx != null ) { 
//                int txStatus = tx.getStatus();
//            }
            Environment env = null;
            if (noCache) {
                emf.close();
                env = EnvironmentFactory.newEnvironment();
                emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
                env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
                env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
                JPAProcessInstanceDbLog.setEnvironment(env);
                taskService = null;
            } else {
                env = ksession.getEnvironment();
                taskService = null;
            }
            KnowledgeSessionConfiguration config = ksession.getSessionConfiguration();
            ksession.dispose();
            
            // reload knowledge session 
            ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, env);
            new JPAWorkingMemoryDbLogger(ksession);
            return ksession;
        } else {
            return ksession;
        }
    }
    /**
     * <p>Discription:[获取会话]</p>
     * @param id 要获取的会话ID
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession loadSession(int id, String... process) throws Exception { 
        KnowledgeBase kbase = createKnowledgeBase(process);
        final KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        config.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, createEnvironment(emf));
        new JPAWorkingMemoryDbLogger(ksession);
        return ksession;
    }
    
    /**
     * <p>Discription:[创建会话]</p>
     * @param process
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void start(String ... process) throws Exception{
        KnowledgeBase kbase = null;
        if(process == null || process.length < 1 || process[0].equals("")){
            kbase = createKnowledgeBase("ProcessTask.bpmn");
        }else{
            kbase = createKnowledgeBase(process);
        }
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        humanTaskHandler = new SyncHumanTaskHandler(syncTaskService, ksession);
//        humanTaskHandler.connect();
//        humanTaskHandler.setLocal(true);
        //ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
//        System.setProperty("jbpm.usergroup.callback", "org.jbpm.task.service.DefaultUserGroupCallbackImpl");
        //humanWorkHandler.
    }
    
    /**
     * <p>Discription:[启动流程]</p>
     * @throws RuntimeException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startProcess() throws RuntimeException {
        try {
            startProcess(null);
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    /**
     * <p>Discription:[启动流程]</p>
     * @param params
     * @throws RuntimeException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startProcess(Map<String, Object> params) throws RuntimeException {
        try {
            startProcess("com.webservice.process.task", params);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    /**
     * <p>Discription:[启动流程]</p>
     * @param taskId 要启动的流程的ID
     * @param params
     * @author:[创建者中文名字]
     * @throws Exception 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startProcess(String processId, Map<String, Object> params) throws Exception {
        try {
            setProcessInstance(ksession.startProcess( processId, params ));
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            if(ex.getMessage().indexOf("Already connected. Disconnect first.") > -1){
            	try {
					syncTaskService.disconnect();
					startProcess(processId, params);
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
            }
        }
    }
    /**
     * <p>Discription:[获取会话]</p>
     * @param sessionId 会话ID
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession getKSession(int sessionId) throws Exception{
        if(sessionId != -1){
            //获取历史会话
            return loadSession(sessionId, "");
        }else{
            //获取当前会话
            return ksession;
        }
    }
    /**
     * <p>Discription:[获取会话ID]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int getKSessionId(){
        return ksession.getId();
    }
    /**
     * <p>Discription:[获取指定实体管理工厂]</p>
     * @param entityName
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public EntityManagerFactory getEntityManagerFactory(String entityName){
        if(entityName == null || "".equals(entityName.trim())){
            entityName = "org.jbpm.persistence.jpa";
        }
        return Persistence.createEntityManagerFactory(entityName);
    }
    
    /**
     * <p>Discription:[获取流程]</p>
     * @param processInstanceId 流程ID
     * @param ksession 会话
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public WorkflowProcessInstance getProcessInstance(long processInstanceId, StatefulKnowledgeSession ksession){
        return (WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId);
    }
    
    /**
     * <p>Discription:[获取变量值]</p>
     * @param name 变量名
     * @param processInstanceId 流程
     * @param ksession 会话
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object getVariableValue(String name, long processInstanceId) {
        try{
            return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
        }catch(NullPointerException e){
            return null;
        }
    }
    
    /**
     * <p>Discription:[设置变量]</p>
     * @param name 变量名
     * @param value 值
     * @param processInstanceId 流程ID
     * @param ksession 会话
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public void setVariableValue(String name, Object value, long processInstanceId) {
        ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).setVariable(name, value);
    }
    /**
     * <p>Discription:[设置变量值]</p>
     * @param name 变量名
     * @param value 值
     * @author 大牙-小白
     * @update 2012-9-14 大牙-小白 [变更描述]
     */
    public void setVariableValue(String name, Object value){
        ((WorkflowProcessInstance)this.getProcessInstance()).setVariable(name, value);
    }
    
    /**
     * <p>Discription:[设置变量值]</p>
     * @param name 变量名
     * @param value 值
     * @author 大牙-小白
     * @update 2012-9-14 大牙-小白 [变更描述]
     */
    public Object getVariableValue(String name){
        return ((WorkflowProcessInstance)this.getProcessInstance()).getVariable(name);
    }
    
    /**
     * <p>Discription:[获取激活状态的节点]</p>
     * @param processInstanceId 流程ID
     * @param ksession 会话
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<String> getNodeActive(long processInstanceId, StatefulKnowledgeSession ksession) {
        List<String> names = new ArrayList<String>();
        
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance instanceof WorkflowProcessInstance) {
            getNodeActive((WorkflowProcessInstance) processInstance, names);
        }
        return names;
    }
    /**
     * <p>Discription:[获取激活状态的节点]</p>
     * @param container 流程节点实例
     * @param names 保存所有节点的集合
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void getNodeActive(NodeInstanceContainer container, List<String> names) {
        for (NodeInstance nodeInstance: container.getNodeInstances()) {
            String nodeName = nodeInstance.getNodeName();
            names.add(nodeName);
            if (nodeInstance instanceof NodeInstanceContainer) {
                getNodeActive((NodeInstanceContainer) nodeInstance, names);
            }
        }
    }
    /**
     * <p>Discription:[获取流程内触发过的节点]</p>
     * @param processInstanceId 流程
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<String> getNodeNameTriggered(long processInstanceId) {
        List<String> names = new ArrayList<String>();
        if (sessionPersistence) {
            if(dbLog == null){
                dbLog = new JPAFixProcessInstanceDbLog(ksession.getEnvironment());
            }
            List<NodeInstanceLog> logs = dbLog.findNodeInstances(processInstanceId);
            if (logs != null) {
                for (NodeInstanceLog l: logs) {
                    String nodeName = l.getNodeName();
                    if (l.getType() == NodeInstanceLog.TYPE_ENTER || l.getType() == NodeInstanceLog.TYPE_EXIT) {
                        names.add(nodeName);
                    }
                }
            }
        } else {
            for (LogEvent event: logger.getLogEvents()) {
                if (event instanceof RuleFlowNodeLogEvent) {
                    String nodeName = ((RuleFlowNodeLogEvent) event).getNodeName();
                    names.add(nodeName);
                }
            }
        }
        return names;
    }
    /**
     * <p>Discription:[清除历史]</p>
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void clearHistory() {
        if (sessionPersistence) {
            if (dbLog == null) {
                dbLog = new JPAFixProcessInstanceDbLog();
            }
            dbLog.clear();
        } else {
            logger.clear();
        }
    }
    
    /**
     * <p>Discription:[判断变量是否在流程内]</p>
     * @param process 流程
     * @param processVarNames 流程变量
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public boolean getProcessVarExists(ProcessInstance process, String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: processVarNames) {
            names.add(nodeName);
        }
        
        for(String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }
        
        if (!names.isEmpty()) {
            //names中的变量不在此流程内
            return false;
        }else{
            return true;
        }
    }
    /**
     * <p>Discription:[判断节点是否在该流程内]</p>
     * @param process 流程
     * @param nodeNames 节点
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public boolean isNodeExists(ProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: nodeNames) {
            names.add(nodeName);
        }
        
        for(Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }
        
        if (!names.isEmpty()) {
            //names集合中的节点不在流程内
            return false;
        }else{
            //所有节点都在流程内
            return true;
        }
    }
    /**
     * <p>Discription:[获取流程图中的所有节点信息, 包括开始和结束节点]</p>
     * @param process
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Node[] getNodeTriggered(long processInstanceId, StatefulKnowledgeSession ksession) {
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        WorkflowProcessInstanceImpl instance = null;
        if (processInstance instanceof WorkflowProcessInstanceImpl) {
            instance = (WorkflowProcessInstanceImpl) processInstance;
        }
        Node [] nodes = instance.getNodeContainer().getNodes();
        return nodes;
    }
    /**
     * <p>Discription:[获取当前节点]</p>
     * @param task 当前任务
     * @param ksession 会话
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Node getCurrentNode(Task task, StatefulKnowledgeSession ksession) {
        ProcessInstance processInstance = ksession.getProcessInstance(task.getTaskData().getProcessInstanceId());
        WorkflowProcessInstanceImpl instance = null;
        if (processInstance instanceof WorkflowProcessInstanceImpl) {
            instance = (WorkflowProcessInstanceImpl) processInstance;
        }
        //ProcessContext kcontext = new org.drools.spi.ProcessContext(ksession);
        WorkItemNodeInstance wni = findNodeInstance(task.getTaskData().getWorkItemId(), instance);
        return wni.getNode();
    }
    
    private WorkItemNodeInstance findNodeInstance(long workItemId, NodeInstanceContainer container) {
        for (NodeInstance nodeInstance: container.getNodeInstances()) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) nodeInstance;
                if (workItemNodeInstance.getWorkItem().getId() == workItemId) {
                    return workItemNodeInstance;
                }
            }
            if (nodeInstance instanceof NodeInstanceContainer) {
                WorkItemNodeInstance result = findNodeInstance(workItemId, ((NodeInstanceContainer) nodeInstance));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    /**
     * <p>Discription:[获取从开始节点到此节点的节点数量]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @param num
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int getNumOfNodeIncommingConnections(ProcessInstance process, String nodeName) {
        if(isNodeExists(process, nodeName)){
            WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
            for(Node node : instance.getNodeContainer().getNodes()) {
                if(node.getName().equals(nodeName)) {
                    return node.getIncomingConnections().size();
                }
            }
        }
        return 0;
    }
    /**
     * <p>Discription:[获取从开始节点到此节点过程中的所有节点]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Map<String, List<Connection>> getNodeIncommingConnections(ProcessInstance process, String nodeName) {
        if(isNodeExists(process, nodeName)){
            WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
            for(Node node : instance.getNodeContainer().getNodes()) {
                if(node.getName().equals(nodeName)) {
                    return node.getIncomingConnections();
                }
            }
        }
        return null;
    }
    
    /**
     * <p>Discription:[获取从此节点开始之后的节点数量]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int getNumOfNodeOutgoingConnections(ProcessInstance process, String nodeName) {
        if(isNodeExists(process, nodeName)){
            WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
            for(Node node : instance.getNodeContainer().getNodes()) {
                if(node.getName().equals(nodeName)) {
                    return node.getOutgoingConnections().size();
                }
            }
        }
        return 0;
    }
    /**
     * <p>Discription:[获取从此节点出发之后的节点]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Map<String, List<Connection>> getNodeOutgoingConnections(ProcessInstance process, String nodeName) {
        if(isNodeExists(process, nodeName)){
            WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
            for(Node node : instance.getNodeContainer().getNodes()) {
                if(node.getName().equals(nodeName)) {
                    return node.getOutgoingConnections();
                }
            }
        }
        return null;
    }
    /**
     * <p>Discription:[根据节点名称获取节点]</p>
     * @param process 流程
     * @param nodeName 节点名称
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Node getNodeByNodeName(ProcessInstance process, String nodeName){
        if(isNodeExists(process, nodeName)){
            WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
            for(Node node : instance.getNodeContainer().getNodes()) {
                if(node.getName().equals(nodeName)) {
                    return node;
                }
            }
        }
        return null;
    }
    
    /**
     * <p>Discription:[手动查询数据]</p>
     * @param EntityManagerFactory emf 
     * @param queryString 查询语句
     * @param params 查询参数，必须与sql中的参数名称匹配
     * @param singleResult 是否只查询一条记录。true：只返回一条记录，false：返回所有记录
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object queryByParams(EntityManagerFactory emf, String queryString, Map<String, Object> params, boolean singleResult){
        TaskServiceSession ssession = null;
        if(emf == null){
            ssession = getService().createSession();
        }else{
            ssession = getService(emf).createSession();
        }
        Query query = ssession.getTaskPersistenceManager().createQuery(queryString);
        if( params != null && ! params.isEmpty() ) { 
            for( String name : params.keySet() ) { 
                if( FIRST_RESULT.equals(name) ) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if( MAX_RESULTS.equals(name) ) { 
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                query.setParameter(name, params.get(name) );
            }
        }
        if( singleResult ) { 
            return query.getSingleResult();
        }
        return query.getResultList();
    }
    /**
     * <p>Discription:[手动查询数据]</p>
     * @param EntityManagerFactory emf 
     * @param queryString 查询语句（参照Taskorm.xml文件）
     * @param params 查询参数，必须与sql中的参数名称匹配
     * @param singleResult 是否只查询一条记录。true：只返回一条记录，false：返回所有记录
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object queryByNamedQueryParams(EntityManagerFactory emf, String queryString, Map<String, Object> params, boolean singleResult){
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }
        EntityManager em = emf.createEntityManager();
        
        em.getTransaction().begin();
        Query query = em.createNamedQuery(queryString);
        log.info("调用的查询语句: " + queryString);
        log.info("查询参数：" + params);
        if( params != null && ! params.isEmpty() ) { 
            for( String name : params.keySet() ) { 
                if( FIRST_RESULT.equals(name) ) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if( MAX_RESULTS.equals(name) ) { 
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                query.setParameter(name, params.get(name) );
            }
        }
        Object obj = null;
        if( singleResult ) { 
            obj = query.getSingleResult();
        }
        obj = query.getResultList();
        em.getTransaction().commit();
        em.close();
        emf.close();
        return obj;
    }
    /**
     * <p>Discription:[获取任务服务接口]</p>
     * @param ksession
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public TaskService getTaskService(StatefulKnowledgeSession ksession) {
        return LocalHumanTaskService.getTaskService(ksession);
    }
    /**
     * <p>Discription:[获取任务服务]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public org.jbpm.task.service.TaskService getService() {
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }
        return new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
    }
    /**
     * <p>Discription:[获取任务服务]</p>
     * @param emf
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public org.jbpm.task.service.TaskService getService(EntityManagerFactory emf) {
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }
        return new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
    }
    /**
     * <p>Discription:[流程启动之后（前提条件），获取任务ID, 此任务ID只是启动流程时的ID, 每次重新启动流程此ID将会被修改]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public synchronized long getTaskId(){
        //return humanTaskHandler.getTaskId();
        return 0;
//        return SyncHumanTaskHandler.taskId;
    }
    /**
     * <p>Discription:[根据任务编号查找任务]</p>
     * @param taskId 任务编号
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Task getTaskById(long taskId){
        Task task = syncTaskService.getTask(taskId);
        return task;
    }
    /**
     * <p>Discription:[分配任务给新用户]</p>
     * @param taskId 任务ID
     * @param srcUserId 任务原用户
     * @param targetUserId 任务新用户
     * @author 大牙-小白
     * @update 2012-9-4 大牙-小白 [变更描述]
     */
    public void assignTaskToUser(Long taskId, String srcUserId, String targetUserId){
        log.info("assigning task from "+ srcUserId +" to new user : " + targetUserId);
        syncTaskService.delegate(taskId, srcUserId, targetUserId);
        log.info("assigning task from "+ srcUserId +" to new user : " + targetUserId);
    }
    
    /**
     * <p>Discription:[领取指定用户的任务]</p>
     * @param user
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<TaskSummary> getAssignedTasks(User user) {
        return syncTaskService.getTasksAssignedAsPotentialOwner(user.getId(), "en-UK");
    }
    /**
     * <p>Discription:[领取指定group的用户认为]</p>
     * @param user 用户
     * @param groups 用户组
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<TaskSummary> getAssignedTasks(User user, List<String> groups) {
//        BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
//        client.getTasksAssignedAsPotentialOwner(user.getId(), groups, "en-UK", taskSummaryResponseHandler);
//        taskSummaryResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
//        List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
        return syncTaskService.getTasksAssignedAsPotentialOwner(user.getId(), groups, "en-UK");
//        return tasks;
    }
    
    /**
     * <p>Discription:[开始任务]</p>
     * @param user 用户
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(User user, Long taskId) {
        System.out.println("Starting task " + taskId);
        syncTaskService.start(taskId, user.getId());
        System.out.println("Started task " + taskId);
    }
    /**
     * <p>Discription:[开始任务]</p>
     * @param user 用户
     * @param groups 用户组
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(User user, List<String> groups, Long taskId) {
        if(groups != null && !groups.isEmpty()){
            syncTaskService.claim(taskId, user.getId(), groups);
        }
        System.out.println("Starting task " + taskId);
        startTask(user, taskId);
        System.out.println("Started task " + taskId);
    }
    
    /**
     * <p>Discription:[完成任务]</p>
     * @param user 用户
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(User user, Long taskId) {
        log.info("Completing task " + taskId);
        syncTaskService.complete(taskId, user.getId(), null);
        log.info("Completed task " + taskId);
    }
    
    /**
     * <p>Discription:[完成任务]</p>
     * @param user 用户
     * @param task 任务
     * @param contentData 传递的消息
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(User user, Long taskId, ContentData contentData) {
        log.info("Completing task " + taskId);
        syncTaskService.complete(taskId, user.getId(), contentData);
        log.info("Completed task " + taskId);
    }
    
    /**
     * <p>Discription:[完成任务]</p>
     * @param user 用户
     * @param task 任务
     * @param data 传递的消息
     * @param notUse 扩展参数，暂时未用
     * @author:[创建者中文名字]
     * @throws Exception 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(User user, Long taskId, Map data, String notUse) throws Exception {
        log.info("Completing task " + taskId);
        syncTaskService.completeWithResults(taskId, user.getId(), data);
        log.info("Completed task " + taskId);
    }
    
    /**
     * <p>Discription:[获取任务内容]</p>
     * @param taskSum 任务
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object getTaskContentInput(Long taskId) {
        Task task2 = syncTaskService.getTask(taskId);
        TaskData taskData = task2.getTaskData();
        Content content = syncTaskService.getContent(taskData.getDocumentContentId());
        ByteArrayInputStream bais = new ByteArrayInputStream(
                content.getContent());
        try {
            ObjectInputStream is = new ObjectInputStream(bais);
            Object obj = null;

            while ((obj = is.readObject()) != null) {
                System.out.println("OBJECT: " + obj);
                return obj;
            }
        } catch (Exception e) {
            System.err.print("There was an error reading task input...");
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    /**
     * <p>Discription:[流程任务结束]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public synchronized void stop() throws Exception {
        //wait for 1 min
        if(isConnected()){
            wait(DEFAULT_WAIT_TIME * 12);
            syncTaskService.disconnect();
            setConnected(false);
        }
    }
    /**
     * <p>Discription:[连接JBPM流程服务]</p>
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public boolean connect(){
        boolean conned = syncTaskService.connect(hostIp, port);
        setConnected(conned);
        return conned;
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String hostIp.
     */
    public String getHostIp() {
        return hostIp;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param hostIp The hostIp to set.
     */
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return int port.
     */
    public int getPort() {
        return port;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return StatefulKnowledgeSession ksession.
     */
    public StatefulKnowledgeSession getKSession() {
        return ksession;
    }
    
    public SessionInfo getSessionInfo(int sessionId){
        PersistenceContext context = ((ProcessPersistenceContextManager) TaskServerDaemon.env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        SessionInfo sessionInfo = context.findSessionInfo(sessionId);
        return sessionInfo;
    }
    
    public StatefulKnowledgeSession getKSession(SessionInfo sessionInfo){
        if(sessionInfo != null){
            return sessionInfo.getJPASessionMashallingHelper().getObject();
        }
        return null;
    }
    /**
     * <p>Discription:[获取非持久化Ksession]</p>
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession getNonJPAKSession(){
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        KnowledgeBase kbase = createKnowledgeBase(process);
        Environment env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = getEntityManagerFactory("org.jbpm.task");
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        return kbase.newStatefulKnowledgeSession(conf, env);
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param ksession The ksession to set.
     */
    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return boolean sessionPersistence.
     */
    public boolean isSessionPersistence() {
        return sessionPersistence;
    }
    /**
     * <p>Discription:[是否需要持久化会话，默认true]</p>
     * @param sessionPersistence The sessionPersistence to set.
     */
    public void setSessionPersistence(boolean sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String[] process.
     */
    public String[] getProcess() {
        return process;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param process The process to set.
     */
    public void setProcess(String... process) {
        this.process = process;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return ProcessInstance processInstance.
     */
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processInstance The processInstance to set.
     */
    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return EntityManagerFactory emf.
     */
    public EntityManagerFactory getEmf() {
        return emf;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param emf The emf to set.
     */
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return boolean isConnected.
     */
    public boolean isConnected() {
        return isConnected;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param isConnected The isConnected to set.
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    
}
