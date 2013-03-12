package com.webservice.gcm.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.webservice.common.dao.impl.BaseDao;
import com.webservice.gcm.bean.GcmModel;
import com.webservice.gcm.dao.GcmDao;

/** 
 * <p>Description: [GCM数据]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class GcmDaoImpl extends BaseDao implements GcmDao {
    private Logger logger = Logger.getLogger(GcmDaoImpl.class);
    /**
     * <p>Discription:[自定义Hql查询]</p>
     * @param hql 查询语句
     * @param paramMap 参数
     * @return 查询结果
     * @author:大牙
     * @update:2013-3-12
     */
    public List findByHql(String hql, Map<String, Object> paramMap) {
        logger.info("hibernate : " + hql);
        if(paramMap != null && !paramMap.isEmpty()){
            Object[] paramArray = null;
            if(paramMap.containsKey("start")){
                int start = NumberUtils.toInt(String.valueOf(paramMap.get("start")));
                int limit = NumberUtils.toInt(String.valueOf(paramMap.get("limit")));
                paramMap.remove("start");
                paramMap.remove("limit");
                paramArray = paramMap.values().toArray();
                return super.queryPageByHQL(hql, paramArray, start, limit);
            }else{
                paramArray = paramMap.values().toArray();
                return super.queryByHQL(hql, paramArray);
            }
        }else{
            return super.queryByHQL(hql, null);
        }
    }
    
    /**
     * <p>Discription:[用户注册设备]</p>
     * @param gcm 注册信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void saveGcmRegis(GcmModel gcm){
        getHibernateTemplate().save(gcm);
    }
    
    /**
     * <p>Discription:[用户注销]</p>
     * @param list 注销信息
     * @author:大牙
     * @update:2013-3-12
     */
    public void deleteGcmRegis(List list){
        getHibernateTemplate().deleteAll(list);
    }
}
