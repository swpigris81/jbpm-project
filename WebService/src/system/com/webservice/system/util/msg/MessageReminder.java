package com.webservice.system.util.msg;

import java.util.List;

import com.webservice.system.common.helper.SpringHelper;
import com.webservice.system.message.bean.SystemMessage;
import com.webservice.system.message.service.IMessageService;
import com.webservice.system.util.dwr.MessageSender;
import com.webservice.system.util.quarz.DynamicJobSchedule;

/** 
 * <p>Description: [站内消息提醒]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class MessageReminder {
    private DynamicJobSchedule dynamicJobSchedule;
    private MessageSender dwrMessageSenderService;
    /**
     * <p>Discription:[推送消息]</p>
     * @param message
     * @author:大牙
     * @update:2013-3-14
     */
    public void sendMessageReminder(SystemMessage message){
        dwrMessageSenderService.sendMessageWithPage(message.getMessageTo(), message);
    }
    
    public void sendAllMessageReminder(){
        IMessageService messageService = (IMessageService) SpringHelper.getBean("messageService");
        List<SystemMessage> unReadMessageList = messageService.findByProperty("messageNew", "1");
        //推送所有人的消息
        if(unReadMessageList != null && !unReadMessageList.isEmpty()){
            for(SystemMessage message : unReadMessageList){
                sendMessageReminder(message);
            }
        }
    }
    
    public DynamicJobSchedule getDynamicJobSchedule() {
        return dynamicJobSchedule;
    }
    public void setDynamicJobSchedule(DynamicJobSchedule dynamicJobSchedule) {
        this.dynamicJobSchedule = dynamicJobSchedule;
    }
    public MessageSender getDwrMessageSenderService() {
        return dwrMessageSenderService;
    }
    public void setDwrMessageSenderService(MessageSender dwrMessageSenderService) {
        this.dwrMessageSenderService = dwrMessageSenderService;
    }
}
