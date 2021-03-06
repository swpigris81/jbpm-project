package com.jbpm.test.client.jbpm.humantask;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.jpa.JpaPersistenceContext;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.jbpm.persistence.JpaProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;

public class MultipleUseJpaPersistenceContextManager implements ProcessPersistenceContextManager {

    Environment env;
    private EntityManagerFactory emf;
    
    private EntityManager appScopedEntityManager;
    protected EntityManager cmdScopedEntityManager;
    
    private boolean internalAppScopedEntityManager;
    private boolean internalCmdScopedEntityManager;
    
    public MultipleUseJpaPersistenceContextManager(Environment env) {
        this.env = env;
        this.emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
    }
    
    public void beginCommandScopedEntityManager() {
        checkAppScopedEntityManager();
        EntityManager cmdScopedEntityManager = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        if (cmdScopedEntityManager == null
                || (this.cmdScopedEntityManager != null && !this.cmdScopedEntityManager.isOpen())) {
            internalCmdScopedEntityManager = true;
            this.cmdScopedEntityManager = this.emf.createEntityManager(); // no need to call joinTransaction as it will
                                                                          // do so if one already exists
            this.env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, this.cmdScopedEntityManager);
            cmdScopedEntityManager = this.cmdScopedEntityManager;
        } else {
            internalCmdScopedEntityManager = false;
        }
        cmdScopedEntityManager.joinTransaction();
        appScopedEntityManager.joinTransaction();
    }

    public void dispose() {
        if (this.internalAppScopedEntityManager) {
            if (this.appScopedEntityManager != null && this.appScopedEntityManager.isOpen()) {
                this.appScopedEntityManager.close();
            }
            this.internalAppScopedEntityManager = false;
            this.env.set(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, null);
            this.appScopedEntityManager = null;
        }


        if (this.internalCmdScopedEntityManager) {
            if (this.cmdScopedEntityManager != null && this.cmdScopedEntityManager.isOpen()) {
                this.cmdScopedEntityManager.close();
            }
            this.internalCmdScopedEntityManager = false;
            this.env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null);
            this.cmdScopedEntityManager = null;
        }
    }

    public void endCommandScopedEntityManager() {
        if (this.internalCmdScopedEntityManager) {
            this.env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null);
        }
    }

    public PersistenceContext getApplicationScopedPersistenceContext() {
        checkAppScopedEntityManager();
        return new JpaPersistenceContext(appScopedEntityManager);
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext(this.cmdScopedEntityManager);
    }

    public ProcessPersistenceContext getProcessPersistenceContext() {
        if (cmdScopedEntityManager == null) {
            this.emf.createEntityManager();;
        }
        return new JpaProcessPersistenceContext(cmdScopedEntityManager);
    }
    
    private void checkAppScopedEntityManager() {
        if (this.appScopedEntityManager == null) {
            // Use the App scoped EntityManager if the user has provided it, and it is open.
            this.appScopedEntityManager = (EntityManager) this.env.get(EnvironmentName.APP_SCOPED_ENTITY_MANAGER);
            if (this.appScopedEntityManager != null && !this.appScopedEntityManager.isOpen()) {
                throw new RuntimeException("Provided APP_SCOPED_ENTITY_MANAGER is not open");
            }


            if (this.appScopedEntityManager == null) {
                internalAppScopedEntityManager = true;
                this.appScopedEntityManager = this.emf.createEntityManager();


                this.env.set(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.appScopedEntityManager);
            } else {
                internalAppScopedEntityManager = false;
            }
        }
    }
}
