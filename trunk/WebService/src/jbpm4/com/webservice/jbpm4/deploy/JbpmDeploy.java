package com.webservice.jbpm4.deploy;

import org.jbpm.api.Configuration;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;

/** 
 * <p>Description: [部署JBPM4流程文件]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class JbpmDeploy {
    private static String deployId;
    public static void main(String[] args) {
        deploy();
        //deployId = "70001";
        //unDeploy();
    }
    /**
     * <p>Discription:[部署流程图]</p>
     * @author:大牙
     * @update:2012-10-24
     */
    public static void deploy(){
        //流程引擎
        ProcessEngine processEngine = Configuration.getProcessEngine();
        //包含了用来管理发布资源的所有方法
        RepositoryService repositoryService = processEngine.getRepositoryService();
        /*
        ExecutionService executionService = processEngine.getExecutionService();
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();
        ManagementService managementService = processEngine.getManagementService();
        */
        String deploymentid = repositoryService.createDeployment().addResourceFromClasspath("com/webservice/jbpm4/jpdl/loan.jpdl.xml").deploy();
        System.out.println(deploymentid);
        JbpmDeploy.deployId = deploymentid;
    }
    /**
     * <p>Discription:[解除流程部署]</p>
     * @author:大牙
     * @update:2012-10-24
     */
    public static void unDeploy(){
        ProcessEngine processEngine = new Configuration().buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        if(JbpmDeploy.deployId != null && !"".equals(JbpmDeploy.deployId)){
            repositoryService.deleteDeployment(JbpmDeploy.deployId);
        }
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String deployId.
     */
    public String getDeployId() {
        return deployId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param deployId The deployId to set.
     */
    public void setDeployId(String deployId) {
        JbpmDeploy.deployId = deployId;
    }
}
