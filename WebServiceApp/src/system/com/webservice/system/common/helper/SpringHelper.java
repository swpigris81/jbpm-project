package com.webservice.system.common.helper;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>Description: [Spring助手类]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙-小白</a>
 * @version v0.1
 */
public class SpringHelper {

    private static WebApplicationContext wac;

    /**
     * 取得bean
     * @param request
     * @param beanName
     * @return
     */
    public static Object getBean(HttpServletRequest request, String beanName) {
        if (wac != null) {
            return wac.getBean(beanName);
        } else {
            HttpSession session = request.getSession(true);
            if (session == null) {
                throw new RuntimeException(
                        " 无法取得Session");
            }
            wac = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(session
                            .getServletContext());
            if (wac == null) {
                throw new RuntimeException(
                        "无法正常取得 WebApplicationContext");
            }
            return wac.getBean(beanName);
        }
    }
    
    /**
     * 取得Bean
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        if (wac == null) {
            throw new RuntimeException(
                    "无法正常取得 WebApplicationContext");
        }
        return wac.getBean(beanName);
    }

    public static void setWac(WebApplicationContext wac) {
        SpringHelper.wac = wac;
    }

    /**
     * 取得Hibernate Session
     * @return
     */
    public static Session getHibernateSession() {
        SessionFactory sf = (SessionFactory) getBean("sessionFactory");
        return sf.openSession();
    }
    
    /**
     * 取得连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        DataSource ds = (DataSource) getBean("dataSource");
        return ds.getConnection();
    }
}
