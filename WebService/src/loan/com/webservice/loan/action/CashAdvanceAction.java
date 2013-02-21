package com.webservice.loan.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.api.Configuration;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.subethamail.wiser.Wiser;

import com.webservice.common.action.BaseAction;
import com.webservice.jbpm4.vo.ProcessDefinitionVO;
import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.service.CashAdvanceService;
import com.webservice.loan.service.CashTaskService;
import com.webservice.loan.vo.StatisticsVo;
import com.webservice.system.role.service.IRoleService;
import com.webservice.system.util.Base64Coder;
/**
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceAction extends BaseAction {
    private CashAdvanceService cashAdvanceService;
    private CashTaskService cashTaskService;
    private IRoleService roleService;
    /** 事务处理 */
    private DataSourceTransactionManager transactionManager;
    
    private JtaTransactionManager springJTM;
    /** 当前用户ID **/
    private String currentUserId;
    /** 当前用户 **/
    private String currentUserName;
    /** 请款信息 **/
    private CashAdvanceInfo cashAdvanceInfo;
    /**
     * 请款流程文件
     */
    private File process;
    /**
     * 请款流程文件类型
     */
    private String processContentType;
    /**
     * 请款流程文件名
     */
    private String processFileName;
    /**
     * 请款流程ID
     */
    private String processId;
    
    /** 请款任务ID **/
    private String taskIds;
    /** 请款ID **/
    private String loanIds;
    /** 处理类型：00-审核驳回，01-审核通过，10-审批驳回，11-审核后通过 **/
    private String doType;
    /** 审核意见 **/
    private String checkResult;
    /** 审批意见 **/
    private String approveResult;
    /** 处理意见或者是原因 **/
    private String reason;
    /**
     * 请款统计查询条件
     */
    private StatisticsVo statistics;
    
    private String fileName;
    private String fileLoc;
    private String downActionName;
    /**
     * 邮件
     */
    private Wiser wiser = new Wiser();
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return CashAdvanceService cashAdvanceService.
     */
    public CashAdvanceService getCashAdvanceService() {
        return cashAdvanceService;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashAdvanceService The cashAdvanceService to set.
     */
    public void setCashAdvanceService(CashAdvanceService cashAdvanceService) {
        this.cashAdvanceService = cashAdvanceService;
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return DataSourceTransactionManager transactionManager.
     */
    public DataSourceTransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param transactionManager The transactionManager to set.
     */
    public void setTransactionManager(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String currentUserId.
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param currentUserId The currentUserId to set.
     */
    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String currentUserName.
     */
    public String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param currentUserName The currentUserName to set.
     */
    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return CashAdvanceInfo cashAdvanceInfo.
     */
    public CashAdvanceInfo getCashAdvanceInfo() {
        return cashAdvanceInfo;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashAdvanceInfo The cashAdvanceInfo to set.
     */
    public void setCashAdvanceInfo(CashAdvanceInfo cashAdvanceInfo) {
        this.cashAdvanceInfo = cashAdvanceInfo;
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

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return CashTaskService cashTaskService.
     */
    public CashTaskService getCashTaskService() {
        return cashTaskService;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashTaskService The cashTaskService to set.
     */
    public void setCashTaskService(CashTaskService cashTaskService) {
        this.cashTaskService = cashTaskService;
    }


    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return JtaTransactionManager springJTM.
     */
    public JtaTransactionManager getSpringJTM() {
        return springJTM;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param springJTM The springJTM to set.
     */
    public void setSpringJTM(JtaTransactionManager springJTM) {
        this.springJTM = springJTM;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String taskIds.
     */
    public String getTaskIds() {
        return taskIds;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param taskIds The taskIds to set.
     */
    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String loanIds.
     */
    public String getLoanIds() {
        return loanIds;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param loanIds The loanIds to set.
     */
    public void setLoanIds(String loanIds) {
        this.loanIds = loanIds;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String doType.
     */
    public String getDoType() {
        return doType;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param doType The doType to set.
     */
    public void setDoType(String doType) {
        this.doType = doType;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String checkResult.
     */
    public String getCheckResult() {
        return checkResult;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param checkResult The checkResult to set.
     */
    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String approveResult.
     */
    public String getApproveResult() {
        return approveResult;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param approveResult The approveResult to set.
     */
    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param reason The reason to set.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return File process.
     */
    public File getProcess() {
        return process;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param process The process to set.
     */
    public void setProcess(File process) {
        this.process = process;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String processContentType.
     */
    public String getProcessContentType() {
        return processContentType;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processContentType The processContentType to set.
     */
    public void setProcessContentType(String processContentType) {
        this.processContentType = processContentType;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String processFileName.
     */
    public String getProcessFileName() {
        return processFileName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processFileName The processFileName to set.
     */
    public void setProcessFileName(String processFileName) {
        this.processFileName = processFileName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String processId.
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processId The processId to set.
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return StatisticsVo statistics.
     */
    public StatisticsVo getStatistics() {
        return statistics;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statistics The statistics to set.
     */
    public void setStatistics(StatisticsVo statistics) {
        this.statistics = statistics;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String fileLoc.
     */
    public String getFileLoc() {
        return fileLoc;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param fileLoc The fileLoc to set.
     */
    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String downActionName.
     */
    public String getDownActionName() {
        return downActionName;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param downActionName The downActionName to set.
     */
    public void setDownActionName(String downActionName) {
        this.downActionName = downActionName;
    }

    /**
     * <p>Discription:[我的请款信息]</p>
     * @return 显示我发起的请款列表
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String myRequest(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        PrintWriter out = null;
        try{
            out = super.getPrintWriter();
            if(cashAdvanceInfo == null || cashAdvanceInfo.getCashUserId() == null || cashAdvanceInfo.getCashUserName() == null
                    || "".equals(cashAdvanceInfo.getCashUserId()) || "".equals(cashAdvanceInfo.getCashUserName())){
                resultMap.put("success", false);
                resultMap.put("msg", "当前用户信息为空，请检查！");
            }else{
                List list = this.cashAdvanceService.getMyRequestCash(cashAdvanceInfo, start, limit);
                Long size = this.cashAdvanceService.getMyRequestCashSize(cashAdvanceInfo);
                resultMap.put("success", true);
                resultMap.put("cashList", list);
                resultMap.put("totalCount", size);
            }
        }catch(Exception e){
            LOG.error(e.getMessage());
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误代码："+e.getMessage());
        }finally{
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[新增请款]</p>
     * @return
     * @author 大牙-小白
     * @update 2012-9-3 大牙-小白 [变更描述]
     */
    public String newRequest(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 定义TransactionDefinition并设置好事务的隔离级别和传播方式。
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(definition);
        PrintWriter out = null;
        try{
            out = super.getPrintWriter();
            if(cashAdvanceInfo == null || cashAdvanceInfo.getCashUserId() == null || cashAdvanceInfo.getCashUserName() == null
                    || "".equals(cashAdvanceInfo.getCashUserId()) || "".equals(cashAdvanceInfo.getCashUserName())){
                resultMap.put("success", false);
                resultMap.put("msg", "当前用户信息为空，请检查！");
            }else if(cashAdvanceInfo.getCashAmount() == null){
                resultMap.put("success", false);
                resultMap.put("msg", "请款金额不能为空！");
            }else{
                ProcessEngine processEngine = Configuration.getProcessEngine();
                RepositoryService repositoryService = processEngine.getRepositoryService();
                //获取指定key=loan的所有流程定义
                Long processDefCount = repositoryService.createProcessDefinitionQuery().processDefinitionKey("loan").count();
                if(processDefCount < 1){
                    resultMap.put("success", false);
                    resultMap.put("msg", "系统未部署任何请款流程，请联系系统管理员部署请款流程！");
                }else{
                    //resultMap = this.cashAdvanceService.addNewRequest(this.springJTM.getUserTransaction(), roleService, jbpmService, cashAdvanceInfo);
                    resultMap = this.cashAdvanceService.addNewRequest(roleService, cashAdvanceInfo);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            LOG.error(e.getMessage());
            status.setRollbackOnly();
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误代码："+e.getMessage());
        }finally{
            if(status.isRollbackOnly()){
                this.transactionManager.rollback(status);
            }else{
                this.transactionManager.commit(status);
            }
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[获取待办请款任务]</p>
     * @return
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public String todoTask(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 定义TransactionDefinition并设置好事务的隔离级别和传播方式。
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(definition);
        PrintWriter out = null;
        try{
            out = super.getPrintWriter();
            if(cashAdvanceInfo == null || cashAdvanceInfo.getCashUserId() == null || cashAdvanceInfo.getCashUserName() == null
                    || "".equals(cashAdvanceInfo.getCashUserId()) || "".equals(cashAdvanceInfo.getCashUserName())){
                resultMap.put("success", false);
                resultMap.put("msg", "当前用户信息为空，请检查！");
            }else{
                //resultMap = this.cashAdvanceService.getTodoRequestCash(this.springJTM.getUserTransaction(), roleService, jbpmService, cashAdvanceInfo, start, limit);
                resultMap = this.cashAdvanceService.getTodoRequestCash(roleService, cashAdvanceInfo, start, limit);
                resultMap.put("success", true);
            }
        }catch(Exception e){
            LOG.error(e.getMessage());
            status.setRollbackOnly();
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误代码："+e.getMessage());
        }finally{
            if(status.isRollbackOnly()){
                this.transactionManager.rollback(status);
            }else{
                this.transactionManager.commit(status);
            }
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[通过或者驳回请款请求]</p>
     * @return
     * @author 大牙-小白
     * @update 2012-9-15 大牙-小白 [变更描述]
     */
    public String doRequestTask(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 定义TransactionDefinition并设置好事务的隔离级别和传播方式。
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(definition);
        PrintWriter out = null;
        try{
            out = super.getPrintWriter();
            if(this.taskIds == null || "".equals(taskIds) || this.loanIds == null || "".equals(loanIds)){
                resultMap.put("success", false);
                resultMap.put("msg", "请选择需要处理的请款请求！");
            }else{
                //this.cashAdvanceService.doRequest(this.springJTM.getUserTransaction(), roleService, jbpmService, taskIds, loanIds, currentUserId, currentUserName, doType, checkResult, approveResult, reason);
                resultMap = this.cashAdvanceService.doRequest(roleService, taskIds, loanIds, currentUserId, currentUserName, doType, checkResult, approveResult, reason);
            }
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            status.setRollbackOnly();
            resultMap.put("success", false);
            resultMap.put("msg", e.getMessage());
        }finally{
            if(status.isRollbackOnly()){
                this.transactionManager.rollback(status);
            }else{
                this.transactionManager.commit(status);
            }
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[修改请款]</p>
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public String editRequest(){
        return newRequest();
    }
    /**
     * <p>Discription:[删除请款]</p>
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public String deleteRequest(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 定义TransactionDefinition并设置好事务的隔离级别和传播方式。
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(definition);
        PrintWriter out = null;
        try{
            out = getPrintWriter();
            if(loanIds == null || "".equals(loanIds.trim())){
                resultMap.put("success", false);
                resultMap.put("msg", "您选择要删除的请款信息为空！");
            }else{
                cashAdvanceService.deleteReuqestCash(loanIds);
                resultMap.put("success", true);
                resultMap.put("msg", "您选择要删除的请款信息已成功删除！");
            }
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            status.setRollbackOnly();
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误原因："+ e.getMessage());
        }finally{
            if(status.isRollbackOnly()){
                this.transactionManager.rollback(status);
            }else{
                this.transactionManager.commit(status);
            }
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[流程列表]</p>
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public String processList(){
        ProcessEngine processEngine = Configuration.getProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取指定key=loan的所有流程定义
        Long allList = repositoryService.createProcessDefinitionQuery().processDefinitionKey("loan").count();
        //分页查询指定key=loan流程定义
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().processDefinitionKey("loan").page(start, limit).list();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<ProcessDefinitionVO> list = new ArrayList<ProcessDefinitionVO>();
        for(ProcessDefinition pd : pdList){
            ProcessDefinitionVO vo = new ProcessDefinitionVO();
            vo.setId(pd.getId());
            vo.setDeploymentId(pd.getDeploymentId());
            vo.setKey(pd.getKey());
            vo.setName(pd.getName());
            vo.setVersion(String.valueOf(pd.getVersion()));
            vo.setDescription(pd.getDescription());
            list.add(vo);
        }
        resultMap.put("success", true);
        resultMap.put("processList", list);
        resultMap.put("totalCount", allList);
        PrintWriter out = null;
        try{
            out = getPrintWriter();
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("msg", e.getMessage());
        }finally{
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[部署流程]</p>
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public String deployLoan(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        PrintWriter out = null;
        try{
            out = getPrintWriter(getRequest(), getResponse(), "utf-8", "text/html; charset=utf-8");
            if(process == null){
                resultMap.put("success", false);
                resultMap.put("msg", "请款流程文件不存在！");
            }else if(processFileName.indexOf("jpdl.xml") < 0){
                resultMap.put("success", false);
                resultMap.put("msg", "请款流程文件类型不正确！");
            }else{
                LOG.info("流程文件类型是：" + this.processContentType);
                //由于文件上传之后变成了*.tmp类似的文件名称，所以只能使用addResourceFromInputStream方法来进行部署
                Configuration.getProcessEngine().getRepositoryService().createDeployment().addResourceFromInputStream(processFileName, new FileInputStream(process)).deploy();
                //启动Email
                //wiser.start();
                resultMap.put("success", true);
                resultMap.put("msg", "请款流程已经部署成功！");
            }
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("msg", "系统异常，异常原因：" + e.getMessage());
        }finally{
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[卸载流程]</p>
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public String unDeployLoan(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(this.processId == null || "".equals(processId.trim())){
            resultMap.put("success", false);
            resultMap.put("msg", "所选请款流程为空！");
        }else{
            PrintWriter out = null;
            try{
                out = getPrintWriter();
                String [] processIds = processId.split(",");
                for(String id : processIds){
                    Configuration.getProcessEngine().getRepositoryService().deleteDeploymentCascade(id);
                }
                resultMap.put("success", true);
                resultMap.put("msg", "所选请款流程已成功卸载！");
            }catch(Exception e){
                resultMap.put("success", false);
                resultMap.put("msg", "系统异常，异常原因：" + e.getMessage());
            }finally{
                if(out != null){
                    out.print(getJsonString(resultMap));
                    out.flush();
                    out.close();
                }
            }
        }
        return null;
    }
    /**
     * <p>Discription:[请款统计]</p>
     * @return
     * @author:大牙
     * @update:2012-11-7
     */
    public String statistics(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        PrintWriter out = null;
        try{
            out = getPrintWriter();
            //我的请款统计
            String userName = super.getRequest().getParameter("userName");
            //我审核的请款统计
            String userByName = getRequest().getParameter("userByName");
            List list = cashAdvanceService.statistics(statistics, userByName, userName);
            resultMap.put("success", true);
            resultMap.put("totalCount", list.size());
            resultMap.put("statisticsList", list);
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误原因："+ e.getMessage());
        }finally{
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
    /**
     * <p>Discription:[下载请款统计]</p>
     * @return
     * @throws IOException
     * @author:大牙
     * @update:2012-11-9
     */
    public String download() throws IOException{
        //我的请款统计
        String userName = super.getRequest().getParameter("userName");
        //我审核的请款统计
        String userByName = getRequest().getParameter("userByName");
        List<StatisticsVo> list = cashAdvanceService.statistics(statistics, userByName, userName);
        String fileName = "";
        try{
            String fileLoc = cashAdvanceService.statisticsInputStream(list, fileName, userByName, userName);
            //BASE64编码，浏览器不支持直接中文
            if(fileLoc != null && !"".equals(fileLoc.trim())){
                setFileLoc(Base64Coder.encodeString(fileLoc));
            }
            setFileName(fileLoc);
            setDownActionName("downloadAction");
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
        }
        if(fileLoc != null && !"".equals(fileLoc.trim())){
            return "download";
        }else{
            PrintWriter out = getPrintWriter(getRequest(), getResponse(), "UTF-8", "text/html; charset=utf-8");
            out.print("<script>alert('当前未查询到任何数据！'); window.close();</script>");
            out.flush();
            out.close();
            return null;
        }
    }
}
