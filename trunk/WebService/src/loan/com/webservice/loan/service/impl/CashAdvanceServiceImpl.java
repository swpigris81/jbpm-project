package com.webservice.loan.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;

import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.bean.CashTaskInfo;
import com.webservice.loan.dao.CashAdvanceDao;
import com.webservice.loan.dao.CashTaskDao;
import com.webservice.loan.service.CashAdvanceService;
import com.webservice.system.common.constants.Constants;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.service.IRoleService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceServiceImpl implements CashAdvanceService {
    private Log log = LogFactory.getLog(CashAdvanceServiceImpl.class);
    private CashAdvanceDao cashAdvanceDao;
    private CashTaskDao cashTaskDao;

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return CashAdvanceDao cashAdvanceDao.
     */
    public CashAdvanceDao getCashAdvanceDao() {
        return cashAdvanceDao;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashAdvanceDao The cashAdvanceDao to set.
     */
    public void setCashAdvanceDao(CashAdvanceDao cashAdvanceDao) {
        this.cashAdvanceDao = cashAdvanceDao;
    }
    
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return CashTaskDao cashTaskDao.
     */
    public CashTaskDao getCashTaskDao() {
        return cashTaskDao;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashTaskDao The cashTaskDao to set.
     */
    public void setCashTaskDao(CashTaskDao cashTaskDao) {
        this.cashTaskDao = cashTaskDao;
    }

    /**
     * <p>Discription:[获取指定条件发起的请款信息]</p>
     * @param info 指定条件
     * @param start 分页开始
     * @param limit 每页显示数
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<CashAdvanceInfo> getMyRequestCash(CashAdvanceInfo info, int start, int limit){
        return cashAdvanceDao.getMyRequestCash(info, start, limit);
    }
    /**
     * <p>Discription:[获取指定条件请款数(一般用于分页)]</p>
     * @param info 指定条件
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Long getMyRequestCashSize(CashAdvanceInfo info){
        return cashAdvanceDao.getMyRequestCashSize(info);
    }
    /**
     * <p>Discription:[新增请款]</p>
     * @param info 请款信息
     * @author 大牙-小白 
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void saveMyRequestCash(CashAdvanceInfo info){
        cashAdvanceDao.save(info);
    }
    public Map<String, Object> addNewRequest(IRoleService roleService, CashAdvanceInfo cashAdvanceInfo) throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            if(Constants.CASH_STATUS_00.equals(cashAdvanceInfo.getCashStatus())){
                //当用户点击暂时保存时的操作
                saveMyRequestCash(cashAdvanceInfo);
                resultMap.put("msg", "请款信息已经保存成功！");
            }else if(Constants.CASH_STATUS_01.equals(cashAdvanceInfo.getCashStatus())){
                //当用户点击提交时的操作
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("cashAmount", cashAdvanceInfo.getCashAmount().doubleValue());
                //设置填写请款单的用户
                param.put("userId", cashAdvanceInfo.getCashUserId());
                param.put("userName", cashAdvanceInfo.getCashUserName());
          
                //设置审核人所在组
                List group = roleService.findParentRoleByUserId(cashAdvanceInfo.getCashUserName());
                RoleInfo role = null;
                if(group != null && !group.isEmpty()){
                    role = (RoleInfo) group.get(0);
                    List checkGroup = roleService.findRoleById(role.getParentRoleId());
                    if(checkGroup != null && !checkGroup.isEmpty()){
                        role = (RoleInfo) checkGroup.get(0);
                    }
                    param.put("checkGroup", role.getRoleName());
                }
                //设置审批人所在组
                if(role != null){
                    List approveGroup = roleService.findRoleById(role.getParentRoleId());
                    if(approveGroup != null && !approveGroup.isEmpty()){
                        role = (RoleInfo) approveGroup.get(0);
                    }
                    param.put("approveGroup", role.getRoleName());
                }
                ProcessEngine processEngine = Configuration.getProcessEngine();
                ExecutionService executionService = processEngine.getExecutionService();
                TaskService taskService = processEngine.getTaskService();
                //启动新的流程实例（查找指定KEY的最新部署版本的流程实例）
                ProcessInstance processInstance = executionService.startProcessInstanceByKey("loan", param);
                String pid = processInstance.getId();
                List<Task> taskList = taskService.findPersonalTasks(cashAdvanceInfo.getCashUserName());
                if(taskList != null && !taskList.isEmpty()){
                    cashAdvanceInfo.setProcessTaskId(NumberUtils.toLong(taskList.get(0).getId()));
                }
                //保存请款信息
                saveMyRequestCash(cashAdvanceInfo);
                //记录流水
                CashTaskInfo info = new CashTaskInfo();
                info.setCashId(cashAdvanceInfo.getId());
                info.setTaskId(NumberUtils.toLong(taskList.get(0).getId()));
                info.setCashStatus(cashAdvanceInfo.getCashStatus());
                this.cashTaskDao.save(info);
                //完成发起请款任务
                Map<String, Object> completeParam = new HashMap<String, Object>();
                completeParam.put("cashId", cashAdvanceInfo.getId());
                taskService.setVariables(taskList.get(0).getId(), completeParam);
                taskService.completeTask(taskList.get(0).getId());
                resultMap.put("msg", "请款信息已经发起审核！");
                resultMap.put("success", true);
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw e;
        }
        return resultMap;
    }
//    public Map<String, Object> addNewRequest(UserTransaction userTransaction, 
//            IRoleService roleService,  IJbpmService jbpmService, CashAdvanceInfo cashAdvanceInfo) throws Exception{
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        try{
//            if(Constants.CASH_STATUS_00.equals(cashAdvanceInfo.getCashStatus())){
//                //当用户点击暂时保存时的操作
//                saveMyRequestCash(cashAdvanceInfo);
//                resultMap.put("msg", "请款信息已经保存成功！");
//            }else if(Constants.CASH_STATUS_01.equals(cashAdvanceInfo.getCashStatus())){
//                userTransaction.begin();
//                //当用户点击提交时的操作
//                Map<String, Object> param = new HashMap<String, Object>();
//                param.put("cashAmount", cashAdvanceInfo.getCashAmount().doubleValue());
//                //设置填写请款单的用户
//                param.put("userId", cashAdvanceInfo.getCashUserId());
//                param.put("userName", cashAdvanceInfo.getCashUserName());
//                
//                //设置审核人所在组
//                List group = roleService.findParentRoleByUserId(cashAdvanceInfo.getCashUserName());
//                RoleInfo role = null;
//                if(group != null && !group.isEmpty()){
//                    role = (RoleInfo) group.get(0);
//                    List checkGroup = roleService.findRoleById(role.getParentRoleId());
//                    if(checkGroup != null && !checkGroup.isEmpty()){
//                        role = (RoleInfo) checkGroup.get(0);
//                    }
//                    param.put("checkGroupName", role.getRoleName());
//                }
//                //设置审批人所在组
//                if(role != null){
//                    List approveGroup = roleService.findRoleById(role.getParentRoleId());
//                    if(approveGroup != null && !approveGroup.isEmpty()){
//                        role = (RoleInfo) approveGroup.get(0);
//                    }
//                    param.put("approveGroupName", role.getRoleName());
//                }
//                
//                //为避免之前已经连接上JBPM的服务，因此先关闭后连接
////                try{
////                    jbpmService.disconnectJbpmServer();
////                }catch(Exception e){
////                    log.warn("JBPM服务未连接，请先连接再断开.", e);
////                }
//                
//                //启动流程, 创建一个任务
//                Task task = jbpmService.getFirstTask(cashAdvanceInfo.getCashUserName(), param, Constants.PROCESS_LOAN_ID, Constants.PROCESS_LOAN_NAME);
//                //将该任务分配给自己
//                jbpmService.assignTaskToUser(task.getId().toString(), Constants.ADMINISTRATOR, cashAdvanceInfo.getCashUserName(), Constants.PROCESS_LOAN_NAME);
//                //用户得到任务之后，需要开始该任务
//                jbpmService.startTask(cashAdvanceInfo.getCashUserName(), null, task.getId().toString(), Constants.PROCESS_LOAN_NAME);
//                cashAdvanceInfo.setProcessTaskId(task.getId());
//                saveMyRequestCash(cashAdvanceInfo);
//                //插入流程变量
//                jbpmService.setInstanceVariableForNewTask("cashId", cashAdvanceInfo.getId());
//                //记录一下任务-请款流水
//                CashTaskInfo info = new CashTaskInfo();
//                info.setCashId(cashAdvanceInfo.getId());
//                info.setTaskId(task.getId());
//                info.setCashStatus(cashAdvanceInfo.getCashStatus());
//                this.cashTaskDao.save(info);
//                
//                //填写完成请款单，完成该任务
//                Map<String, Object> contentMap = new HashMap<String, Object>();
//                contentMap.put("cashAmount", cashAdvanceInfo.getCashAmount().doubleValue());
//                contentMap.put("cashId", cashAdvanceInfo.getId());
//                jbpmService.completeTask(cashAdvanceInfo.getCashUserName(), task.getId().toString(), contentMap, Constants.PROCESS_LOAN_NAME);
//                
////                //记录一下任务-请款流水
////                info = new CashTaskInfo();
////                info.setCashId(cashAdvanceInfo.getId());
////                info.setTaskId(jbpmService.getTaskId(Constants.PROCESS_LOAN_NAME));
////                info.setCashStatus(cashAdvanceInfo.getCashStatus());
////                cashTaskDao.save(info);
//                
//                resultMap.put("msg", "请款信息已经发起审核！");
//                resultMap.put("success", true);
//                //transactionManager.commit();
//                userTransaction.commit();
//            }
//        }catch(Exception e){
//            try {
//                userTransaction.rollback();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                throw e1;
//            }
//        }
//        return resultMap;
//    }
    /**
     * <p>Discription:[获取本人的待办请款任务(已翻页)]</p>
     * @param roleService 角色服务
     * @param info 本人信息
     * @param start 分页起始数
     * @param limit 每页显示数
     * @return 请款列表（{cashList : infoList, totalCount : count}）
     * @author 大牙-小白
     * @throws Exception 
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public Map<String, Object> getTodoRequestCash(IRoleService roleService, CashAdvanceInfo info, int start, int limit) throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            ProcessEngine processEngine = Configuration.getProcessEngine();
            TaskService taskService = processEngine.getTaskService();
            //获取当前用户所在组的所有任务(参数是当前用户名，而不是当前用户所在组)
            List<Task> taskList = taskService.findGroupTasks(info.getCashUserName());
            //获取直接分配给我的所有任务
            List<Task> taskAssignList = taskService.findPersonalTasks(info.getCashUserName());
            if(taskAssignList != null && !taskAssignList.isEmpty()){
                if(taskList != null && !taskList.isEmpty()){
                    taskList.addAll(taskAssignList);
                }else{
                    taskList = taskAssignList;
                }
            }
            List<String> cashIdList = new ArrayList<String>();
            Map<String, String> tempCashTask = new HashMap<String, String>();
            if(taskList != null && !taskList.isEmpty()){
                for(Task task : taskList){
                    String cashId = String.valueOf(taskService.getVariable(task.getId(), "cashId"));
                    cashIdList.add(cashId);
                    tempCashTask.put(cashId, task.getId());
                }
                List<CashAdvanceInfo> infoList = this.cashAdvanceDao.getCashInfoByIds(cashIdList, start, limit);
                List<CashAdvanceInfo> cashInfoList = new ArrayList<CashAdvanceInfo>(infoList.size());
                for(CashAdvanceInfo ci : infoList){
                    ci.setProcessTaskId(NumberUtils.toLong(tempCashTask.get(ci.getId())));
                    cashInfoList.add(ci);
                }
                List<CashAdvanceInfo> list = this.cashAdvanceDao.getCashInfoByIds(cashIdList, -1, -1);
                resultMap.put("cashList", cashInfoList);
                resultMap.put("totalCount", list.size());
            }else{
                resultMap.put("cashList", new ArrayList());
                resultMap.put("totalCount", 0);
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw e;
        }
        return resultMap;
    }
//    public Map<String, Object> getTodoRequestCash(UserTransaction userTransaction, IRoleService roleService, IJbpmService jbpmService,
//            CashAdvanceInfo info, int start, int limit) throws Exception {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        try{
//            userTransaction.begin();
//            //检查当前用户的角色
//            List<RoleInfo> roles = roleService.findParentRoleByUserId(info.getCashUserName());
//            RoleInfo userRole = null;
//            if(roles != null && !roles.isEmpty()){
//                userRole = roles.get(0);
//            }
//            List<String> groups = new ArrayList<String>();
//            if(userRole != null){
//                groups.add(userRole.getRoleName());
//            }
//            List<TaskSummary> taskList = jbpmService.getAssignedTaskByUserOrGroup(info.getCashUserName(), groups, Constants.PROCESS_LOAN_NAME);
//            List<String> cashIdList = new ArrayList<String>();
//            Map<String, Long> tempCashTask = new HashMap<String, Long>();
//            if(taskList != null && !taskList.isEmpty()){
//                for(TaskSummary ts : taskList){
//                    String cashId = String.valueOf(jbpmService.getInstanceVariable("cashId", ts.getProcessSessionId(), ts.getProcessInstanceId(), Constants.PROCESS_LOAN_NAME));
//                    cashIdList.add(cashId);
//                    tempCashTask.put(cashId, ts.getId());
//                }
//                List<CashAdvanceInfo> infoList = this.cashAdvanceDao.getCashInfoByIds(cashIdList, start, limit);
//                List<CashAdvanceInfo> cashInfoList = new ArrayList<CashAdvanceInfo>();
//                for(CashAdvanceInfo ci : infoList){
//                    ci.setProcessTaskId(tempCashTask.get(ci.getId()));
//                    cashInfoList.add(ci);
//                }
//                List<CashAdvanceInfo> list = this.cashAdvanceDao.getCashInfoByIds(cashIdList, -1, -1);
//                resultMap.put("cashList", cashInfoList);
//                resultMap.put("totalCount", list.size());
//            }else{
//                resultMap.put("cashList", new ArrayList());
//                resultMap.put("totalCount", 0);
//            }
//            userTransaction.commit();
//        }catch(Exception e){
//            userTransaction.rollback();
//            log.error(e.getMessage(), e);
//            throw e;
//        }
//        return resultMap;
//    }

    /**
     * <p>Discription:[处理请款请求]</p>
     * @param roleService 角色服务
     * @param taskIds 请款任务ID
     * @param loanIds 请款ID
     * @param userId 处理人ID
     * @param userName 处理人名称
     * @param doType 处理类型，00-审核驳回，01-审核通过，10-审批驳回，11-审核后通过
     * @param checkResult 审核意见
     * @param approveResult 审批意见
     * @param reason 意见或者是原因
     * @return 处理结果{success:true,msg:"处理通过"}
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-15 大牙-小白 [变更描述]
     */
    public Map<String, Object> doRequest(IRoleService roleService,  String taskIds, String loanIds, String userId, String userName, String doType, String checkResult, String approveResult, String reason) throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            if(taskIds != null && !"".equals(taskIds.trim()) && loanIds!= null && !"".equals(loanIds.trim())){
                String []taskArray = taskIds.split(",");
                String []loanArray = loanIds.split(",");
                ProcessEngine processEngine = Configuration.getProcessEngine();
                TaskService taskService = processEngine.getTaskService();
                String result = "";
                if(Constants.CHECK_BAK.equals(doType)){
                    result = "审核驳回";
                }else if(Constants.CHECK_PASS.equals(doType)){
                    result = "审核通过";
                }else if(Constants.APPROVE_BAK.equals(doType)){
                    result = "审批驳回";
                }else if(Constants.APPROVE_PASS.equals(doType)){
                    result = "审批通过";
                }
                for(int i=0; i<taskArray.length; i++){
                    String taskId = taskArray[i];
                    String loanId = loanArray[i];
                    //将该任务分配给自己
                    //taskService.takeTask(taskId, userName);
                    taskService.assignTask(taskId, userName);
                    Map<String, Object> contentMap = new HashMap<String, Object>();
                    CashAdvanceInfo info = cashAdvanceDao.findById(loanId);
                    
                    if(Constants.CHECK_BAK.equals(doType)){
                        contentMap.put("cashCheckResult", "0");
                        info.setCashCheckResult("0");
                        info.setCashCheckDate(new Date());
                        info.setCashStatus(Constants.CASH_STATUS_03);
                        info.setCashCheckRemark(reason);
                        info.setCashCheckUserId(userId);
                        info.setCashCheckUserName(userName);
                    }else if(Constants.CHECK_PASS.equals(doType)){
                        contentMap.put("cashCheckResult", "1");
                        info.setCashCheckResult("1");
                        info.setCashCheckDate(new Date());
                        info.setCashStatus(Constants.CASH_STATUS_02);
                        info.setCashCheckRemark(reason);
                        info.setCashCheckUserId(userId);
                        info.setCashCheckUserName(userName);
                    }else if(Constants.APPROVE_BAK.equals(doType)){
                        contentMap.put("cashApprovalResult", "0");
                        info.setCashApprovalResult("0");
                        info.setCashApprovalDate(new Date());
                        info.setCashStatus(Constants.CASH_STATUS_06);
                        info.setCashApprovalRemark(reason);
                        info.setCashApprovalUserId(userId);
                        info.setCashApprovalUserName(userName);
                    }else if(Constants.APPROVE_PASS.equals(doType)){
                        contentMap.put("cashApprovalResult", "1");
                        info.setCashApprovalResult("1");
                        info.setCashApprovalDate(new Date());
                        info.setCashStatus(Constants.CASH_STATUS_05);
                        info.setCashApprovalRemark(reason);
                        info.setCashApprovalUserId(userId);
                        info.setCashApprovalUserName(userName);
                    }else if(Constants.APPLY_AGAIN.equals(doType)){
                        //再次发起请求
                        info.setCashCheckResult(null);
                        info.setCashApprovalResult(null);
                        info.setCashStatus(Constants.CASH_STATUS_01);
                        info.setCashApprovalUserId(null);
                        info.setCashApprovalUserName(null);
                        info.setCashCheckUserId(null);
                        info.setCashCheckUserName(null);
                        info.setCashCheckDate(null);
                        info.setCashApprovalDate(null);
                        info.setCashReason(reason);
                        contentMap.put("cashAmount", info.getCashAmount().doubleValue());
                        contentMap.put("cashId", info.getId());
                    }
                    //设置变量
                    taskService.setVariables(taskId, contentMap);
                    //完成审核/审批任务
                    if(Constants.APPLY_AGAIN.equals(doType)){
                        taskService.completeTask(taskId);
                    }else{
                        taskService.completeTask(taskId, result);
                    }
                    cashAdvanceDao.update(info);
                    //记录一下任务-请款流水
                    CashTaskInfo taskInfo = new CashTaskInfo();
                    taskInfo.setCashId(loanId);
                    taskInfo.setTaskId(NumberUtils.toLong(taskId));
                    taskInfo.setCashStatus(info.getCashStatus());
                    this.cashTaskDao.save(taskInfo);
                }
                resultMap.put("success", true);
                resultMap.put("msg", "所选待办任务已成功处理！");
            }else{
                resultMap.put("success", false);
                resultMap.put("msg", "所选请款任务为空！");
            }
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw e;
        }
        return resultMap;
    }
//    public Map<String, Object> doRequest(UserTransaction userTransaction,
//            IRoleService roleService, IJbpmService jbpmService, String taskIds,
//            String loanIds, String userId, String userName, String doType, String checkResult, String approveResult, String reason)
//            throws Exception {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        try{
//            userTransaction.begin();
//            if(taskIds != null && !"".equals(taskIds.trim()) && loanIds!= null && !"".equals(loanIds.trim())){
//                String []taskArray = taskIds.split(",");
//                String []loanArray = loanIds.split(",");
//                for(int i=0; i<taskArray.length; i++){
//                    String taskId = taskArray[i];
//                    String loanId = loanArray[i];
//                    //检查当前用户的角色
//                    List<RoleInfo> roles = roleService.findParentRoleByUserId(userName);
//                    RoleInfo userRole = null;
//                    if(roles != null && !roles.isEmpty()){
//                        userRole = roles.get(0);
//                    }
//                    List<String> groups = new ArrayList<String>();
//                    if(userRole != null){
//                        groups.add(userRole.getRoleName());
//                    }
//                    //将该任务分配给自己
//                    jbpmService.assignTaskToUser(taskId, Constants.ADMINISTRATOR, userName, Constants.PROCESS_LOAN_NAME);
//                    jbpmService.startTask(userName, groups, taskId, Constants.PROCESS_LOAN_NAME);
//                    Map<String, Object> contentMap = new HashMap<String, Object>();
//                    
//                    CashAdvanceInfo info = cashAdvanceDao.findById(loanId);
//                    info.setCashCheckDate(new Date());
//                    
//                    if("00".equals(doType)){
//                        contentMap.put("cashCheckResult", "0");
//                        info.setCashCheckResult("0");
//                        info.setCashStatus(Constants.CASH_STATUS_03);
//                        info.setCashCheckRemark(reason);
//                        info.setCashCheckUserId(userId);
//                        info.setCashCheckUserName(userName);
//                    }else if("01".equals(doType)){
//                        contentMap.put("cashCheckResult", "1");
//                        info.setCashCheckResult("1");
//                        info.setCashStatus(Constants.CASH_STATUS_02);
//                        info.setCashCheckRemark(reason);
//                        info.setCashCheckUserId(userId);
//                        info.setCashCheckUserName(userName);
//                    }else if("10".equals(doType)){
//                        contentMap.put("cashApprovalResult", "0");
//                        info.setCashApprovalResult("0");
//                        info.setCashStatus(Constants.CASH_STATUS_06);
//                        info.setCashApprovalRemark(reason);
//                        info.setCashApprovalUserId(userId);
//                        info.setCashApprovalUserName(userName);
//                    }else if("11".equals(doType)){
//                        contentMap.put("cashApprovalResult", "1");
//                        info.setCashApprovalResult("1");
//                        info.setCashStatus(Constants.CASH_STATUS_05);
//                        info.setCashApprovalRemark(reason);
//                        info.setCashApprovalUserId(userId);
//                        info.setCashApprovalUserName(userName);
//                    }else if("99".equals(doType)){
//                        //再次发起请求
//                        info.setCashCheckResult(null);
//                        info.setCashApprovalResult(null);
//                        info.setCashStatus(Constants.CASH_STATUS_01);
//                        info.setCashReason(reason);
//                        contentMap.put("cashAmount", info.getCashAmount().doubleValue());
//                        contentMap.put("cashId", info.getId());
//                    }
//                    jbpmService.completeTask(userName, taskId, contentMap, Constants.PROCESS_LOAN_NAME);
//                    cashAdvanceDao.update(info);
//                    //记录一下任务-请款流水
//                    CashTaskInfo taskInfo = new CashTaskInfo();
//                    taskInfo.setCashId(loanId);
//                    taskInfo.setTaskId(NumberUtils.toLong(taskId));
//                    taskInfo.setCashStatus(info.getCashStatus());
//                    this.cashTaskDao.save(taskInfo);
//                }
//            }else{
//                resultMap.put("success", false);
//                resultMap.put("msg", "所选请款任务为空！");
//            }
//        }catch(Exception e){
//            userTransaction.rollback();
//            log.error(e.getMessage(), e);
//            throw e;
//        }
//        return resultMap;
//    }
}
