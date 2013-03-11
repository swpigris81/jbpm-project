package com.webservice.system.user.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.system.user.bean.UserInfo;
import com.webservice.system.user.dao.IUserDao;
import com.webservice.system.user.service.IUserService;
import com.webservice.system.util.ProtectUserInfo;

/** 
 * <p>Description: [用户管理]</p>
 * @author  <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate 2011-6-11
 */
public class UserService implements IUserService {
    private IUserDao userDao;
    private BaseDao baseDao;

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return BaseDao baseDao.
     */
    public BaseDao getBaseDao() {
        return baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param baseDao The baseDao to set.
     */
    public void setBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return IUserDao userDao.
     */
    public IUserDao getUserDao() {
        return userDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param userDao The userDao to set.
     */
    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List findUserByPageCondition(String sql,int start, int limit, Object [] params) {
        return this.userDao.findUserByPage(sql, start, limit, params);
    }
    
    public Long findUserSize(){
        long size = 0L;
        String sql = "select count(employee_info.operater_id) as user_size from employee_info";
        List list = this.baseDao.queryBySQL(sql, null);
        if(list!=null){
            size = NumberUtils.toLong((String.valueOf(list.get(0))), 0L);
        }
        return size;
    }

    @Override
    public List findUserByPageWithProtect(int start, int limit) {
        List<UserInfo> userList = this.userDao.findUserByPage(null, start, limit, null);
        return ProtectUserInfo.protectUserInfo(userList);
    }
    
    public void saveOrUpdate(UserInfo entity){
        this.userDao.saveOrUpdate(entity);
    }
    
    /**
     * 根据用户ID查询用户信息
     * @param id
     * @return
     */
    public UserInfo findById(String id){
        return this.userDao.findById(id);
    }
    
    public void deleteAll(Collection entities){
        this.userDao.deleteAll(entities);
    }
    
    /**
     * <p>Discription:[根据用户名查询用户信息]</p>
     * @param userName
     * @return
     * @author: 代超
     * @update: 2011-6-11 代超[变更描述]
     */
    public List getUserByName(String userName){
        return this.userDao.getUserByName(userName);
    }
    
    public List getUserByRole(String roleId){
        String sql = "SELECT model.operater_id from supplier_role model where model.role_id = ?";
        return this.baseDao.queryBySQL(sql, new Object[]{roleId});
    }
    
    public List findAll(){
        return this.userDao.findAll();
    }
    
    /**
     * <p>Discription:[根据设备号查询]</p>
     * @param imei 设备号
     * @return 用户信息
     * @author:大牙
     * @update:2013-3-11
     */
    public List findByImei(String imei){
        return this.userDao.findByProperties("phoneImei", imei);
    }
    
    /**
     * <p>Discription:[查询所有绑定设备信息的用户]</p>
     * @param start 分页起始页
     * @param limit 分页数
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public List findImeiUserList(int start, int limit){
        String hql = "from UserInfo where phoneImei is not null";
        return userDao.findUserByPage(hql, start, limit, null);
    }
    /**
     * <p>Discription:[查询所有绑定设备信息的用户数]</p>
     * @return
     * @author:大牙
     * @update:2013-3-11
     */
    public int findCountImeiUser(){
        String sql = "select count(employee_info.operater_id) as user_size from employee_info where phone_imei is not null";
        List list = this.baseDao.queryBySQL(sql, null);
        int size = 0;
        if(list!=null){
            size = NumberUtils.toInt((String.valueOf(list.get(0))), 0);
        }
        return size;
    }
}
