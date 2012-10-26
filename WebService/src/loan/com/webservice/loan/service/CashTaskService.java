package com.webservice.loan.service;

import java.util.List;

import com.webservice.loan.bean.CashTaskInfo;

public interface CashTaskService {
    /**
     * <p>Discription:[新增请款-任务对照]</p>
     * @param info
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public void save(CashTaskInfo info);
    /**
     * <p>Discription:[条件查询请款-任务对照]</p>
     * @param info
     * @return
     * @author 大牙-小白
     * @update 2012-9-6 大牙-小白 [变更描述]
     */
    public List findByExample(CashTaskInfo info);
}
