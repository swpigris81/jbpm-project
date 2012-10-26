package com.webservice.loan.decision.handler;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.jpdl.DecisionHandler;
import org.jbpm.api.model.OpenExecution;
/**
 * <p>Description: [决定是否需要下一步审批]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class LoanHandler implements DecisionHandler{
    private Log log = LogFactory.getLog(LoanHandler.class);
    @Override
    public String decide(OpenExecution execution) {
        Double cashAmount = NumberUtils.toDouble(String.valueOf(execution.getVariable("cashAmount")), 0);
        log.info("请款金额：" + cashAmount);
        if(cashAmount > 1000){
            //请款金额大于1000时需要下一步审批
            return "yes";
        }else{
            //无需再审批
            return "no";
        }
    }
}
