package com.webservice.loan.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.webservice.loan.vo.StatisticsVo;
import com.webservice.system.common.constants.Constants;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.service.IRoleService;
import com.webservice.system.util.PoiUtil;
import com.webservice.system.util.Tools;

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
        cashAdvanceDao.saveOrUpdate(info);
    }
    /**
     * <p>Discription:[删除请款信息]</p>
     * @param idArray
     * @author:大牙
     * @update:2012-10-29
     */
    public void deleteReuqestCash(String idArray){
        if(idArray == null || "".equals(idArray.trim())){
            return;
        }
        cashAdvanceDao.deleteReuqestCash(idArray);
    }
    
    public Map<String, Object> addNewRequest(IRoleService roleService, CashAdvanceInfo cashAdvanceInfo) throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            if(cashAdvanceInfo.getId() == null || "".equals(cashAdvanceInfo.getId())){
                cashAdvanceInfo.setId(null);
            }
            if(Constants.CASH_STATUS_00.equals(cashAdvanceInfo.getCashStatus())){
                //当用户点击暂时保存时的操作
                saveMyRequestCash(cashAdvanceInfo);
                resultMap.put("success", true);
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
    
    /**
     * <p>Discription:[请款统计]</p>
     * @param statistics 请款统计查询条件
     * @param userByName 我审核的请款统计
     * @param userName 我的请款统计
     * @return 请款统计信息，分月显示
     * @author:大牙
     * @update:2012-11-8
     */
    public List statistics(StatisticsVo statistics, String userByName, String userName){
        return makeStatistics(queryLoanList(statistics, userByName, userName));
    }
    /**
     * <p>Discription:[查询指定条件的请款信息]</p>
     * @param statistics 查询条件
     * @param userByName 审批人
     * @param userName 请款人
     * @return 请款信息
     * @author:大牙
     * @update:2012-11-9
     */
    public List queryLoanList(StatisticsVo statistics, String userByName, String userName){
        List statisticsList = new ArrayList();
        if(userByName == null || "".equals(userByName.trim())){
            //查询我的统计，请款人是userName
            if(userName != null && !"".equals(userName.trim())){
                statisticsList = cashAdvanceDao.myStatistics(statistics, userName);
            }
        }else if(userName == null || "".equals(userName.trim())){
            //查询我审核的请款统计
            statisticsList = cashAdvanceDao.statisticsByMe(statistics, userByName);
        }
        return statisticsList;
    }
    
    /**
     * <p>Discription:[组装统计信息]</p>
     * @param statisticsList 请款信息
     * @return 统计信息
     * @author:大牙
     * @update:2012-11-8
     */
    public List<StatisticsVo> makeStatistics(List<CashAdvanceInfo> statisticsList){
        List<StatisticsVo> statisticList = new ArrayList<StatisticsVo>();
        if(statisticsList != null && !statisticsList.isEmpty()){
            //使用年月来分别存放请款信息以便统计
            Map<Integer, ArrayList<CashAdvanceInfo>> map = new HashMap<Integer, ArrayList<CashAdvanceInfo>>();
            for(CashAdvanceInfo info : statisticsList){
                ArrayList<CashAdvanceInfo> tempList = null;
                if(map.get(Tools.getMonthFromDate(info.getCashDate())) != null){
                    //如果已经存在相同月份的请款信息
                    tempList = map.get(Tools.getMonthFromDate(info.getCashDate()));
                }else{
                    //如果不存在相同月份的请款信息
                    tempList = new ArrayList<CashAdvanceInfo>();
                }
                tempList.add(info);
                map.put(Tools.getMonthFromDate(info.getCashDate()), tempList);
            }
            if(map != null && !map.isEmpty()){
                Set<Integer> keySet = map.keySet();
                for(int key : keySet){
                    //统计请款总数
                    BigDecimal statisticsAllLoan = new BigDecimal("0");
                    //已批准请款总数
                    BigDecimal statisticsAllPassLoan = new BigDecimal("0");
                    //正在审批请款总数
                    BigDecimal statisticsCheckingLoan = new BigDecimal("0");
                    //审批驳回请款总数
                    BigDecimal statisticsRejectLoan = new BigDecimal("0");
                    ArrayList<CashAdvanceInfo> list = map.get(key);
                    StatisticsVo vo = makeLoanInfo(list, statisticsAllLoan, statisticsAllPassLoan,
                            statisticsCheckingLoan, statisticsRejectLoan);
                    statisticList.add(vo);
                }
            }
        }
        return statisticList;
    }
    /**
     * <p>Discription:[统计]</p>
     * @param list 要统计的数据
     * @param statisticsAllLoan 统计所有请款
     * @param statisticsAllPassLoan 统计所有审批通过的请款
     * @param statisticsCheckingLoan 统计正在审批的请款
     * @param statisticsRejectLoan 统计审核驳回的请款
     * @author:大牙
     * @update:2012-11-8
     */
    public StatisticsVo makeLoanInfo(ArrayList<CashAdvanceInfo> list,
            BigDecimal statisticsAllLoan, BigDecimal statisticsAllPassLoan,
            BigDecimal statisticsCheckingLoan, BigDecimal statisticsRejectLoan) {
        StatisticsVo vo = new StatisticsVo();
        if(list != null && !list.isEmpty()){
            Date[] date = new Date[list.size()];
            int i=0;
            for(CashAdvanceInfo info : list){
                date[i] = info.getCashDate();
                statisticsAllLoan = statisticsAllLoan.add(info.getCashAmount());
                //需更高级审批时
                if(info.getCashAmount().compareTo(new BigDecimal(Constants.DEFAULT_LOAN_AMOUNT)) == 1){
                    if (Constants.CASH_STATUS_05.equals(info.getCashStatus())) {
                        // 审批通过
                        statisticsAllPassLoan = statisticsAllPassLoan.add(info.getCashAmount());
                    } else if (Constants.CASH_STATUS_03.equals(info.getCashStatus())
                            || Constants.CASH_STATUS_06.equals(info.getCashStatus())) {
                        // 审核或者是审批驳回
                        statisticsRejectLoan = statisticsRejectLoan.add(info.getCashAmount());
                    } else if (Constants.CASH_STATUS_01.equals(info.getCashStatus())
                            || Constants.CASH_STATUS_02.equals(info.getCashStatus())
                            || Constants.CASH_STATUS_04.equals(info.getCashStatus())) {
                        // 发起审核(正在审核)
                        statisticsCheckingLoan = statisticsCheckingLoan.add(info.getCashAmount());
                    }
                }else{
                    if(Constants.CASH_STATUS_02.equals(info.getCashStatus())){
                        //审核通过
                        statisticsAllPassLoan = statisticsAllPassLoan.add(info.getCashAmount());
                    }else if(Constants.CASH_STATUS_03.equals(info.getCashStatus())){
                        //审核驳回
                        statisticsRejectLoan = statisticsRejectLoan.add(info.getCashAmount());
                    }else if(Constants.CASH_STATUS_01.equals(info.getCashStatus())){
                        //发起审核(正在审核)
                        statisticsCheckingLoan = statisticsCheckingLoan.add(info.getCashAmount());
                    }
                }
                i ++ ;
            }
            vo.setStatisticsAllLoan(statisticsAllLoan);
            vo.setStatisticsAllPassLoan(statisticsAllPassLoan);
            vo.setStatisticsCheckingLoan(statisticsCheckingLoan);
            vo.setStatisticsRejectLoan(statisticsRejectLoan);
            vo.setStatisticsName(list.get(0).getCashUserName());
            vo.setStatisticsBeginDate(Tools.getMinDate(date));
            vo.setStatisticsEndDate(Tools.getMaxDate(date));
            vo.setLoanList(list);
        }
        return vo;
    }
    
    /**
     * <p>Discription:[将统计信息写入报表文件]</p>
     * @param dataList 要写入的数据
     * @param fileName 要写入的报表名称
     * @param userByName 我审核的请款统计
     * @param userName 我的请款统计
     * @return 报表文件流
     * @author:大牙
     * @throws Exception 
     * @update:2012-11-9
     */
    public String statisticsInputStream(List<StatisticsVo> dataList, String fileName, String userByName, String userName) throws Exception{
        if(dataList == null || dataList.isEmpty()){
            return null;
        }
        if(userByName == null || "".equals(userByName.trim())){
            //查询我的统计，请款人是userName
            if(userName != null && !"".equals(userName.trim())){
                fileName = userName + "的请款统计";
            }
        }else if(userName == null || "".equals(userName.trim())){
            //查询我审核的请款统计
            fileName = userByName + "审批的请款统计";
        }
        PoiUtil poiUtil = new PoiUtil();
        Workbook wb = poiUtil.createWorkBook(1000);
        Sheet sheet = poiUtil.createWorkSheet(wb, fileName);
        Sheet detailSheet = poiUtil.createWorkSheet(wb, fileName + "明细");
        int i=1;
        int j=1;
        FileOutputStream out = null;
        File outFile = new File(fileName + ".xlsx");
        try{
            out = new FileOutputStream(outFile);
            poiUtil.exportExcel2007(sheet, 0, new Object[]{"请款人姓名", "统计请款开始日期", "统计请款结束日期", "请款总额", "已批准总额", "审批中总额", "审批驳回总额"});
            for(StatisticsVo vo : dataList){
                List data = new ArrayList();
                data.add(vo.getStatisticsName());
                data.add(Tools.dateToString(vo.getStatisticsBeginDate()));
                data.add(Tools.dateToString(vo.getStatisticsEndDate()));
                data.add(vo.getStatisticsAllLoan());
                data.add(vo.getStatisticsAllPassLoan());
                data.add(vo.getStatisticsCheckingLoan());
                data.add(vo.getStatisticsRejectLoan());
                List<CashAdvanceInfo> cl = vo.getLoanList();
                poiUtil.exportExcel2007(sheet, i, data.toArray());
                i++;
                poiUtil.exportExcel2007(detailSheet, 0, new Object[]{"请款人姓名", "请款日期", "请款金额", "请款状态"});
                for(CashAdvanceInfo info : cl){
                    data = new ArrayList();
                    data.add(info.getCashUserName());
                    data.add(Tools.dateToString(info.getCashDate()));
                    data.add(info.getCashAmount());
                    switch (NumberUtils.toInt(info.getCashStatus(), 0)) {
                        case 0: {
                            data.add("申请请款");
                        }
                            break;
                        case 1: {
                            data.add("发起审核");
                        }
                            break;
                        case 2: {
                            data.add("审核通过");
                        }
                            break;
                        case 3: {
                            data.add("审核驳回");
                        }
                            break;
                        case 4: {
                            data.add("发起审批");
                        }
                            break;
                        case 5: {
                            data.add("审批通过");
                        }
                            break;
                        case 6: {
                            data.add("审批驳回");
                        }
                            break;
                        default :{
                            data.add("状态未知");
                        }
                            break;
                    }
                    poiUtil.exportExcel2007(detailSheet, j, data.toArray());
                    j++;
                }
            }
            wb.write(out);
        }catch(Exception e){
            throw e;
        }finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return outFile.getName();
    }
}
