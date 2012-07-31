package com.huateng.jbpm.humantask;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

/**
 * <p>Description: [人工任务客户端]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$
 */
public class HumanTaskClient {
    
    private Log log = LogFactory.getLog(HumanTaskClient.class);
    
    private static final long DEFAULT_WAIT_TIME = 5000;
    private TaskClient client;
    private KnowledgeRuntimeLogger logger;
    private StatefulKnowledgeSession ksession;
    
    private String hostIp;
    private int port;
    /**
     * <p>Discription:[初始化任务客户端, 默认服务器端IP：127.0.0.1，端口：9123]</p>
     * @coustructor 方法.
     */
    public HumanTaskClient() {
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 9123);
    }
    /**
     * <p>Discription:[初始化任务客户端]</p>
     * @param hostIp 工作流服务器端IP
     * @param port 工作流服务器端端口
     * @coustructor 方法.
     */
    public HumanTaskClient(String hostIp, int port) {
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect(hostIp, port);
    }
    
    /**
     * <p>Discription:[初始化任务客户端]</p>
     * @param bool 是否使用默认服务器端IP以及端口, 默认服务器端IP：127.0.0.1，端口：9123
     * @coustructor 方法.
     */
    public HumanTaskClient(boolean bool) {
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        if(bool){
            client.connect("127.0.0.1", 9123);
        }else{
            client.connect(hostIp, port);
        }
    }
    
    /**
     * <p>Discription:[加载流程模板和注册人工任务]</p>
     * @throws RuntimeException
     * @param bool 是否采用默认工作流服务器端ip地址以及端口号
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void start(boolean bool) throws RuntimeException {
        if(bool){
            start("ProcessTask.bpmn", "127.0.0.1", 9123);
        }else{
            start("ProcessTask.bpmn", this.hostIp, this.port);
        }
    }
    /**
     * <p>Discription:[加载流程模板和注册人工任务]</p>
     * @param bpmnFile 要加载的流程图相对路径
     * @param hostIp 工作流服务器端IP地址
     * @param port 工作流服务器端端口号
     * @throws RuntimeException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void start(String bpmnFile, String hostIp, int port) throws RuntimeException {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource(bpmnFile), ResourceType.BPMN2);
        log.info("Compiling resources");
        
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError error: kbuilder.getErrors()) {
                    log.error("Error building kbase:" + error.getMessage());
                }
            }
            throw new RuntimeException("Error building kbase!");
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        ksession = kbase.newStatefulKnowledgeSession();
        logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        WSHumanTaskHandler wsHumanTaskHandler = new WSHumanTaskHandler();
        wsHumanTaskHandler.setConnection(hostIp, port);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", wsHumanTaskHandler);
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
        logger.close();
    }
    /**
     * <p>Discription:[获取工作流服务器端IP地址]</p>
     * @return String hostIp.
     */
    public String getHostIp() {
        return hostIp;
    }
    /**
     * <p>Discription:[设置工作流服务器端IP地址]</p>
     * @param hostIp The hostIp to set.
     */
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
    /**
     * <p>Discription:[获取工作流服务器端端口]</p>
     * @return int port.
     */
    public int getPort() {
        return port;
    }
    /**
     * <p>Discription:[设置工作流服务器端端口]</p>
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }
    
}
