package com.webservice.system.role.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.webservice.common.dao.IBaseDao;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.dao.IRoleDao;
import com.webservice.system.role.service.IRoleService;

/** 
 * <p>Description: [角色管理]</p>
 * @author  <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate 2011-5-28
 */
public class RoleService implements IRoleService {
    private IRoleDao roleDao;
    private IBaseDao baseDao;

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return IBaseDao baseDao.
     */
    public IBaseDao getBaseDao() {
        return baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param baseDao The baseDao to set.
     */
    public void setBaseDao(IBaseDao baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return IRoleDao roleDao.
     */
    public IRoleDao getRoleDao() {
        return roleDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param roleDao The roleDao to set.
     */
    public void setRoleDao(IRoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List findRoleListByPage(int start, int limit) {
        return this.roleDao.findRoleListByPage(start, limit);
    }

    @Override
    public Long findRoleSize() {long size = 0L;
        String sql = "select count(role_info.role_id) as role_size from role_info";
        List list = this.baseDao.queryBySQL(sql, null);
        if(list!=null){
            size = NumberUtils.toLong((String.valueOf(list.get(0))), 0L);
        }
        return size;
    }

    @Override
    public void deleteAll(Collection entities) {
        this.roleDao.deleteAll(entities);
    }

    @Override
    public void saveOrUpdate(RoleInfo role) {
        this.roleDao.saveOrUpdate(role);
    }
    
    /**
     * <p>Discription:[根据角色名称查询角色信息]</p>
     * @param roleName
     * @return
     * @author: 代超
     * @update: 2011-7-9 代超[变更描述]
     */
    public List findRoleByName(String roleName){
        return this.roleDao.findRoleByName(roleName);
    }
    
    /**
     * <p>Discription:[根据角色名称查询角色信息]</p>
     * @param roleName
     * @return
     * @author: 代超
     * @update: 2011-7-9 代超[变更描述]
     */
    public List findRoleById(String roleId){
        return this.roleDao.findRoleById(roleId);
    }
    
    public List findAll(){
        return this.roleDao.findAll();
    }
    
    /**
     * <p>Discription:[根据用户ID查询用户所在组的上级角色]</p>
     * @param userId
     * @return
     * @author 大牙-小白
     * @update 2012-9-5 大牙-小白 [变更描述]
     */
    public List findParentRoleByUserId(String userId){
        String sql = "select r from RoleInfo r, UserRole s where r.roleId = s.roleId and s.userId = ?";
        List list = this.baseDao.queryByHQL(sql, new Object[]{userId});
        return list;
    }
}
