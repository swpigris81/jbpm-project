package org.androidpn.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ���ڼ���û��Ƿ��½�Ĺ����������δ��¼�����ض���ָ�ĵ�¼ҳ�� ���ò��� 
 * checkSessionKey ������� Session �б���Ĺؼ���
 * redirectURL ����û�δ��¼�����ض���ָ����ҳ�棬URL������ ContextPath 
 * notCheckURLList ��������URL�б��Էֺŷֿ������� URL �в����� ContextPath
 */
public class SessionCheckFilter implements Filter {
	protected FilterConfig filterConfig = null;
	private String redirectURL = null;
	private Set<String> notCheckSuffixList;
    private Set<String> notCheckURLList;
	private String sessionKey = null;

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession();
		if (sessionKey == null) {
			filterChain.doFilter(request, response);
			return;
		}
		if ((!checkRequestURIIntNotFilterList(request))
				&& session.getAttribute(sessionKey) == null) {
			response.sendRedirect(request.getContextPath() + redirectURL);
			return;
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
	    notCheckSuffixList.clear();
		notCheckURLList.clear();
	}

	private boolean checkRequestURIIntNotFilterList(HttpServletRequest request) {
		String uri = request.getServletPath()
				+ (request.getPathInfo() == null ? "" : request.getPathInfo());

		if (notCheckURLList.contains(uri)) {
            return true;
        } else {
            int index = uri.lastIndexOf(".");
            if (index != -1) {
                String suffix = uri.substring(index + 1);
                return notCheckSuffixList.contains(suffix);
            }
        }
        return true;
	}

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        notCheckSuffixList = new HashSet<String>();
        notCheckURLList = new HashSet<String>();
        redirectURL = filterConfig.getInitParameter("redirectURL");
        sessionKey = filterConfig.getInitParameter("checkSessionKey");

        String notCheckSuffixListStr = filterConfig.getInitParameter("notCheckSuffixList");
        if (notCheckSuffixListStr != null) {
            String[] params = notCheckSuffixListStr.split(",");
            for (int i = 0; i < params.length; i++) {
                notCheckSuffixList.add(params[i].trim());
            }
        }

        String notCheckURLListStr = filterConfig.getInitParameter("notCheckURLList");
        if (notCheckURLListStr != null) {
            String[] params = notCheckURLListStr.split(",");
            for (int i = 0; i < params.length; i++) {
                notCheckURLList.add(params[i].trim());
            }
        }

    }
}