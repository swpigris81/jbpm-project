package com.webservice.loan.service.impl;

import java.util.List;

import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.bean.CashTaskInfo;
import com.webservice.loan.dao.CashAdvanceDao;
import com.webservice.loan.dao.CashTaskDao;
import com.webservice.loan.service.CashTaskService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashTaskServiceImpl implements CashTaskService {
    private CashTaskDao cashTaskDao;

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
     * <p>Discription:[新增请款-任务对照]</p>
     * @param info
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public void save(CashTaskInfo info){
        this.cashTaskDao.save(info);
    }
    /**
     * <p>Discription:[条件查询请款-任务对照]</p>
     * @param info
     * @return
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public List findByExample(CashTaskInfo info){
        return this.cashTaskDao.findByExample(info);
    }
}
