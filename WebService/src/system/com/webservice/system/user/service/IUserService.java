package com.webservice.system.user.service;

import java.util.Collection;
import java.util.List;

import com.webservice.system.user.bean.UserInfo;

/** 
 * <p>Description: [用户管理]</p>
 * @author  <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate 2011-6-11
 */
public interface IUserService {
    /**
     * <p>Discription:[用户列表，分页自定义sql条件查询]</p>
     * @param start
     * @param limit
     * @return
     * @author: 代超
     * @update: 2011-6-11 代超[变更描述]
     */
    public List findUserByPageCondition(String sql, int start, int limit, Object [] params);
    /**
     * <p>Discription:[查询所有用户的数量]</p>
     * @return
     * @author: 代超
     * @update: 2011-6-11 代超[变更描述]
     */
    public Long findUserSize();
    /**
     * <p>Discription:[查询用户列表，被保护的信息]</p>
     * @param start
     * @param limit
     * @return
     * @author: 代超
     * @update: 2011-6-11 代超[变更描述]
     */
    public List findUserByPageWithProtect(int start, int limit);
    /**
     * 新增或修改用户信息
     * @param entity
     */
    public void saveOrUpdate(UserInfo entity);
    
    /**
     * 根据用户ID查询用户信息
     * @param id
     * @return
     */
    public UserInfo findById(String id);
    /**
     * 批量删除用户信息
     * @param entities
     */
    public void deleteAll(Collection entities);
    
    /**
     * <p>Discription:[根据用户名查询用户信息]</p>
     * @param userName
     * @return
     * @author: 代超
     * @update: 2011-6-11 代超[变更描述]
     */
    public List getUserByName(String userName);
    /**
     * <p>Discription:[查询某角色的所有用户]</p>
     * @param roleId
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List getUserByRole(String roleId);
    /**
     * <p>Discription:[查询所有用户]</p>
     * @return 所有用户列表
     * @author 大牙-小白
     * @update 2012-9-5 大牙-小白 [变更描述]
     */
    public List findAll();
    /**
     * <p>Discription:[根据设备号查询]</p>
     * @param imei 设备号
     * @return 用户信息
     * @author:大牙
     * @update:2013-3-11
     */
    public List findByImei(String imei);
    /**
     * <p>Discription:[查询所有绑定设备信息的用户]</p>
     * @param start 分页起始页
     * @param limit 分页数
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public List findImeiUserList(int start, int limit);
    /**
     * <p>Discription:[查询所有绑定设备信息的用户数]</p>
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public int findCountImeiUser();
}
