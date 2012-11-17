package com.webservice.system.right.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.webservice.common.dao.IBaseDao;
import com.webservice.system.menu.bean.ButtonInfo;
import com.webservice.system.right.dao.IRightDao;
import com.webservice.system.right.service.IRightService;

/** 
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">Chao Dai</a>
 * @createDate 2011-6-5
 */
public class RightService implements IRightService {
    private IRightDao rightDao;
    private IBaseDao baseDao;

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return IBaseDao baseDao.
     */
    public IBaseDao getBaseDao() {
        return baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param baseDao The baseDao to set.
     */
    public void setBaseDao(IBaseDao baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @return IRightDao rightDao.
     */
    public IRightDao getRightDao() {
        return rightDao;
    }

    /**
     * <p>Discription:[方法功能描述]</p>
     * @param rightDao The rightDao to set.
     */
    public void setRightDao(IRightDao rightDao) {
        this.rightDao = rightDao;
    }
    
    public List<String> getButtonRoleNameByButton(String buttonId){
        //String sql = "SELECT role_info.role_name FROM menubutton , right_info , role_info WHERE menubutton.button_id =  right_info.button_id AND right_info.role_id =  role_info.role_id AND menubutton.menu_id =  right_info.menu_id AND menubutton.button_id =  ? ";
        String sql = "SELECT role_info.role_id FROM menubutton , right_info , role_info WHERE menubutton.button_id =  right_info.button_id AND right_info.role_id =  role_info.role_id AND menubutton.button_id =  ? ";
        String []params = new String[]{buttonId};
        return this.baseDao.queryBySQL(sql, params);
    }
    
    /**
     * <p>Discription:[根据按钮ID查询能访问该按钮的角色信息]</p>
     * @param buttonIds 按钮id
     * @return 按钮ID, 角色id(以,分隔的角色ID), 按钮url
     * @author:大牙
     * @update:2012-11-12
     */
    public List getButtonRoleByButtonIds(Object ... buttonIds){
        StringBuffer sql = new StringBuffer();
        if(buttonIds != null && buttonIds.length > 0){
            sql.append("SELECT menubutton.button_id, GROUP_CONCAT(role_info.role_id), menubutton.button_url ");
            sql.append("FROM menubutton, right_info, role_info ");
            sql.append("WHERE menubutton.button_id = right_info.button_id AND right_info.role_id = role_info.role_id ");
            sql.append("AND menubutton.button_id IN( ");
            for(int i=0; i<buttonIds.length; i++){
                if(i == buttonIds.length - 1){
                    sql.append(" ? )");
                }else{
                    sql.append(" ?, ");
                }
            }
            sql.append(" GROUP BY menubutton.button_id");
            return this.baseDao.queryBySQL(sql.toString(), buttonIds);
        }else{
            return null;
        }
    }

    @Override
    public List getButtonByRight(String menuId, String roleId) {
        List buttons = new ArrayList();
        List buttonRight = this.rightDao.getButtonByRight(menuId, roleId);
        if(buttonRight !=null){
            for(int i=0,j = buttonRight.size();i<j;i++){
                Object[] obj = (Object[]) buttonRight.get(i);
                ButtonInfo button = (ButtonInfo) obj[0];
                buttons.add(button);
            }
        }
        return buttons;
    }

    @Override
    public void deleteByRole(String[] roles) throws Exception {
        if(roles == null || roles.length<1){
            return;
        }
        String sql = "delete from right_info where role_id in ( ? ";
        if(roles != null && roles.length>1){
            for(int i=1;i<roles.length;i++){
                sql += " , ? ";
            }
        }
        sql += " )";
        this.baseDao.excuteBySQL(sql, roles);
    }
}
