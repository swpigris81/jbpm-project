package com.webservice.jbpm.service.impl;

import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.TaskService;

import com.webservice.jbpm.server.JbpmSupport;
import com.webservice.jbpm.service.JbpmService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$ 
 */
public class JbpmServiceImpl implements JbpmService {
    private JbpmSupport jbpmSupport;
    /**
     * <p>Discription:[启动工作流]</p>
     * @param processId 启动工作流ID
     * @param reqMap 启动参数
     * @param process 启动工作流程图地址
     * @return
     * @throws Exception
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public ProcessInstance startProcess(String processId, Map<String, Object> reqMap, String... process) throws Exception{
        if(jbpmSupport == null){
            throw new Exception("工作流框架初始化异常！");
        }
        jbpmSupport.setProcess(process);
        jbpmSupport.init();
        StatefulKnowledgeSession ksession = jbpmSupport.getKsession();
        if(ksession == null){
            throw new Exception("工作流初始化异常！");
        }
        ProcessInstance processInstance = ksession.startProcess(processId, reqMap);
        ksession.fireAllRules();
        return processInstance;
    }
    
    public List getAssignedTasksList(String user, List<String> group){
        TaskService taskService = jbpmSupport.getTaskService();
        return null;
    }
    
    
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return JbpmSupport jbpmSupport.
     */
    public JbpmSupport getJbpmSupport() {
        return jbpmSupport;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param jbpmSupport The jbpmSupport to set.
     */
    public void setJbpmSupport(JbpmSupport jbpmSupport) {
        this.jbpmSupport = jbpmSupport;
    }
}
