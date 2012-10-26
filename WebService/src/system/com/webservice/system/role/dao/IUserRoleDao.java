package com.webservice.system.role.dao;

import java.util.Collection;
import java.util.List;

import com.webservice.system.role.bean.UserRole;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate May 18, 2011
 */
public interface IUserRoleDao {
    /**
     * <p>Discription:[根据用户ID查询用户角色]</p>
     * @param userId
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List findRoleByUserID(String userId);
    
    public List findRoleByUserIdName(String userId);
    
    /**
     * 新增用户角色信息
     * @param entities
     */
    public void saveOrUpdateAll(Collection entities);
    
    /**
     * 新增用户角色信息
     * @param entities
     */
    public void saveOrUpdate(UserRole userRole);
    
    /**
     * <p>Discription:[查询所有用户角色]</p>
     * @return
     * @author:大牙
     * @update:2012-10-25
     */
    public List findAll();
}
