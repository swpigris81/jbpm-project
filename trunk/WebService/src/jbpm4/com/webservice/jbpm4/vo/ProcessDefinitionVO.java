package com.webservice.jbpm4.vo;

/** 
 * <p>Description: [流程定义信息]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class ProcessDefinitionVO {
    /**
     * 流程定义ID
     */
    private String id;
    /**
     * 流程部署ID
     */
    private String deploymentId;
    /**
     * 流程KEY
     */
    private String key;
    /**
     * 流程定义名称
     */
    private String name;
    /**
     * 流程定义版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String id.
     */
    public String getId() {
        return id;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String name.
     */
    public String getName() {
        return name;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String version.
     */
    public String getVersion() {
        return version;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String key.
     */
    public String getKey() {
        return key;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String deploymentId.
     */
    public String getDeploymentId() {
        return deploymentId;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param deploymentId The deploymentId to set.
     */
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
}
