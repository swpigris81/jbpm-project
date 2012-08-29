package com.webservice.jbpm.server.servlet;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webservice.jbpm.server.daemon.TaskServerDaemon;

/**
 * <p>Description: [JBPM服务启动servlet]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class HumanTaskServerServlet extends HttpServlet {
    private Log log = LogFactory.getLog(HumanTaskServerServlet.class);
    private TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
    
    public void init(){
        log.info("starting server...");
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
}
