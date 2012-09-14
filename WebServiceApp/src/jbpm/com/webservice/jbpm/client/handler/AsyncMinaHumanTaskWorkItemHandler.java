package com.webservice.jbpm.client.handler;

import org.drools.SystemEventListenerFactory;
import org.drools.runtime.KnowledgeRuntime;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.utils.OnErrorAction;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class AsyncMinaHumanTaskWorkItemHandler extends
        AsyncGenericHumanTaskWorkItemHandler {
    
    public AsyncMinaHumanTaskWorkItemHandler(KnowledgeRuntime session) {
        super(session);
        init();
    }

    public AsyncMinaHumanTaskWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
        init();
    }

    private void init(){
        setClient(new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()))));
        if(getPort() <= 0){
            setPort(9123);
        }
        if(getIpAddress() == null || getIpAddress().equals("")){
            setIpAddress("127.0.0.1");
        }
    }
}
