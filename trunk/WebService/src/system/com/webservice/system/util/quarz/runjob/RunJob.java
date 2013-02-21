package com.webservice.system.util.quarz.runjob;

import org.apache.commons.beanutils.BeanUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webservice.apps.account.bean.AccountBaseInfo;
import com.webservice.system.util.quarz.manager.QuartzManager;

/** 
 * <p>Description: 定时器执行类</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public abstract class RunJob implements Job{
    public static void main(String[] args) {
        //QuartzManager.addJob("test", RunJob.class, "00 40 11 ? * FRI *");
        //QuartzManager.startJobs();
        AccountBaseInfo instance = new AccountBaseInfo();
        try{
            System.out.println(BeanUtils.describe(instance));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //执行JOB
    public abstract void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}
