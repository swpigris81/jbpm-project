package com.webservice.common.init;

import javax.servlet.http.HttpServlet;

import org.springframework.web.context.support.WebApplicationContextUtils;

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
    }
}
