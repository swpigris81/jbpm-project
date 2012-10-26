package com.webservice.loan.bean;

/** 
 * <p>Description: [请款-任务对照]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashTaskInfo {
    /** 主键ID **/
    private String id;
    /** 请款ID **/
    private String cashId;
    /** 任务ID，一个请款可以有多个任务ID(处于多个状态时) **/
    private Long taskId;
    /** 当前请款状态 **/
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
     * @return String cashId.
     */
    public String getCashId() {
        return cashId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param cashId The cashId to set.
     */
    public void setCashId(String cashId) {
        this.cashId = cashId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Long taskId.
     */
    public Long getTaskId() {
        return taskId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param taskId The taskId to set.
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
