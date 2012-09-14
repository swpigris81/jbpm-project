package com.webservice.loan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;

import com.webservice.jbpm.service.IJbpmService;
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
    
    public Map<String, Object> addNewRequest(UserTransaction userTransaction, 
            IRoleService roleService,  IJbpmService jbpmService, CashAdvanceInfo cashAdvanceInfo) throws Exception{
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            if(Constants.CASH_STATUS_00.equals(cashAdvanceInfo.getCashStatus())){
                //当用户点击暂时保存时的操作
                saveMyRequestCash(cashAdvanceInfo);
                resultMap.put("msg", "请款信息已经保存成功！");
            }else if(Constants.CASH_STATUS_01.equals(cashAdvanceInfo.getCashStatus())){
                userTransaction.begin();
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
                    param.put("checkGroupName", role.getRoleName());
                }
                //设置审批人所在组
                if(role != null){
                    List approveGroup = roleService.findRoleById(role.getParentRoleId());
                    if(approveGroup != null && !approveGroup.isEmpty()){
                        role = (RoleInfo) approveGroup.get(0);
                    }
                    param.put("approveGroupName", role.getRoleName());
                }
                
                //为避免之前已经连接上JBPM的服务，因此先关闭后连接
                try{
                    jbpmService.disconnectJbpmServer();
                }catch(Exception e){
                    log.warn("JBPM服务未连接，请先连接再断开.", e);
                }
                
                //启动流程, 创建一个任务
                Task task = jbpmService.getFirstTask(cashAdvanceInfo.getCashUserName(), param, Constants.PROCESS_LOAN_ID, Constants.PROCESS_LOAN_NAME);
                //将该任务分配给自己
                jbpmService.assignTaskToUser(task.getId().toString(), Constants.ADMINISTRATOR, cashAdvanceInfo.getCashUserName(), Constants.PROCESS_LOAN_NAME);
                //用户得到任务之后，需要开始该任务
                jbpmService.startTask(cashAdvanceInfo.getCashUserName(), null, task.getId().toString(), Constants.PROCESS_LOAN_NAME);
                cashAdvanceInfo.setProcessTaskId(task.getId());
                saveMyRequestCash(cashAdvanceInfo);
                //插入流程变量
                jbpmService.setInstanceVariableForNewTask("cashId", cashAdvanceInfo.getId());
                //记录一下任务-请款流水
                CashTaskInfo info = new CashTaskInfo();
                info.setCashId(cashAdvanceInfo.getId());
                info.setTaskId(task.getId());
                info.setCashStatus(cashAdvanceInfo.getCashStatus());
                this.cashTaskDao.save(info);
                
                //填写完成请款单，完成该任务
                Map<String, Object> contentMap = new HashMap<String, Object>();
                contentMap.put("cashAmount", cashAdvanceInfo.getCashAmount().doubleValue());
                contentMap.put("cashId", cashAdvanceInfo.getId());
                jbpmService.completeTask(cashAdvanceInfo.getCashUserName(), task.getId().toString(), contentMap, Constants.PROCESS_LOAN_NAME);
                
//                //记录一下任务-请款流水
//                info = new CashTaskInfo();
//                info.setCashId(cashAdvanceInfo.getId());
//                info.setTaskId(jbpmService.getTaskId(Constants.PROCESS_LOAN_NAME));
//                info.setCashStatus(cashAdvanceInfo.getCashStatus());
//                cashTaskDao.save(info);
                
                resultMap.put("msg", "请款信息已经发起审核！");
                resultMap.put("success", true);
                //transactionManager.commit();
                userTransaction.commit();
            }
        }catch(Exception e){
            try {
                userTransaction.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
                throw e1;
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getTodoRequestCash(UserTransaction userTransaction, IRoleService roleService, IJbpmService jbpmService,
            CashAdvanceInfo info, int start, int limit) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            userTransaction.begin();
            //检查当前用户的角色
            List<RoleInfo> roles = roleService.findParentRoleByUserId(info.getCashUserName());
            RoleInfo userRole = null;
            if(roles != null && !roles.isEmpty()){
                userRole = roles.get(0);
            }
            List<String> groups = new ArrayList<String>();
            if(userRole != null){
                groups.add(userRole.getRoleName());
            }
            List<TaskSummary> taskList = jbpmService.getAssignedTaskByUserOrGroup(info.getCashUserName(), groups, Constants.PROCESS_LOAN_NAME);
            List<String> cashIdList = new ArrayList<String>();
            Map<String, Long> tempCashTask = new HashMap<String, Long>();
            if(taskList != null){
                for(TaskSummary ts : taskList){
                    String cashId = String.valueOf(jbpmService.getInstanceVariable("cashId", ts.getProcessSessionId(), ts.getProcessInstanceId(), Constants.PROCESS_LOAN_NAME));
                    cashIdList.add(cashId);
                    tempCashTask.put(cashId, ts.getId());
                }
            }
            List<CashAdvanceInfo> infoList = this.cashAdvanceDao.getCashInfoByIds(cashIdList, start, limit);
            List<CashAdvanceInfo> cashInfoList = new ArrayList<CashAdvanceInfo>();
            for(CashAdvanceInfo ci : infoList){
                ci.setProcessTaskId(tempCashTask.get(ci.getId()));
                cashInfoList.add(ci);
            }
            List<CashAdvanceInfo> list = this.cashAdvanceDao.getCashInfoByIds(cashIdList, -1, -1);
            resultMap.put("cashList", cashInfoList);
            resultMap.put("totalCount", list.size());
            userTransaction.commit();
        }catch(Exception e){
            userTransaction.rollback();
            log.error(e.getMessage(), e);
            throw e;
        }
        return resultMap;
    }
}
