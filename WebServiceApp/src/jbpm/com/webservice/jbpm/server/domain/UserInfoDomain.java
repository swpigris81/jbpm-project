package com.webservice.jbpm.server.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.UserInfo;

/**
 * <p>Description: [描述该类概要功能介绍]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class UserInfoDomain implements UserInfo {
    private Map<Group, List<OrganizationalEntity>> groups = new HashMap<Group, List<OrganizationalEntity>>();

    private Map<OrganizationalEntity, String> emails = new HashMap<OrganizationalEntity, String>();

    private Map<OrganizationalEntity, String> languages = new HashMap<OrganizationalEntity, String>();

    private Map<OrganizationalEntity, String> displayNames = new HashMap<OrganizationalEntity, String>();

    public Map<Group, List<OrganizationalEntity>> getGroups() {
        return groups;
    }

    public void setGroups(Map<Group, List<OrganizationalEntity>> groups) {
        this.groups = groups;
    }

    public Map<OrganizationalEntity, String> getEmails() {
        return emails;
    }

    public void setEmails(Map<OrganizationalEntity, String> emails) {
        this.emails = emails;
    }

    public String getEmailForEntity(OrganizationalEntity entity) {
        return emails.get( entity );
    }



    public Map<OrganizationalEntity, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(Map<OrganizationalEntity, String> displayNames) {
        this.displayNames = displayNames;
    }

    public Map<OrganizationalEntity, String> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<OrganizationalEntity, String> languages) {
        this.languages = languages;
    }

    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        return groups.get( group ).iterator();
    }

    public boolean hasEmail(Group group) {
        return emails.containsKey( group );
    }

    public String getDisplayName(OrganizationalEntity entity) {
        String displayName = displayNames.get( entity );
        return ( displayName != null ) ? displayName : entity.getId();
    }

    public String getLanguageForEntity(OrganizationalEntity entity) {
        return languages.get( entity );
    }
}
