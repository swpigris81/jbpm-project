package com.webservice.loan.service.impl;

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
    
    
}
