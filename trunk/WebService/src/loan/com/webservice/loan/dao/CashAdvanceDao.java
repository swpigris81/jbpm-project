package com.webservice.loan.dao;

import java.util.List;

import com.webservice.loan.bean.CashAdvanceInfo;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public interface CashAdvanceDao {
    /**
     * <p>Discription:[获取指定条件发起的请款信息]</p>
     * @param info 指定条件
     * @param start 分页开始
     * @param limit 每页显示数
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<CashAdvanceInfo> getMyRequestCash(CashAdvanceInfo info, int start, int limit);
    
    /**
     * <p>Discription:[获取指定条件请款数(一般用于分页)]</p>
     * @param info 指定条件
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Long getMyRequestCashSize(CashAdvanceInfo info);
    /**
     * <p>Discription:[新增请款]</p>
     * @param info 请款信息
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void save(CashAdvanceInfo info);
    
    /**
     * <p>Discription:[新增/修改请款]</p>
     * @param info 请款信息
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void saveOrUpdate(CashAdvanceInfo info);
    
    /**
     * <p>Discription:[删除请款信息]</p>
     * @param idArray
     * @author:大牙
     * @update:2012-10-29
     */
    public void deleteReuqestCash(String idArray);
    
    /**
     * <p>Discription:[更新请款]</p>
     * @param info 请款信息
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public void update(CashAdvanceInfo info);
    /**
     * <p>Discription:[以主键查询]</p>
     * @param id 请款ID
     * @return 请款信息
     * @author 大牙-小白
     * @update 2012-9-15 大牙-小白 [变更描述]
     */
    public CashAdvanceInfo findById(String id);
    /**
     * <p>Discription:[批量查询请款信息]</p>
     * @param cashIds 请款ID
     * @return 请款信息
     * @author 大牙-小白
     * @update 2012-9-8 大牙-小白 [变更描述]
     */
    public List<CashAdvanceInfo> getCashInfoByIds(List<String> cashIds, int start, int limit);
}
