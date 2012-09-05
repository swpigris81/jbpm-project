package com.webservice.jbpm.server.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.webservice.jbpm.server.daemon.TaskServerDaemon;
import com.webservice.system.common.helper.SpringHelper;

/**
 * <p>Description: [JBPM服务启动servlet]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class HumanTaskServerServlet extends HttpServlet {
    private Log log = LogFactory.getLog(HumanTaskServerServlet.class);
    private TaskServerDaemon taskServerDaemon;
    
    public void init(){
        log.info("starting server...");
        initSpringService();
        if(taskServerDaemon == null){
            taskServerDaemon = new TaskServerDaemon();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("\n");
                try {
                    taskServerDaemon.stopServer();
                    log.info("server stoped...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    log.info("server stop error: " + e.getMessage());
                }
            }
        });
        taskServerDaemon.startServer();
        log.info("server started... (ctrl-c to stop it)");
    }
    
    public void destroy(){
        if(taskServerDaemon == null){
            taskServerDaemon = new TaskServerDaemon();
        }
        log.info("stopping server...");
        try {
            taskServerDaemon.stopServer();
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        log.info("server stoped");
        super.destroy();
    }
    /**
     * <p>Discription:[初始化Spring 容器]</p>
     * @author 大牙-小白
     * @update 2012-9-5 大牙-小白 [变更描述]
     */
    private void initSpringService(){
        log.info("初始化Spring applicationContext...");
        ServletContext servletContext = this.getServletContext();
        SpringHelper.setWac(WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext));
        log.info("初始化Spring applicationContext完成");
    }
}
