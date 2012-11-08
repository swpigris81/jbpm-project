package com.webservice.loan.vo;

import java.math.BigDecimal;
import java.util.Date;

/** 
 * <p>Description: [请款统计]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class StatisticsVo {
    /**
     * 请款人
     */
    private String statisticsName;
    /**
     * 统计开始日期
     */
    private Date statisticsBeginDate;
    /**
     * 统计结束日期
     */
    private Date statisticsEndDate;
    /**
     * 请款总数
     */
    private BigDecimal statisticsAllLoan;
    /**
     * 已批准请款总数
     */
    private BigDecimal statisticsAllPassLoan;
    /**
     * 正在审批请款总数
     */
    private BigDecimal statisticsCheckingLoan;
    /**
     * 审批驳回请款总数
     */
    private BigDecimal statisticsRejectLoan;
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String statisticsName.
     */
    public String getStatisticsName() {
        return statisticsName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsName The statisticsName to set.
     */
    public void setStatisticsName(String statisticsName) {
        this.statisticsName = statisticsName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Date statisticsBeginDate.
     */
    public Date getStatisticsBeginDate() {
        return statisticsBeginDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsBeginDate The statisticsBeginDate to set.
     */
    public void setStatisticsBeginDate(Date statisticsBeginDate) {
        this.statisticsBeginDate = statisticsBeginDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Date statisticsEndDate.
     */
    public Date getStatisticsEndDate() {
        return statisticsEndDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsEndDate The statisticsEndDate to set.
     */
    public void setStatisticsEndDate(Date statisticsEndDate) {
        this.statisticsEndDate = statisticsEndDate;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return BigDecimal statisticsAllLoan.
     */
    public BigDecimal getStatisticsAllLoan() {
        return statisticsAllLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsAllLoan The statisticsAllLoan to set.
     */
    public void setStatisticsAllLoan(BigDecimal statisticsAllLoan) {
        this.statisticsAllLoan = statisticsAllLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return BigDecimal statisticsAllPassLoan.
     */
    public BigDecimal getStatisticsAllPassLoan() {
        return statisticsAllPassLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsAllPassLoan The statisticsAllPassLoan to set.
     */
    public void setStatisticsAllPassLoan(BigDecimal statisticsAllPassLoan) {
        this.statisticsAllPassLoan = statisticsAllPassLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return BigDecimal statisticsCheckingLoan.
     */
    public BigDecimal getStatisticsCheckingLoan() {
        return statisticsCheckingLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsCheckingLoan The statisticsCheckingLoan to set.
     */
    public void setStatisticsCheckingLoan(BigDecimal statisticsCheckingLoan) {
        this.statisticsCheckingLoan = statisticsCheckingLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return BigDecimal statisticsRejectLoan.
     */
    public BigDecimal getStatisticsRejectLoan() {
        return statisticsRejectLoan;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param statisticsRejectLoan The statisticsRejectLoan to set.
     */
    public void setStatisticsRejectLoan(BigDecimal statisticsRejectLoan) {
        this.statisticsRejectLoan = statisticsRejectLoan;
    }
    
}
