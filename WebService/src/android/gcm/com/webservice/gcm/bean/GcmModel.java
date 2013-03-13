package com.webservice.gcm.bean;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class GcmModel {
    private Long id;
    private String userName;
    private String regisId;
    private String androidAlias;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getRegisId() {
        return regisId;
    }
    public void setRegisId(String regisId) {
        this.regisId = regisId;
    }
    public String getAndroidAlias() {
        return androidAlias;
    }
    public void setAndroidAlias(String androidAlias) {
        this.androidAlias = androidAlias;
    }
    
    
}
