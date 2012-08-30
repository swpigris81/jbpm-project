package com.webservice.loan.service.impl;

import java.util.List;

import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.dao.CashAdvanceDao;
import com.webservice.loan.service.CashAdvanceService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceServiceImpl implements CashAdvanceService {
    private CashAdvanceDao cashAdvanceDao;

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
}
