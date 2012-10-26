package com.webservice.jbpm4.identity;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jbpm.api.JbpmException;
import org.jbpm.api.identity.Group;
import org.jbpm.api.identity.User;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.jbpm.pvm.internal.id.DbidGenerator;
import org.jbpm.pvm.internal.identity.impl.GroupImpl;
import org.jbpm.pvm.internal.identity.impl.MembershipImpl;
import org.jbpm.pvm.internal.identity.impl.UserImpl;
import org.jbpm.pvm.internal.identity.spi.IdentitySession;
import org.jbpm.pvm.internal.util.CollectionUtil;

import com.webservice.system.common.helper.SpringHelper;

/** 
 * <p>Description: [自定义用户角色系统(替代JBPM自有的用户角色系统)]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class UserSession implements IdentitySession {
    protected Session session;
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @param givenName
     * @param familyName
     * @param businessEmail
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public String createUser(String userName, String givenName,
            String familyName, String businessEmail) {
        if (session == null) {
            injectSession();
        }
        try {
            User user = findUserById(userName);
            if (user != null) {
                throw new JbpmException("Cannot create user, userId: ["
                        + userName + "] has been used");
            }
        } catch (Exception ex) {
            throw new JbpmException(
                    "Cannot create user, error while validating", ex);
        }
        UserImpl user = new UserImpl(userName, givenName, familyName);
        user.setBusinessEmail(businessEmail);

        long dbid = EnvironmentImpl.getFromCurrent(DbidGenerator.class)
                .getNextId();
        user.setDbid(dbid);
        session.save(user);
        return user.getId();
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public User findUserById(String userId) {
        if (session == null) {
            injectSession();
        }
        return (User) session.createCriteria(UserImpl.class)
                .add(Restrictions.eq("id", userId))
                .uniqueResult();
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userIds
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public List<User> findUsersById(String... userIds) {
        if (session == null) {
            injectSession();
        }
        List<?> users = session.createCriteria(UserImpl.class)
                .add(Restrictions.in("id", userIds)).list();
        if (userIds.length != users.size()) {
            throw new JbpmException("not all users were found: "
                    + Arrays.toString(userIds));
        }
        return CollectionUtil.checkList(users, User.class);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public List<User> findUsers() {
        if (session == null) {
            injectSession();
        }
        List<?> users = session.createCriteria(UserImpl.class).list();
        return CollectionUtil.checkList(users, User.class);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public void deleteUser(String userId) {
        if (session == null) {
            injectSession();
        }
        // lookup the user
        User user = findUserById(userId);

        // cascade the deletion to the memberships
        List<?> memberships = session.createCriteria(MembershipImpl.class)
                .add(Restrictions.eq("user", user)).list();

        // delete the related memberships
        for (Object membership : memberships) {
            session.delete(membership);
        }

        // delete the user
        session.delete(user);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param groupName
     * @param groupType
     * @param parentGroupId
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public String createGroup(String groupName, String groupType,
            String parentGroupId) {
        if (session == null) {
            injectSession();
        }
        try {
            GroupImpl group = (GroupImpl) findGroupById(groupName);
            if (group != null) {
                throw new JbpmException("Cannot create group, groupId: ["
                        + groupName + "] has been used");
            }
        } catch (Exception ex) {
            throw new JbpmException(
                    "Cannot create group, error while validating", ex);
        }
        GroupImpl group = new GroupImpl();
        String groupId = groupType != null ? groupType + "." + groupName
                : groupName;
        group.setId(groupId);

        long dbid = EnvironmentImpl.getFromCurrent(DbidGenerator.class)
                .getNextId();
        group.setDbid(dbid);

        group.setName(groupName);
        group.setType(groupType);

        if (parentGroupId != null) {
            GroupImpl parentGroup = (GroupImpl) findGroupById(parentGroupId);
            group.setParent(parentGroup);
        }

        session.save(group);

        return group.getId();
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param groupId
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public List<User> findUsersByGroup(String groupId) {
        if (session == null) {
            injectSession();
        }
        List<?> users = session.createCriteria(MembershipImpl.class)
                .createAlias("group", "g")
                .add(Restrictions.eq("g.id", groupId))
                .setProjection(Projections.property("user")).list();
        return CollectionUtil.checkList(users, User.class);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param groupId
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public Group findGroupById(String groupId) {
        if (session == null) {
            injectSession();
        }
        return (GroupImpl) session.createCriteria(GroupImpl.class)
                .add(Restrictions.eq("id", groupId)).uniqueResult();
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @param groupType
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public List<Group> findGroupsByUserAndGroupType(String userId,
            String groupType) {
        if (session == null) {
            injectSession();
        }
        List<?> groups = session.getNamedQuery("findGroupsByUserAndGroupType")
                .setString("userId", userId).setString("groupType", groupType)
                .list();
        return CollectionUtil.checkList(groups, Group.class);
    }

    /**
     * <p>Discription:[通过用户ID查询角色列表]</p>
     * @param userId
     * @return
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public List<Group> findGroupsByUser(String userId) {
        if(session == null){
            injectSession();
        }
        String hql = "select r from RoleInfo r, UserRole ur where r.roleId = ur.roleId and ur.userId = ?";
        List<?> list = session.createQuery(hql).setString(0, userId).list();
        return CollectionUtil.checkList(list, Group.class);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param groupId
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public void deleteGroup(String groupId) {
        if (session == null) {
            injectSession();
        }
        // look up the group
        GroupImpl group = (GroupImpl) findGroupById(groupId);

        // cascade the deletion to the memberships
        List<?> memberships = session.createCriteria(MembershipImpl.class)
                .add(Restrictions.eq("group", group)).list();

        // delete the related memberships
        for (Object membership : memberships) {
            session.delete(membership);
        }

        // delete the group
        session.delete(group);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @param groupId
     * @param role
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public void createMembership(String userId, String groupId, String role) {
        if (session == null) {
            injectSession();
        }
        User user = findUserById(userId);
        if (user == null) {
            throw new JbpmException("user " + userId + " doesn't exist");
        }
        Group group = findGroupById(groupId);
        if (group == null) {
            throw new JbpmException("group " + groupId + " doesn't exist");
        }

        MembershipImpl membership = new MembershipImpl();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole(role);

        long dbid = EnvironmentImpl.getFromCurrent(DbidGenerator.class)
                .getNextId();
        membership.setDbid(dbid);

        session.save(membership);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param userId
     * @param groupId
     * @param role
     * @author:大牙
     * @update:2012-10-26
     */
    @Override
    public void deleteMembership(String userId, String groupId, String role) {
        if (session == null) {
            injectSession();
        }
        MembershipImpl membership = (MembershipImpl) session
                .createCriteria(MembershipImpl.class).createAlias("user", "u")
                .createAlias("group", "g").add(Restrictions.eq("u.id", userId))
                .add(Restrictions.eq("g.id", groupId)).uniqueResult();
        session.delete(membership);
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return Session session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param session The session to set.
     */
    public void setSession(Session session) {
        this.session = session;
    }
    
    public void injectSession(){
        SessionFactory sf = (SessionFactory) SpringHelper.getBean("sessionFactory");
        Session session = sf.openSession();
        setSession(session);
    }
}
