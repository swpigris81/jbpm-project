package com.huateng.jbpm.humantask.test;

import com.huateng.jbpm.humantask.HumanTaskClient;
/**
 * <p>Description: [工作流测试类]</p>
 * @author  <a href="mailto: xxx@huateng.com">作者中文名</a>
 * @version $Revision$
 */
public class TestHumanTask {
    public static void main(String[] args) {
        
        HumanTaskClient client = new HumanTaskClient();
        //加载流程定义
        client.start(true);
        //启动流程
        client.startProcess();
        
    }
}
