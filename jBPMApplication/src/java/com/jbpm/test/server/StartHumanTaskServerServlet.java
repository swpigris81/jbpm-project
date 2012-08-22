package com.jbpm.test.server;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StartHumanTaskServerServlet extends HttpServlet {
    
    public final static Log log = LogFactory.getLog(StartHumanTaskServerServlet.class);
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
