package com.webservice.common.init;

import javax.servlet.http.HttpServlet;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.webservice.netty.server.WebServiceServer;
import com.webservice.system.common.helper.SpringHelper;

/** 
 * <p>Description: [初始化Spring上下文]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class SpringInitServlet extends HttpServlet {
    public void init() {
        SpringHelper.setWac(WebApplicationContextUtils
                .getRequiredWebApplicationContext(this.getServletContext()));
        //启动Netty服务端
        WebServiceServer webServiceNetty = (WebServiceServer) SpringHelper.getBean("serverService");
        webServiceNetty.startNettyService();
    }

    /**
     * <p>Discription:[服务器停止时触发]</p>
     * @author:大牙
     * @update:2013-2-21
     */
    public void destroy() {
        //停止Netty服务端
        WebServiceServer webServiceNetty = (WebServiceServer) SpringHelper.getBean("serverService");
        webServiceNetty.stop();
        super.destroy();
    }
}
