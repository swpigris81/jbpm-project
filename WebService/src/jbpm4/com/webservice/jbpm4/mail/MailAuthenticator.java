package com.webservice.jbpm4.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.webservice.system.util.DES;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class MailAuthenticator extends Authenticator{
    private String userName;
    private String password;
    
    public MailAuthenticator(){
        
    }
    
    public MailAuthenticator(String userName, String password){
        this.userName = userName;
        DES des = new DES();
        try{
            this.password = new String(des.createDecryptor(des.stringToByte(password)));
        }catch(Exception e){
            this.password = password;
        }
    }
    
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
