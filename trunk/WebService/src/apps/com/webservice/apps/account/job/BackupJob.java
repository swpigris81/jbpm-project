package com.webservice.apps.account.job;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webservice.apps.account.bean.AccountBaseInfo;
import com.webservice.apps.account.service.IAccountBaseInfoService;
import com.webservice.system.common.helper.SpringHelper;
import com.webservice.system.systembackup.bean.SystemBackupInfo;
import com.webservice.system.systembackup.service.ISystemBackupService;
import com.webservice.system.util.PoiUtil;
import com.webservice.system.util.Tools;
import com.webservice.system.util.quarz.runjob.RunJob;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class BackupJob extends RunJob {
    private static Log log = LogFactory.getLog(BackupJob.class);
    /**
     * <p>Discription:执行账目信息的备份操作</p>
     * @param jobExecutionContext
     * @throws JobExecutionException
     * @author:大牙
     * @update:2012-11-23
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        log.info("开始自动备份账目信息");
        IAccountBaseInfoService accountBaseInfoService = (IAccountBaseInfoService) SpringHelper.getBean("accountBaseInfoService");
        ISystemBackupService systemBackupService = (ISystemBackupService) SpringHelper.getBean("systemBackupService");
        if(accountBaseInfoService == null){
            throw new RuntimeException("无法获取到名称为：”accountBaseInfoService“的服务！");
        }
        String userName = String.valueOf(jobExecutionContext.getMergedJobDataMap().get("userName"));
        if(userName == null || "null".equalsIgnoreCase(userName.trim()) || "".equals(userName.trim())){
            throw new RuntimeException("无法获取账目所属用户，用户名不能为空！");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userName", userName);
        List list = accountBaseInfoService.queryPage(-1, -1, paramMap);
        PoiUtil<AccountBaseInfo> poiUtil = new PoiUtil<AccountBaseInfo>();
        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("baseinfoid", "唯一编号");
        propertiesMap.put("basedate", "记账日期");
        propertiesMap.put("baseyear", "记账年份");
        propertiesMap.put("basemonth", "记账年月");
        propertiesMap.put("accountenter", "当日收入");
        propertiesMap.put("accountout", "当日支出");
        propertiesMap.put("accountmargin", "当日结算");
        propertiesMap.put("remark", "备注");
        propertiesMap.put("accountcard", "记账账户编号");
        propertiesMap.put("deletetag", "删除标志，0-否；1-是");
        propertiesMap.put("margintag", "结算标志，0-未结算，1-已结算");
        propertiesMap.put("userid", "记账用户ID");
        propertiesMap.put("username", "记账用户名称");
        propertiesMap.put("maintype", "记账消费主类别");
        propertiesMap.put("setype", "记账消费次类别");
        FileOutputStream out = null;
        try{
            File outFile = new File(URLDecoder.decode(this.getClass().getResource("/").getPath() + "账目备份_"+userName+"_"+Tools.dateToString(new Date())+".xlsx", "UTF-8"));
            if(!outFile.exists()){
                outFile.createNewFile();
            }else{
                outFile.delete();
                outFile.createNewFile();
            }
            out = new FileOutputStream(outFile);
            poiUtil.exportExcel2007("账目信息自动备份"+Tools.dateToString(new Date()), propertiesMap, list).write(out);
            log.info("账目信息备份完成, 备份文件所在路径："+ outFile.getAbsolutePath());
            SystemBackupInfo backupInfo = new SystemBackupInfo();
            backupInfo.setBackDate(new Date());
            backupInfo.setBackType("accountBanlance");
            backupInfo.setFileName(outFile.getName());
            backupInfo.setFilePath(outFile.getAbsolutePath());
            backupInfo.setUserName(userName);
            backupInfo.setMemo("账目信息自动备份");
            systemBackupService.saveOrUpdate(backupInfo);
        }catch(Exception e){
            log.error("账目信息备份异常，" + e.getMessage());
            throw new RuntimeException(e);
        }finally{
            if(out != null){
                try{
                    out.flush();
                    out.close();
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
