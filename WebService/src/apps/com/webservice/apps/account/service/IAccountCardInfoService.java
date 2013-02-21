package com.webservice.apps.account.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.webservice.apps.account.bean.AccountBaseInfo;
import com.webservice.apps.account.bean.AccountCardInfo;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">代超</a>
 * @version $Revision$ 
 */
public interface IAccountCardInfoService {
    /**
     * <p>Discription:[保存数据]</p>
     * @param cardInfo
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void save(AccountCardInfo cardInfo);
    
    /**
     * <p>Discription:[保存数据]</p>
     * @param cardInfo
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void update(AccountCardInfo cardInfo);
    /**
     * <p>Discription:[保存或修改]</p>
     * @param cardInfo
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void saveOrUpdate(AccountCardInfo cardInfo);
    /**
     * <p>Discription:[批量保存或修改]</p>
     * @param cardInfos
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void saveOrUpdateAll(Collection<AccountCardInfo> cardInfos);
    /**
     * <p>Discription:[删除数据]</p>
     * @param cardInfos
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void delete(AccountCardInfo cardInfos);
    /**
     * <p>Discription:[批量删除]</p>
     * @param cardInfos
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void deleteAll(Collection<AccountCardInfo> cardInfos);
    /**
     * <p>Discription:[主键查询]</p>
     * @param id
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public AccountCardInfo findById(java.lang.String id);
    /**
     * <p>Discription:[多属性查询]</p>
     * @param instance
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<AccountCardInfo> findByExample(AccountCardInfo instance);
    /**
     * <p>Discription:[单属性查询]</p>
     * @param propertyName
     * @param value
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<AccountCardInfo> findByProperty(String propertyName, Object value);
    /**
     * <p>Discription:[自定义sql查询]</p>
     * @param sql 自定义sql语句
     * @param isSql 是否是标准sql语句
     * @param paramMap 查询参数
     * @param start 分页参数
     * @param limit 分页参数
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List findBySql(String sql, boolean isSql, Map<String, Object> paramMap, int start, int limit);
    /**
     * <p>Discription:[分页查询账户信息(列表)]</p>
     * @param paramMap
     * @param start
     * @param limit
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<AccountCardInfo> findInstanceList(Map<String, Object> paramMap, int start, int limit);
    /**
     * <p>Discription:[分页查询, 查询所有记录数]</p>
     * @param paramMap
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int findInstanceListSize(Map<String, Object> paramMap);
    /**
     * <p>Discription:[账户转账]</p>
     * @param outCard 转出账户
     * @param inCard 转入账户
     * @param amount 转入金额
     * @param comment 备注
     * @author:[代超]
     * @throws Exception 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void transferAccount(AccountCardInfo outCard, AccountCardInfo inCard, double amount, String comment) throws Exception;
    /**
     * <p>Discription:[返还金额给账户]</p>
     * @param baseInfoList 账目列表
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void backAccount(List<AccountBaseInfo> baseInfoList);
}
