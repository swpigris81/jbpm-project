/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webservice.jbpm.client.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.process.workitem.wsht.HumanTaskHandlerHelper;
import org.jbpm.task.AccessType;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Content;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.SubTasksStrategyFactory;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>Description: [异步处理流程, 不建议使用，因为在处理complete任务的时候会出现异常]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @see com.webservice.jbpm.client.handler.SyncHumanTaskHandler
 * @deprecated
 * @version v0.1
 */
public class AsyncHumanTaskHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AsyncHumanTaskHandler.class);
    
    private String ipAddress = "127.0.0.1";
    private int port = 9123;
    
    private long taskId;
    
    private AsyncTaskService client;
    private WorkItemManager manager = null;
    private KnowledgeRuntime session;
    private OnErrorAction action;
    private Map<TaskEventKey, EventResponseHandler> eventHandlers = new HashMap<TaskEventKey, EventResponseHandler>();
    
    public AsyncHumanTaskHandler() {
    	this.action = OnErrorAction.LOG;
    }

    public AsyncHumanTaskHandler(AsyncTaskService client) {
        this.client = client;
        this.action = OnErrorAction.LOG;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, KnowledgeRuntime session) {
        this.client = client;
        this.session = session;
        this.action = OnErrorAction.LOG;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, OnErrorAction action) {
        this.client = client;
        this.action = action;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, KnowledgeRuntime session, OnErrorAction action) {
        this.client = client;
        this.session = session;
        this.action = action;
    }

    public void setConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setClient(AsyncTaskService client) {
        this.client = client;
    }
    
    public void setAction(OnErrorAction action) {
		this.action = action;
	}
    

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return long taskId.
     */
    public long getTaskId() {
        return taskId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param taskId The taskId to set.
     */
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public void connect() {
        if (client == null) {
            throw new IllegalStateException("You must set the client to the work item to work");
        }
        if (client != null) {
            boolean connected = client.connect(ipAddress, port);
            if (!connected) {
                throw new IllegalArgumentException("Could not connect task client");
            }
        }
        registerTaskEvents();
    }

    private void registerTaskEvents() {
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler(manager, session.getEnvironment(),  client);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        client.registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
    }

    public void setManager(WorkItemManager manager) {
        this.manager = manager;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (this.manager == null) {
            this.manager = manager;
        } else {
            if (this.manager != manager) {
                throw new IllegalArgumentException(
                        "This WSHumanTaskHandler can only be used for one WorkItemManager");
            }
        }
        connect();
        Task task = new Task();
        String taskName = (String) workItem.getParameter("TaskName");
        if (taskName != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText("en-UK", taskName));
            task.setNames(names);
        }
        String comment = (String) workItem.getParameter("Comment");
        if (comment == null) {
            comment = "";
        }
        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NText("en-UK", comment));
        task.setDescriptions(descriptions);
        List<I18NText> subjects = new ArrayList<I18NText>();
        subjects.add(new I18NText("en-UK", comment));
        task.setSubjects(subjects);
        
        String priorityString = (String) workItem.getParameter("Priority");
        int priority = 0;
        if (priorityString != null) {
            try {
                priority = new Integer(priorityString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        task.setPriority(priority);

        TaskData taskData = new TaskData();
        taskData.setWorkItemId(workItem.getId());
        taskData.setProcessInstanceId(workItem.getProcessInstanceId());
        if(session != null && session.getProcessInstance(workItem.getProcessInstanceId()) != null) {
			taskData.setProcessId(session.getProcessInstance(workItem.getProcessInstanceId()).getProcess().getId());
		}
        if(session != null && (session instanceof StatefulKnowledgeSession)) { 
        	taskData.setProcessSessionId( ((StatefulKnowledgeSession) session).getId() );
        }
        taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if (parentId != null) {
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if (subTaskStrategiesCommaSeparated != null && !subTaskStrategiesCommaSeparated.equals("")) {
            String[] subTaskStrategies = subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for (String subTaskStrategyString : subTaskStrategies) {
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();

        String actorId = (String) workItem.getParameter("ActorId");
        if (actorId != null && actorId.trim().length() > 0) {
            String[] actorIds = actorId.split(",");
            for (String id : actorIds) {
                potentialOwners.add(new User(id.trim()));
            }
            //Set the first user as creator ID??? hmmm might be wrong
            if (potentialOwners.size() > 0) {
                taskData.setCreatedBy((User) potentialOwners.get(0));
            }
        }

        String groupId = (String) workItem.getParameter("GroupId");
        if (groupId != null && groupId.trim().length() > 0) {
            String[] groupIds = groupId.split(",");
            for (String id : groupIds) {
                potentialOwners.add(new Group(id.trim()));
            }
        }

        assignments.setPotentialOwners(potentialOwners);
        List<OrganizationalEntity> businessAdministrators = new ArrayList<OrganizationalEntity>();
        businessAdministrators.add(new User("Administrator"));
        assignments.setBusinessAdministrators(businessAdministrators);
        task.setPeopleAssignments(assignments);

        task.setTaskData(taskData);

        ContentData content = null;
        Object contentObject = workItem.getParameter("Content");
        if (contentObject == null) {
            contentObject = new HashMap<String, Object>(workItem.getParameters());
        }
        if (contentObject != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(contentObject);
                out.close();
                content = new ContentData();
                content.setType(contentObject.getClass().getName());
                content.setContent(bos.toByteArray());
                content.setAccessType(AccessType.Inline);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } // If the content is not set we will automatically copy all the input objects into 
        task.setDeadlines(HumanTaskHandlerHelper.setDeadlines(workItem, businessAdministrators));
        TaskAddedHandler taskHandler = new TaskAddedHandler(workItem.getId());
        client.addTask(task, content, taskHandler);
        this.taskId = taskHandler.getTaskId();
    }

    public void dispose() throws Exception {
        for(TaskEventKey key : eventHandlers.keySet()){
            client.registerForEvent(key, true, eventHandlers.get(key));
        }
        eventHandlers.clear();
        if (client != null) {
            client.disconnect();
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        GetTaskResponseHandler abortTaskResponseHandler =
                new AbortTaskResponseHandler(client);
        client.getTaskByWorkItemId(workItem.getId(), abortTaskResponseHandler);
    }
    
    private class TaskAddedHandler extends BlockingAddTaskResponseHandler implements AddTaskResponseHandler {

		private long workItemId;
		
		public TaskAddedHandler(long workItemId) {
			this.workItemId = workItemId;
		}

		@Override
		public synchronized void setError(RuntimeException error) {		
			super.setError(error);
			
			if (action.equals(OnErrorAction.ABORT)) {
				session.getWorkItemManager().abortWorkItem(workItemId);
				
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
        private Environment env;
        public TaskCompletedHandler(WorkItemManager manager, Environment env,  AsyncTaskService client) {
            this.manager = manager;
            this.client = client;
            this.env = env;
        }

        public void execute(Payload payload) {
            TaskEvent event = (TaskEvent) payload.get();
            long taskId = event.getTaskId();
            GetTaskResponseHandler getTaskResponseHandler =
                    new GetCompletedTaskResponseHandler(manager, env, client);
            client.getTask(taskId, getTaskResponseHandler);
        }

        public boolean isRemove() {
            return false;
        }
    }

    private static class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private WorkItemManager manager;
        private AsyncTaskService client;
        private Environment env;
        public GetCompletedTaskResponseHandler(WorkItemManager manager, Environment env, AsyncTaskService client) {
            this.manager = manager;
            this.client = client;
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
                    GetContentResponseHandler getContentResponseHandler =
                            new GetResultContentResponseHandler(manager, env, task, results);
                    client.getContent(contentId, getContentResponseHandler);
                } else {
                    manager.completeWorkItem(workItemId, results);
                    //session.getWorkItemManager().completeWorkItem(workItemId, results);
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
        private Environment env;
        public GetResultContentResponseHandler(WorkItemManager manager, Environment env, Task task, Map<String, Object> results) {
            this.manager = manager;
            this.task = task;
            this.results = results;
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
