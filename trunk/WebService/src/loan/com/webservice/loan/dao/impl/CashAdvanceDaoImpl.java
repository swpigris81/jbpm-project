package com.webservice.loan.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.dao.CashAdvanceDao;

/** 
 * <p>Description: [请款]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class CashAdvanceDaoImpl extends BaseDao implements CashAdvanceDao {
    private Log log = LogFactory.getLog(CashAdvanceDaoImpl.class);

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

    @Override
    public void save(CashAdvanceInfo info) {
        log.info("begin save cashAdvanceInfo");
        getHibernateTemplate().save(info);
        log.info("end save cashAdvanceInfo");
    }
    
    /**
     * <p>Discription:[更新请款]</p>
     * @param info 请款信息
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void update(CashAdvanceInfo info){
        log.info("begin update cashAdvanceInfo");
        getHibernateTemplate().update(info);
        log.info("end update cashAdvanceInfo");
    }
    /**
     * <p>Discription:[以主键查询]</p>
     * @param id 请款ID
     * @return 请款信息
     * @author 大牙-小白
     * @update 2012-9-15 大牙-小白 [变更描述]
     */
    public CashAdvanceInfo findById(String id){
        return (CashAdvanceInfo)super.findById(CashAdvanceInfo.class, id);
    }
    /**
     * <p>Discription:[批量查询请款信息]</p>
     * @param cashIds 请款ID
     * @return 请款信息
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public List<CashAdvanceInfo> getCashInfoByIds(List<String> cashIds, int start, int limit){
        StringBuffer sql = new StringBuffer("from CashAdvanceInfo model where 1 = 1 ");
        if(cashIds != null && !cashIds.isEmpty()){
            sql.append(" and model.id in ( ");
            for(int i=0,j=cashIds.size(); i< j; i++){
                if(i == j -1){
                    sql.append(" ? ");
                }else{
                    sql.append(" ?, ");
                }
            }
            sql.append(" ) ");
            return super.queryPageByHQL(sql.toString(), cashIds.toArray(), start, limit);
        }
        return super.queryPageByHQL(sql.toString(), new Object[]{}, start, limit);
    }
}
