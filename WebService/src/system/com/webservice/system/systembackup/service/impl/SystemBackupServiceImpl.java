package com.webservice.system.systembackup.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.webservice.apps.account.job.BackupJob;
import com.webservice.common.dao.impl.BaseDao;
import com.webservice.system.systembackup.bean.SystemBackupInfo;
import com.webservice.system.systembackup.dao.ISystemBackupDao;
import com.webservice.system.systembackup.service.ISystemBackupService;
import com.webservice.system.util.quarz.manager.QuartzManager;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">代超</a>
 * @version $Revision$ 
 */
public class SystemBackupServiceImpl implements ISystemBackupService {
    private BaseDao baseDao;
    private ISystemBackupDao backDao;
    
    @Override
    public void saveOrUpdateAll(Collection<SystemBackupInfo> instance) {
        this.backDao.saveOrUpdateAll(instance);
    }
    @Override
    public void saveOrUpdate(SystemBackupInfo instance) {
        this.backDao.saveOrUpdate(instance);
    }
    @Override
    public List findByExample(SystemBackupInfo instance) {
        return this.backDao.findByExample(instance);
    }
    @Override
    public SystemBackupInfo findById(String id) {
        return this.backDao.findById(id);
    }
    @Override
    public void deleteAll(Collection<SystemBackupInfo> persistentInstance) {
        this.backDao.deleteAll(persistentInstance);
    }
    @Override
    public void delete(SystemBackupInfo persistentInstance) {
        this.backDao.delete(persistentInstance);
    }
    @Override
    public void save(SystemBackupInfo transientInstance) {
        this.backDao.save(transientInstance);
    }
    @Override
    public List queryPageBackList(int start, int limit, Map<String, Object> paramMap) {
        StringBuffer hql = new StringBuffer("from SystemBackupInfo model where 1 = 1 ");
        if(paramMap != null && !paramMap.isEmpty()){
            for(String key : paramMap.keySet()){
                if(paramMap.get(key) == null || "".equals(paramMap.get(key).toString().trim())){
                    continue;
                }
                hql.append(" and model.").append(key).append(" = :").append(key).append(" ");
            }
        }
        return this.baseDao.queryPageByHQL(hql.toString(), paramMap, start, limit);
    }
    @Override
    public int queryPageBackList(Map<String, Object> paramMap) {
        List list = queryPageBackList(-1, -1, paramMap);
        if(list != null && !list.isEmpty()){
            return list.size();
        }
        return 0;
    }
    
    /**
     * <p>Discription:添加/修改系统备份定时任务</p>
     * @param jobDataMap 任务参数
     * @param jobName 任务名称
     * @param cronTime 任务执行时间
     * @author:大牙
     * @update:2012-11-23
     */
    public void executeJob(Map jobDataMap, String jobName, String cronTime){
        if(QuartzManager.getJobDetail(jobName) != null){
            QuartzManager.removeJob(jobName);
        }
        QuartzManager.addJob(jobDataMap, jobName, BackupJob.class, cronTime);
        QuartzManager.startJobs();
    }
    
    public BaseDao getBaseDao() {
        return baseDao;
    }
    public void setBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }
    public ISystemBackupDao getBackDao() {
        return backDao;
    }
    public void setBackDao(ISystemBackupDao backDao) {
        this.backDao = backDao;
    }
}
