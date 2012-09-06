package com.webservice.system.role.bean;

/**
 * <p>
 * Description: [角色PO]
 * </p>
 * 
 * @author <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate May 17, 2011
 */
public class RoleInfo {
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String parentRoleId.
     */
    public String getParentRoleId() {
        return parentRoleId;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param parentRoleId The parentRoleId to set.
     */
    public void setParentRoleId(String parentRoleId) {
        this.parentRoleId = parentRoleId;
    }

    private String roleId;
    private String roleName;
    private String parentRoleId;
    private String comment;
}
