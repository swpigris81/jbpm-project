package com.webservice.jbpm4.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.jbpm.api.Configuration;
import org.jbpm.api.IdentityService;
import org.jbpm.api.ProcessEngine;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.webservice.system.common.helper.SpringHelper;
import com.webservice.system.role.bean.RoleInfo;
import com.webservice.system.role.bean.UserRole;
import com.webservice.system.role.service.IRoleService;
import com.webservice.system.role.service.IUserRoleService;
import com.webservice.system.user.bean.UserInfo;
import com.webservice.system.user.service.IUserService;

/** 
 * <p>Description: JBPM4用户角色初始化信息, 如何扩展成自己的用户-角色系统</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class JbpmUserGroupInitServlet extends HttpServlet{
    /**
     * <p>Discription:[初始化系统中已有的用户角色到JBPM系统中]</p>
     * @author:大牙
     * @update:2012-10-25
     */
    public void init(){
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) SpringHelper.getBean("transactionManager");
        // 定义TransactionDefinition并设置好事务的隔离级别和传播方式。
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // 代价最大、可靠性最高的隔离级别，所有的事务都是按顺序一个接一个地执行
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(definition);
        try{
            IUserService userService = (IUserService)SpringHelper.getBean("userService");
            IRoleService roleService = (IRoleService)SpringHelper.getBean("roleService");
            IUserRoleService userRoleService = (IUserRoleService)SpringHelper.getBean("userRoleService");
            //JBPM
            ProcessEngine processEngine = Configuration.getProcessEngine();
            IdentityService identityService = processEngine.getIdentityService();
            //初始化用户
            List<UserInfo> users = userService.findAll();
            //初始化角色
            List<RoleInfo> groups = roleService.findAll();
            //初始化用户角色
            List<UserRole> userGroup = userRoleService.findAll();
            Map<String, String> groupMap = new HashMap<String, String>();
            for(RoleInfo role : groups){
                identityService.createGroup(role.getRoleName());
                groupMap.put(role.getRoleId(), role.getRoleName());
            }
            Map<String, String> userMap = new HashMap<String, String>();
            for(UserInfo user : users){
                identityService.createUser(user.getUserName(), user.getUserName(), user.getUserName(), user.getEmail());
                userMap.put(user.getUserName(), user.getUserName());
            }
            for(UserRole userRole : userGroup){
                String role = groupMap.get(userRole.getRoleId());
                if(role == null || "".equals(role.trim())){
                    continue;
                }
                if(userMap.get(userRole.getUserId()) == null || "".equals(userMap.get(userRole.getUserId()).trim())){
                    continue;
                }
                identityService.createMembership(userRole.getUserId(), role);
            }
        }catch(Exception e){
            status.setRollbackOnly();
        }finally{
            if(status.isRollbackOnly()){
                transactionManager.rollback(status);
            }else{
                transactionManager.commit(status);
            }
        }
    }
}
