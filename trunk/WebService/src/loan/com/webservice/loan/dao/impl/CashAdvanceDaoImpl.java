package com.webservice.loan.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.loan.bean.CashAdvanceInfo;
import com.webservice.loan.dao.CashAdvanceDao;
import com.webservice.loan.vo.StatisticsVo;

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
    public void saveOrUpdate(CashAdvanceInfo info) {
        log.info("begin saveOrUpdate cashAdvanceInfo");
        getHibernateTemplate().saveOrUpdate(info);
        log.info("end saveOrUpdate cashAdvanceInfo");
    }

    @Override
    public void save(CashAdvanceInfo info) {
        log.info("begin save cashAdvanceInfo");
        getHibernateTemplate().save(info);
        log.info("end save cashAdvanceInfo");
    }
    /**
     * <p>Discription:[删除请款信息]</p>
     * @param idArray
     * @author:大牙
     * @update:2012-10-29
     */
    public void deleteReuqestCash(String idArray){
        log.info("begin delete cashAdvanceInfo");
        StringBuffer sb = new StringBuffer("delete from CashAdvanceInfo model where 1=2 ");
        if(idArray != null && !"".equals(idArray.trim())){
            sb.append("or ( model.id in ( ");
            String [] ids = idArray.split(",");
            for(int i=0; i<ids.length; i++){
                if(i == ids.length -1){
                    sb.append(" ? )");
                }else{
                    sb.append(" ?, ");
                }
            }
            sb.append(" ) ");
            super.excuteByHQL(sb.toString(), ids);
        }
        log.info("end delete cashAdvanceInfo");
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
    
    /**
     * <p>Discription:[我的请款统计]</p>
     * @param statistics 统计条件
     * @param userName 我的请款
     * @return 请款集合
     * @author:大牙
     * @update:2012-11-8
     */
    public List myStatistics(StatisticsVo statistics, String userName){
        List list = null;
        List paramList = new ArrayList();
        StringBuffer hql = new StringBuffer("from CashAdvanceInfo model where 1=1");
        if(statistics != null){
            if(statistics.getStatisticsBeginDate() != null && !"".equals(statistics.getStatisticsBeginDate())){
                hql.append(" and model.cashDate >= ? ");
                paramList.add(statistics.getStatisticsBeginDate());
            }
            if(statistics.getStatisticsEndDate() != null && !"".equals(statistics.getStatisticsEndDate())){
                hql.append(" and model.cashDate <= ? ");
                paramList.add(statistics.getStatisticsEndDate());
            }
        }
        if(userName != null && !"".equals(userName.trim())){
            hql.append(" and (model.cashUserId = ? or model.cashUserName like ? ) ");
            paramList.add(userName);
            paramList.add("%" + userName + "%");
        }
        log.info(hql.toString());
        list = super.queryByHQL(hql.toString(), paramList.toArray());
        return list;
    }
    
    /**
     * <p>Discription:[我的请款统计]</p>
     * @param statistics 统计条件
     * @param userByName 我审核的请款
     * @return 请款集合
     * @author:大牙
     * @update:2012-11-8
     */
    public List statisticsByMe(StatisticsVo statistics, String userByName){
        List list = null;
        List paramList = new ArrayList();
        StringBuffer hql = new StringBuffer("from CashAdvanceInfo model where 1=1");
        if(statistics != null){
            if(statistics.getStatisticsBeginDate() != null && !"".equals(statistics.getStatisticsBeginDate())){
                hql.append(" and model.cashDate >= ? ");
                paramList.add(statistics.getStatisticsBeginDate());
            }
            if(statistics.getStatisticsEndDate() != null && !"".equals(statistics.getStatisticsEndDate())){
                hql.append(" and model.cashDate <= ? ");
                paramList.add(statistics.getStatisticsEndDate());
            }
            if(statistics.getStatisticsName() != null && !"".equals(statistics.getStatisticsName().trim())){
                hql.append(" and (model.cashUserId = ? or model.cashUserName like ? )");
                paramList.add(statistics.getStatisticsName());
                paramList.add("%" + statistics.getStatisticsName() + "%");
            }
        }
        if(userByName != null && !"".equals(userByName.trim())){
            hql.append(" and (model.cashCheckUserId = ? or model.cashCheckUserName like ? or model.cashApprovalUserId = ? or model.cashApprovalUserName like ? ) ");
            paramList.add(userByName);
            paramList.add("%" + userByName + "%");
            paramList.add(userByName);
            paramList.add("%" + userByName + "%");
        }
        log.info(hql.toString());
        list = super.queryByHQL(hql.toString(), paramList.toArray());
        return list;
    }
}
