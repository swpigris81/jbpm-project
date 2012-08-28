package com.webservice.jbpm.client.handler;

import java.util.Date;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.wsht.AsyncWSHumanTaskHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.utils.OnErrorAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * <p>Description: [添加获取任务ID的功能]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$ 
 */
public class AsyncHumanTaskHandler extends AsyncWSHumanTaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHumanTaskHandler.class);
    
    private OnErrorAction action;
    private KnowledgeRuntime session;
    private AsyncTaskService client;
    private Long taskId;
    
    public AsyncHumanTaskHandler() {
        super();
        this.action = OnErrorAction.LOG;
    }

    public AsyncHumanTaskHandler(AsyncTaskService client) {
        super(client);
        this.client = client;
        this.action = OnErrorAction.LOG;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, KnowledgeRuntime session) {
        super(client, session);
        this.client = client;
        this.session = session;
        this.action = OnErrorAction.LOG;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, OnErrorAction action) {
        super(client, action);
        this.client = client;
        this.action = action;
    }
    
    public AsyncHumanTaskHandler(AsyncTaskService client, KnowledgeRuntime session, OnErrorAction action) {
        super(client, session, action);
        this.client = client;
        this.session = session;
        this.action = action;
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
                logMsg.append(". Error reported by task server: " + getError().getMessage());
                logger.error(logMsg.toString(), getError());
            }

        }
    }
    
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        super.executeWorkItem(workItem, manager);
        TaskAddedHandler handler = new TaskAddedHandler(workItem.getId());
        this.taskId = handler.getTaskId();
        logger.info("新任务ID：" + taskId);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Long taskId.
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param taskId The taskId to set.
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
}
