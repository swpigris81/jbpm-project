package com.webservice.loan.bean;

import java.math.BigDecimal;
import java.util.Date;

/** 
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceInfo {
    private String id;
    /** 请款卡号 **/
    private String cardId;
    /** 请款金额 **/
    private BigDecimal cashAmount;
    /** 请款日期 **/
    private Date cashDate;
    /** 请款原因 **/
    private String cashReason;
    /** 备注 **/
    private String cashRemark;
    /** 请款人 **/
    private String cashUserId;
    /** 请款人 **/
    private String cashUserName;
    /** 审核人 **/
    private String cashCheckUserId;
    /** 审核人 **/
    private String cashCheckUserName;
    /** 审核时间 **/
    private Date cashCheckDate;
    /** 审核结果 0-驳回，1-通过 **/
    private String cashCheckResult;
    /** 审批人 **/
    private String cashApprovalUserId;
    /** 审批人 **/
    private String cashApprovalUserName;
    /** 审批时间 **/
    private Date cashApprovalDate;
    /** 审批结果 0-驳回，1-通过 **/
    private String cashApprovalResult;
    /** 流程任务ID **/
    private Long processTaskId;
    /** 当前请款状态（00-申请请款，01-发起审核，02-审核通过，03-审核驳回，04-发起审批，05-审批通过，06-审批驳回） **/
    private String cashStatus;
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String id.
     */
    public String getId() {
        return id;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cardId.
     */
    public String getCardId() {
        return cardId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cardId The cardId to set.
     */
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return BigDecimal cashAmount.
     */
    public BigDecimal getCashAmount() {
        return cashAmount;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashAmount The cashAmount to set.
     */
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Date cashDate.
     */
    public Date getCashDate() {
        return cashDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashDate The cashDate to set.
     */
    public void setCashDate(Date cashDate) {
        this.cashDate = cashDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashReason.
     */
    public String getCashReason() {
        return cashReason;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashReason The cashReason to set.
     */
    public void setCashReason(String cashReason) {
        this.cashReason = cashReason;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashRemark.
     */
    public String getCashRemark() {
        return cashRemark;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashRemark The cashRemark to set.
     */
    public void setCashRemark(String cashRemark) {
        this.cashRemark = cashRemark;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashUserId.
     */
    public String getCashUserId() {
        return cashUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashUserId The cashUserId to set.
     */
    public void setCashUserId(String cashUserId) {
        this.cashUserId = cashUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashUserName.
     */
    public String getCashUserName() {
        return cashUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashUserName The cashUserName to set.
     */
    public void setCashUserName(String cashUserName) {
        this.cashUserName = cashUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashCheckUserId.
     */
    public String getCashCheckUserId() {
        return cashCheckUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashCheckUserId The cashCheckUserId to set.
     */
    public void setCashCheckUserId(String cashCheckUserId) {
        this.cashCheckUserId = cashCheckUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashCheckUserName.
     */
    public String getCashCheckUserName() {
        return cashCheckUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashCheckUserName The cashCheckUserName to set.
     */
    public void setCashCheckUserName(String cashCheckUserName) {
        this.cashCheckUserName = cashCheckUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Date cashCheckDate.
     */
    public Date getCashCheckDate() {
        return cashCheckDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashCheckDate The cashCheckDate to set.
     */
    public void setCashCheckDate(Date cashCheckDate) {
        this.cashCheckDate = cashCheckDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashCheckResult.
     */
    public String getCashCheckResult() {
        return cashCheckResult;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashCheckResult The cashCheckResult to set.
     */
    public void setCashCheckResult(String cashCheckResult) {
        this.cashCheckResult = cashCheckResult;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashApprovalUserId.
     */
    public String getCashApprovalUserId() {
        return cashApprovalUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashApprovalUserId The cashApprovalUserId to set.
     */
    public void setCashApprovalUserId(String cashApprovalUserId) {
        this.cashApprovalUserId = cashApprovalUserId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashApprovalUserName.
     */
    public String getCashApprovalUserName() {
        return cashApprovalUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashApprovalUserName The cashApprovalUserName to set.
     */
    public void setCashApprovalUserName(String cashApprovalUserName) {
        this.cashApprovalUserName = cashApprovalUserName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Date cashApprovalDate.
     */
    public Date getCashApprovalDate() {
        return cashApprovalDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashApprovalDate The cashApprovalDate to set.
     */
    public void setCashApprovalDate(Date cashApprovalDate) {
        this.cashApprovalDate = cashApprovalDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashApprovalResult.
     */
    public String getCashApprovalResult() {
        return cashApprovalResult;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashApprovalResult The cashApprovalResult to set.
     */
    public void setCashApprovalResult(String cashApprovalResult) {
        this.cashApprovalResult = cashApprovalResult;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Long processTaskId.
     */
    public Long getProcessTaskId() {
        return processTaskId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param processTaskId The processTaskId to set.
     */
    public void setProcessTaskId(Long processTaskId) {
        this.processTaskId = processTaskId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String cashStatus.
     */
    public String getCashStatus() {
        return cashStatus;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashStatus The cashStatus to set.
     */
    public void setCashStatus(String cashStatus) {
        this.cashStatus = cashStatus;
    }
}
