package cn.com.xuxiaowei.nacos.sentinel.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
public class UrlUtils {

	public static String contextPath(HttpServletRequest request) {
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();

		StringBuilder host = new StringBuilder();

		host.append(scheme).append("://").append(serverName);

		if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
			host.append(":").append(serverPort);
		}

		host.append(contextPath);

		return host.toString();
	}

}
