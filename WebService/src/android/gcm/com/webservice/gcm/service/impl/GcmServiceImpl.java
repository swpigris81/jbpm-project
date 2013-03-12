package com.webservice.gcm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webservice.common.dao.IBaseDao;
import com.webservice.gcm.bean.GcmModel;
import com.webservice.gcm.dao.GcmDao;
import com.webservice.gcm.service.GcmService;

/** 
 * <p>Description: [GCM服务]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class GcmServiceImpl implements GcmService {
    private Logger logger = Logger.getLogger(GcmServiceImpl.class);
    private GcmDao gcmDao;
    private IBaseDao baseDao;
    
    @Override
    public List findByRegisterId(String regId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("regId", regId);
        String hql = "from com.webservice.gcm.bean.GcmModel where regisId = ?";
        return gcmDao.findByHql(hql, paramMap);
    }
    
    /**
     * <p>Discription:[通过用户名查询用户注册信息]</p>
     * @param userName 用户名
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public List findByUserName(String userName){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userName", userName);
        String hql = "from com.webservice.gcm.bean.GcmModel where userName = ?";
        return gcmDao.findByHql(hql, paramMap);
    }
    
    /**
     * <p>Discription:[注册信息]</p>
     * @param gcm 注册信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void saveGcmRegis(GcmModel gcm){
        gcmDao.saveGcmRegis(gcm);
    }
    
    /**
     * <p>Discription:[用户注销]</p>
     * @param list 注销信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void unRegistion(List list){
        gcmDao.deleteGcmRegis(list);
    }
    
    /**
     * <p>Discription:[查询注册用户信息]</p>
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public List findRegisList(int start, int limit){
        String hql = "from com.webservice.gcm.bean.GcmModel";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("start", start);
        paramMap.put("limit", limit);
        return gcmDao.findByHql(hql, paramMap);
    }

    public GcmDao getGcmDao() {
        return gcmDao;
    }

    public void setGcmDao(GcmDao gcmDao) {
        this.gcmDao = gcmDao;
    }

    public IBaseDao getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(IBaseDao baseDao) {
        this.baseDao = baseDao;
    }
    
    
}
