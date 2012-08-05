package com.huateng.jbpm.test.client.jbpm.humantask;


import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

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
import org.drools.definition.process.Node;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.task.Content;
import org.jbpm.task.Group;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.test.JbpmJUnitTestCase.TestWorkItemHandler;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

public class JbpmService {
    private Log log = LogFactory.getLog(JbpmService.class);
    
    private static final long DEFAULT_WAIT_TIME = 5000;
    private TaskClient client;
    private WorkingMemoryInMemoryLogger logger;
    private EntityManagerFactory emf;
    private JPAProcessInstanceDbLog dbLog;
    private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
    private org.jbpm.task.service.TaskService taskService;
    private StatefulKnowledgeSession ksession;
    private String hostIp = "127.0.0.1";
    private int port = 9123;
    /** session是否需要持久化 **/
    private boolean sessionPersistence = true;
    
    /**
     * <p>Discription:[获取JBPM客户端，使用默认的服务器端IP和端口]</p>
     * @coustructor 方法.
     */
    public JbpmService(){
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 9123);
    }
    /**
     * <p>Discription:[获取JBPM客户端，初始化服务器端IP和端口]</p>
     * @coustructor 方法.
     */
    public JbpmService(String hostIp, int port){
        this.hostIp = hostIp;
        this.port = port;
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect(hostIp, port);
    }
    /**
     * <p>Discription:[流程启动]</p>
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
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
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Environment createEnvironment(EntityManagerFactory emf) {
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(transactionManager, null, transactionManager));
        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new MultipleUseJpaPersistenceContextManager(env));
        return env;
    }
    /**
     * <p>Discription:[创建基础知识]</p>
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public KnowledgeBase createKnowledgeBase(String... process) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String p: process) {
            //kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
            kbuilder.add(new ClassPathResource(p), ResourceType.BPMN2);
        }
        
        // Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    log.warn(error.toString());
                }
            }
        }
        return kbuilder.newKnowledgeBase();
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
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        StatefulKnowledgeSession session;
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        
        if (sessionPersistence) {
            Environment env = createEnvironment(emf);
            session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);
            new JPAWorkingMemoryDbLogger(session);
            if (dbLog == null) {
                dbLog = new JPAProcessInstanceDbLog(session.getEnvironment());
            }
        } else {
            Environment env = EnvironmentFactory.newEnvironment();
            env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
            session = kbase.newStatefulKnowledgeSession(conf, env);
            logger = new WorkingMemoryInMemoryLogger(session);
        }
        this.ksession = session;
        return session;
    }
    /**
     * <p>Discription:[创建会话]</p>
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession createKnowledgeSession(String... process) {
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
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession loadSession(int id, String... process) { 
        KnowledgeBase kbase = createKnowledgeBase(process);
        final KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        config.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, createEnvironment(emf));
        new JPAWorkingMemoryDbLogger(ksession);
        return ksession;
    }
    
    public void start(String ... process){
        KnowledgeBase kbase = null;
        if(process == null || process.length < 1 || process[0].equals("")){
            kbase = createKnowledgeBase("ProcessTask.bpmn");
        }else{
            kbase = createKnowledgeBase(process);
        }
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        BaseHumanTaskHandler humanTaskHandler = new BaseHumanTaskHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
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
            startProcess( "com.huateng.process.task", params );
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    /**
     * <p>Discription:[启动流程]</p>
     * @param taskId 要启动的流程的ID
     * @param params
     * @throws RuntimeException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startProcess(String taskId, Map<String, Object> params) throws RuntimeException {
        try {
            ksession.startProcess( taskId, params );
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage());
        }
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
    public Object getVariableValue(String name, long processInstanceId, StatefulKnowledgeSession ksession) {
        return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
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
    public List<String> getNodeTriggered(long processInstanceId) {
        List<String> names = new ArrayList<String>();
        if (sessionPersistence) {
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
                dbLog = new JPAProcessInstanceDbLog();
            }
            dbLog.clear();
        } else {
            logger.clear();
        }
    }
    
    public TestWorkItemHandler getTestWorkItemHandler() {
        return workItemHandler;
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
     * <p>Discription:[获取从开始节点到此节点的节点数量]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @param num
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int getNumOfIncommingConnections(ProcessInstance process, String nodeName, int num) {
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
     * <p>Discription:[获取从此节点开始之后的节点数量]</p>
     * @param process 流程
     * @param nodeName 此节点名称
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int getNumOfOutgoingConnections(ProcessInstance process, String nodeName) {
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
    
    public TaskService getTaskService(StatefulKnowledgeSession ksession) {
        if (taskService == null) {
            taskService = new org.jbpm.task.service.TaskService(
                emf, SystemEventListenerFactory.getSystemEventListener());
            
            Map vars = new HashMap();
            Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/LoadUsers.mvel"));     
            Map<String, User> users = ( Map<String, User> ) org.jbpm.task.service.TaskService.eval( reader, vars );
            
            reader = new InputStreamReader(this.getClass().getResourceAsStream("/LoadGroups.mvel"));
            Map<String, Group> groups = ( Map<String, Group> ) org.jbpm.task.service.TaskService.eval( reader, vars ); 
            
            taskService.addUsersAndGroups(users, groups);
        }
//        SyncWSHumanTaskHandler humanTaskHandler = new SyncWSHumanTaskHandler(
//            new LocalTaskService(taskService), ksession);
//        humanTaskHandler.setLocal(true);
//        humanTaskHandler.connect();
        BaseHumanTaskHandler humanTaskHandler = new BaseHumanTaskHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
        return new LocalTaskService(taskService);
    }
    
    public org.jbpm.task.service.TaskService getService() {
        return new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
    }
    
    /**
     * <p>Discription:[领取指定用户的任务]</p>
     * @param user
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<TaskSummary> getAssignedTasks(User user) {
        BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(user.getId(), "en-UK", taskSummaryResponseHandler);
        taskSummaryResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
        return tasks;
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
        BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(user.getId(), groups, "en-UK", taskSummaryResponseHandler);
        taskSummaryResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
        return tasks;
    }
    
    /**
     * <p>Discription:[开始任务]</p>
     * @param user 用户
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(User user, TaskSummary task) {
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), user.getId(), operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());
    }
    /**
     * <p>Discription:[开始任务]</p>
     * @param user 用户
     * @param groups 用户组
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void startTask(User user, ArrayList<String> groups, TaskSummary task) {
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claim(task.getId(), user.getId(), groups, operationResponseHandler);
        client.start(task.getId(), user.getId(), operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());
    }
    
    /**
     * <p>Discription:[完成任务]</p>
     * @param user 用户
     * @param task 任务
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(User user, TaskSummary task) {
        log.info("Completing task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), user.getId(), null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        log.info("Completed task " + task.getId());
    }
    
    /**
     * <p>Discription:[完成任务]</p>
     * @param user 用户
     * @param task 任务
     * @param contentData 传递的消息
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void completeTask(User user, TaskSummary task, ContentData contentData) {
        log.info("Completing task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), user.getId(), contentData, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        log.info("Completed task " + task.getId());
    }
    
    /**
     * <p>Discription:[获取任务内容]</p>
     * @param taskSum 任务
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object getTaskContentInput(TaskSummary taskSum) {
        BlockingGetTaskResponseHandler handlerT = new BlockingGetTaskResponseHandler();
        client.getTask(taskSum.getId(), handlerT);
        Task task2 = handlerT.getTask();
        TaskData taskData = task2.getTaskData();
        BlockingGetContentResponseHandler handlerC = new BlockingGetContentResponseHandler();
        client.getContent(taskData.getDocumentContentId(), handlerC);
        Content content = handlerC.getContent();
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
    public void stop() throws Exception {
        client.disconnect();
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
    public StatefulKnowledgeSession getKsession() {
        return ksession;
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
}