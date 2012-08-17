package com.webservice.jbpm.server;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;
import com.webservice.jbpm.server.daemon.TaskServerDaemon;

public class JbpmSupport {
    private Log log = LogFactory.getLog(JbpmSupport.class);
    
    private EntityManagerFactory entityManagerFactory;
    private StatefulKnowledgeSession ksession;
    private TaskServiceSession taskServiceSession;
    /** session是否需要持久化 **/
    private boolean sessionPersistence = true;
    private WorkingMemoryInMemoryLogger logger;
    private String[] process;
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param process 流程图地址
     * @throws NamingException
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public void init() throws NamingException {
        createKnowledgeSession(createKnowledgeBase(getProcess()));
        // 为 ksession 设置log
        new JPAWorkingMemoryDbLogger(ksession);
        JPAProcessInstanceDbLog.setEnvironment(ksession.getEnvironment());
        //new JPAProcessInstanceDbLog(ksession.getEnvironment());
        // 创建 local human service 及其 handler
        org.jbpm.task.service.TaskService tService = new org.jbpm.task.service.TaskService(entityManagerFactory,
                SystemEventListenerFactory.getSystemEventListener());
        taskServiceSession = tService.createSession();
        // TODO 事务该如何设置？
        // taskServiceSession.setTransactionType("RESOURCE_LOCAL");
        SyncWSHumanTaskHandler humanTaskHandler = new SyncWSHumanTaskHandler(new LocalTaskService(tService),
                ksession);
        humanTaskHandler.setLocal(true);
        humanTaskHandler.connect();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);

        // set user group callback
        System.setProperty("jbpm.usergroup.callback", "org.jbpm.task.service.DefaultUserGroupCallbackImpl");
    }
    /**
     * <p>Discription:[创建基础知识]</p>
     * @param process 流程图地址
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public KnowledgeBase createKnowledgeBase(String... process) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        if(process == null || process.length < 1 || process[0].equals("")){
            kbuilder.add(new ClassPathResource("ProcessTask.bpmn"), ResourceType.BPMN2);
        }else{
            for (String p: process) {
                //kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
                kbuilder.add(new ClassPathResource(p), ResourceType.BPMN2);
            }
        }
        
        // Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    log.warn(error.toString());
                }
            }
        }
        return kbuilder.newKnowledgeBase();
    }
    /**
     * <p>Discription:[创建会话]</p>
     * @param kbase 基础知识
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) throws NamingException {
        StatefulKnowledgeSession session;
        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        
        if (sessionPersistence) {
            Environment env = createEnvironment(entityManagerFactory);
            session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);
        } else {
            Environment env = EnvironmentFactory.newEnvironment();
            env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
            session = kbase.newStatefulKnowledgeSession(conf, env);
            logger = new WorkingMemoryInMemoryLogger(session);
        }
        this.ksession = session;
        return session;
    }
    
    /**
     * <p>Discription:[创建环境]</p>
     * @param emf 实体管理工厂
     * @return
     * @author:[创建者中文名字]
     * @throws NamingException 
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Environment createEnvironment(EntityManagerFactory emf) throws NamingException {
//        Environment env = EnvironmentFactory.newEnvironment();
//        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
//        InitialContext ctx = new InitialContext();
//        UserTransaction transactionManager = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
//        env.set(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(transactionManager, null, transactionManager));
//        env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new MultipleUseJpaPersistenceContextManager(env));
//        return env;
        return TaskServerDaemon.env;
    }
    
    public TaskService getTaskService() {
        return new LocalTaskService(taskServiceSession.getService());
    }
    
    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String[] process.
     */
    public String[] getProcess() {
        return process;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param process The process to set.
     */
    public void setProcess(String[] process) {
        this.process = process;
    }
}
