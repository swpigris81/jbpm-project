package com.huateng.jbpm.test;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StartHumanTaskServer implements ServletContextListener {
    public final static Log log = LogFactory.getLog(StartHumanTaskServer.class);

    public static void main(String[] args) {
        final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("\n");
                try {
                    taskServerDaemon.stopServer();
                    System.out.println("server stoped...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        taskServerDaemon.startServer();
        System.out.println("server started... (ctrl-c to stop it)");
    }

    public void contextDestroyed(ServletContextEvent event) {
        final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
        log.info("stopping server...");
        try {
            taskServerDaemon.stopServer();
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        log.info("server stoped");
    }

    public void contextInitialized(ServletContextEvent event) {
        final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
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
}
