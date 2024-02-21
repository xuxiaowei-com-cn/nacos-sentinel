package cn.com.xuxiaowei.nacos.sentinel.controller;

import cn.com.xuxiaowei.nacos.sentinel.utils.UrlUtils;
import cn.com.xuxiaowei.nacos.sentinel.vo.ResponseLinkVo;
import cn.com.xuxiaowei.nacos.sentinel.vo.ResponseVo;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * 首页
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@RestController
public class IndexRestController {

	@RequestMapping
	public ResponseVo index(HttpServletRequest request, HttpServletResponse response) {
		ResponseLinkVo result = new ResponseLinkVo();

		String contextPath = UrlUtils.contextPath(request);

		ResponseLinkVo.LinkVo linkVo = new ResponseLinkVo.LinkVo();
		result.setLinks(Collections.singletonList(linkVo));
		linkVo
			.setUrl(contextPath + NacosDiscoveryRestController.NACOS_DISCOVERY + NacosDiscoveryRestController.SERVICE);
		linkVo.setMethod(HttpMethod.GET.toString());
		linkVo.setDescription("获取 Nacos 注册中心服务列表");

		return result;
	}

}
