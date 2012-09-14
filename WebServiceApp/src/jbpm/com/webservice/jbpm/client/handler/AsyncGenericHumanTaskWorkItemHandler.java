package com.webservice.jbpm.client.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.process.workitem.wsht.AbstractHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClientHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.ContentMarshallerContext;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class AsyncGenericHumanTaskWorkItemHandler extends
    AbstractHumanTaskWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AsyncGenericHumanTaskWorkItemHandler.class);
    private AsyncTaskService client;
    private String ipAddress;
    private int port;
    private WorkItemManager manager;
    private boolean local = false;
    private boolean connected = false;
    
    public AsyncGenericHumanTaskWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
    }

    public AsyncGenericHumanTaskWorkItemHandler(KnowledgeRuntime session) {
        super(session);
    }

    public AsyncTaskService getClient() {
        return client;
    }

    public void setClient(AsyncTaskService client) {
        this.client = client;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isLocal() {
        return local;
    }

    public int getPort() {
        return port;
    }
    

    private void registerTaskEvents() {
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler(manager, marshallerContext, session.getEnvironment(),  client);
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
    }

    public void connect() {
        if (client == null) {
            throw new IllegalStateException("You must set the Task Service Client to the work item to work");
        }
        if (ipAddress == null || ipAddress.equals("") || port <= 0) {
            throw new IllegalStateException("You must set the IP and Port to the work item to work");
        }
        if (client != null) {
            if(!connected){
                connected = client.connect(ipAddress, port);
                if (!connected) {
                    throw new IllegalArgumentException("Could not connect task client");
                }
                registerTaskEvents();
            }else{
                logger.warn(" Task Service Client was already connected, just saying ... ");
            }
        }
    }

    public void dispose() throws Exception {
        for (TaskEventKey key : eventHandlers.keySet()) {
            client.registerForEvent(key, true, eventHandlers.get(key));
        }
        eventHandlers.clear();
        if (client != null) {
            client.disconnect();
        }

    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        this.manager = manager;
        Task task = createTaskBasedOnWorkItemParams(workItem);
        ContentData content = createTaskContentBasedOnWorkItemParams(workItem);
        connect();
        client.addTask(task, content, new TaskAddedHandler(workItem.getId()));

        
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        GetTaskResponseHandler abortTaskResponseHandler = new AbortTaskResponseHandler(client);
        client.getTaskByWorkItemId(workItem.getId(), abortTaskResponseHandler);
    }

    private class TaskAddedHandler extends AbstractBaseResponseHandler implements TaskClientHandler.AddTaskResponseHandler {

        private long workItemId;
        
        public TaskAddedHandler(long workItemId) {
            this.workItemId = workItemId;
        }
        public void execute(long taskId) {
            
        }

        @Override
        public synchronized void setError(RuntimeException error) {     
            super.setError(error);
            
            if (action.equals(OnErrorAction.ABORT)) {
                manager.abortWorkItem(workItemId);
                
            } else if (action.equals(OnErrorAction.RETHROW)) {
                throw getError();
                
            } else if (action.equals(OnErrorAction.LOG)) {
                StringBuffer logMsg = new StringBuffer();
                logMsg.append(new Date() + ": Error when creating task on task server for work item id " + workItemId);
                logMsg.append(". Error reported by task server: " + getError().getMessage() );
                logger.error(logMsg.toString(), getError());
            }
        }
    
    }

    private static class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

        private WorkItemManager manager;
        private AsyncTaskService client;
        private ContentMarshallerContext marshallContext;
        private Environment env;
        public TaskCompletedHandler(WorkItemManager manager, ContentMarshallerContext marshallContext, Environment env,  AsyncTaskService client) {
            this.manager = manager;
            this.client = client;
            this.marshallContext = marshallContext;
            this.env = env;
        }

        public void execute(Payload payload) {
            TaskEvent event = (TaskEvent) payload.get();
            long taskId = event.getTaskId();
            TaskClientHandler.GetTaskResponseHandler getTaskResponseHandler =
                    new GetCompletedTaskResponseHandler(manager, marshallContext, env, client);
            client.getTask(taskId, getTaskResponseHandler);
        }

        public boolean isRemove() {
            return false;
        }
    }

    private static class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private WorkItemManager manager;
        private AsyncTaskService client;
        private ContentMarshallerContext marshallContext;
        private Environment env;
        public GetCompletedTaskResponseHandler(WorkItemManager manager, ContentMarshallerContext marshallContext,Environment env, AsyncTaskService client) {
            this.manager = manager;
            this.client = client;
            this.marshallContext = marshallContext;
            this.env = env;
        }

        public void execute(Task task) {
            long workItemId = task.getTaskData().getWorkItemId();
            if (task.getTaskData().getStatus() == Status.Completed) {
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    TaskClientHandler.GetContentResponseHandler getContentResponseHandler =
                            new GetResultContentResponseHandler(manager, marshallContext, env, task, results);
                    client.getContent(contentId, getContentResponseHandler);
                } else {
                    manager.completeWorkItem(workItemId, results);
                }
            } else {
                manager.abortWorkItem(workItemId);
            }
        }
    }

    private static class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

        private WorkItemManager manager;
        private Task task;
        private Map<String, Object> results;
        private ContentMarshallerContext marshallContext;
        private Environment env;
        public GetResultContentResponseHandler(WorkItemManager manager, ContentMarshallerContext marshallContext, Environment env, Task task, Map<String, Object> results) {
            this.manager = manager;
            this.task = task;
            this.results = results;
            this.marshallContext = marshallContext;
            this.env = env;
        }

        public void execute(Content content) {
            ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(bis);
                Object result = in.readObject();
                in.close();
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }
                manager.completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static class AbortTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private AsyncTaskService client;

        public AbortTaskResponseHandler(AsyncTaskService client) {
            this.client = client;
        }

        public void execute(Task task) {
            if (task != null) {
                client.exit(task.getId(), "Administrator", null);
            }
        }
    }
}
