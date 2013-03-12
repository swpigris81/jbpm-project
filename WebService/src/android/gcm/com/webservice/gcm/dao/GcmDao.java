package com.webservice.gcm.dao;

import java.util.List;
import java.util.Map;

import com.webservice.gcm.bean.GcmModel;

/** 
 * <p>Description: [GCM数据]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public interface GcmDao {
    /**
     * <p>Discription:[自定义HQL查询]</p>
     * @param hql hql查询语句
     * @param paramMap 查询参数
     * @return
     * @author:大牙
     * @update:2013-3-12
     */
    public List findByHql(String hql, Map<String, Object> paramMap);
    /**
     * <p>Discription:[用户注册设备]</p>
     * @param gcm 注册信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void saveGcmRegis(GcmModel gcm);
    /**
     * <p>Discription:[用户注销]</p>
     * @param list 注销信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void deleteGcmRegis(List list);
}
