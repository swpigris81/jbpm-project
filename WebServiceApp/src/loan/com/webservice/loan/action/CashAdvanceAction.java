package com.webservice.loan.action;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.webservice.common.action.BaseAction;
import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.service.CashAdvanceService;
import com.webservice.system.common.constants.Constants;
/**
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceAction extends BaseAction {
    private CashAdvanceService cashAdvanceService;
    /** 事务处理 */
    private DataSourceTransactionManager transactionManager;
    /** 当前用户ID **/
    private String currentUserId;
    /** 当前用户 **/
    private String currentUserName;
    /** 请款信息 **/
    private CashAdvanceInfo cashAdvanceInfo;
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
            }else{
                resultMap.put("success", true);
                this.cashAdvanceService.saveMyRequestCash(cashAdvanceInfo);
                if(Constants.CASH_STATUS_00.equals(cashAdvanceInfo.getCashStatus())){
                    resultMap.put("msg", "请款信息已经保存成功！");
                }else if(Constants.CASH_STATUS_01.equals(cashAdvanceInfo.getCashStatus())){
                    resultMap.put("msg", "请款信息已经发起审核！");
                    //启动流程
                    
                }
            }
        }catch(Exception e){
            LOG.error(e.getMessage());
            status.setRollbackOnly();
            resultMap.put("success", false);
            resultMap.put("msg", "系统错误，错误代码："+e.getMessage());
        }finally{
            this.transactionManager.commit(status);
            if(out != null){
                out.print(getJsonString(resultMap));
                out.flush();
                out.close();
            }
        }
        return null;
    }
}
