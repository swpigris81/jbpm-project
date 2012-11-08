package com.webservice.loan.service;

import java.util.List;
import java.util.Map;

import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.vo.StatisticsVo;
import com.webservice.system.role.service.IRoleService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public interface CashAdvanceService {
    /**
     * <p>Discription:[获取指定条件发起的请款信息]</p>
     * @param info 指定条件
     * @param start 分页开始
     * @param limit 每页显示数
     * @return 请款列表
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<CashAdvanceInfo> getMyRequestCash(CashAdvanceInfo info, int start, int limit);
    /**
     * <p>Discription:[获取本人的待办请款任务(已翻页)]</p>
     * @param userTransaction 流程事务
     * @param jbpmService 流程服务
     * @param roleService 角色服务
     * @param info 本人信息
     * @param start 分页起始数
     * @param limit 每页显示数
     * @return 请款列表（{cashList : infoList, totalCount : count}）
     * @author 大牙-小白
     * @throws Exception 
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public Map<String, Object> getTodoRequestCash(IRoleService roleService, CashAdvanceInfo info, int start, int limit) throws Exception;
    //public Map<String, Object> getTodoRequestCash(UserTransaction userTransaction, IRoleService roleService, IJbpmService jbpmService, CashAdvanceInfo info, int start, int limit) throws Exception;
    
    /**
     * <p>Discription:[获取指定条件请款数(一般用于分页)]</p>
     * @param info 指定条件
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Long getMyRequestCashSize(CashAdvanceInfo info);
    /**
     * <p>Discription:[新增请款]</p>
     * @param info
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void saveMyRequestCash(CashAdvanceInfo info);
    /**
     * <p>Discription:[删除请款信息]</p>
     * @param idArray
     * @author:大牙
     * @update:2012-10-29
     */
    public void deleteReuqestCash(String idArray);
    /**
     * <p>Discription:[新增请款]</p>
     * @param userTransaction 事务
     * @param roleService 角色服务
     * @param jbpmService 流程服务
     * @param cashAdvanceInfo 请款信息
     * @return
     * @throws Exception
     * @author 大牙-小白
     * @update 2012-9-7 大牙-小白 [变更描述]
     */
    public Map<String, Object> addNewRequest(IRoleService roleService, CashAdvanceInfo cashAdvanceInfo) throws Exception;
//    public Map<String, Object> addNewRequest(UserTransaction userTransaction, 
//            IRoleService roleService,  IJbpmService jbpmService, CashAdvanceInfo cashAdvanceInfo) throws Exception;
    /**
     * <p>Discription:[处理请款请求]</p>
     * @param userTransaction 事务
     * @param roleService 角色服务
     * @param jbpmService 流程服务
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
    public Map<String, Object> doRequest(IRoleService roleService,  String taskIds, String loanIds, String userId, String userName, String doType, String checkResult, String approveResult, String reason) throws Exception;

//    public Map<String, Object> doRequest(UserTransaction userTransaction, 
//            IRoleService roleService,  IJbpmService jbpmService, String taskIds, String loanIds, String userId, String userName, String doType, String checkResult, String approveResult, String reason) throws Exception;
    /**
     * <p>Discription:[请款统计]</p>
     * @param statistics 请款统计查询条件
     * @param userByName 我审核的请款统计
     * @param userName 我的请款统计
     * @return 请款统计信息，分月显示
     * @author:大牙
     * @update:2012-11-8
     */
    public List statistics(StatisticsVo statistics, String userByName, String userName);
}
