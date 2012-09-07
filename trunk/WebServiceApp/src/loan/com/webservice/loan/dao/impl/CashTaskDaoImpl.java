package com.webservice.loan.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.loan.bean.CashTaskInfo;
import com.webservice.loan.dao.CashTaskDao;

/** 
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashTaskDaoImpl extends BaseDao implements CashTaskDao {
    private Log log = LogFactory.getLog(CashTaskDaoImpl.class);

    @Override
    public void save(CashTaskInfo info) {
        log.info("begin save CashTaskInfo");
        getHibernateTemplate().save(info);
        log.info("end save CashTaskInfo");
    }
    
    public List findByExample(CashTaskInfo info){
        log.info("find by CashTaskInfo");
        return super.findByExample(info, -1, -1);
    }
}
