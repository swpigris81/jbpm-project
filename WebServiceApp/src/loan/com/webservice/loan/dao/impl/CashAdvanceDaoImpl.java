package com.webservice.loan.dao.impl;

import java.util.List;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.dao.CashAdvanceDao;

/** 
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceDaoImpl extends BaseDao implements CashAdvanceDao {

    @Override
    public List<CashAdvanceInfo> getMyRequestCash(CashAdvanceInfo info, int start, int limit) {
        return super.findByExample(info, start, limit);
    }

    @Override
    public Long getMyRequestCashSize(CashAdvanceInfo info) {
        List list = super.findByExample(info, -1, -1);
        if(list != null && !list.isEmpty()){
            return (Integer.valueOf(list.size())).longValue();
        }
        return 0L;
    }

}
