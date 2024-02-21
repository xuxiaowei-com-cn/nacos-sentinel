package cn.com.xuxiaowei.nacos.sentinel.controller;

import cn.com.xuxiaowei.nacos.sentinel.listener.NacosDiscoveryListener;
import cn.com.xuxiaowei.nacos.sentinel.properties.NacosSentinelDiscoveryProperties;
import cn.com.xuxiaowei.nacos.sentinel.utils.StringUtils;
import cn.com.xuxiaowei.nacos.sentinel.vo.ResponseDiscoveryVo;
import cn.com.xuxiaowei.nacos.sentinel.vo.ResponseVo;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.coyote.http11.Http11InputBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static cn.com.xuxiaowei.nacos.sentinel.controller.NacosDiscoveryRestController.NACOS_DISCOVERY;

/**
 * Nacos 注册中心
 *
 * @author xuxiaowei
 * @since 0.0.1
 * @see Http11InputBuffer
 */
@Slf4j
@RestController
@RequestMapping(NACOS_DISCOVERY)
public class NacosDiscoveryRestController {

	public static final String NACOS_DISCOVERY = "/nacos/discovery";

	public static final String SERVICE = "/service";

	private NacosDiscoveryListener nacosDiscoveryListener;

	@Autowired
	public void setNacosDiscoveryListener(NacosDiscoveryListener nacosDiscoveryListener) {
		this.nacosDiscoveryListener = nacosDiscoveryListener;
	}

	@GetMapping(SERVICE)
	public ResponseVo service(HttpServletRequest request, HttpServletResponse response)
			throws NacosException, JsonProcessingException {
		ResponseDiscoveryVo result = new ResponseDiscoveryVo();

		NamingService namingService = nacosDiscoveryListener.createNamingService();
		NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties = nacosDiscoveryListener
			.getNacosSentinelDiscoveryProperties();

		String serverAddr = nacosSentinelDiscoveryProperties.getServerAddr();
		String namespace = nacosSentinelDiscoveryProperties.getNamespace();
		int pageNo = nacosSentinelDiscoveryProperties.getPageNo();
		int pageSize = nacosSentinelDiscoveryProperties.getPageSize();
		String groupName = nacosSentinelDiscoveryProperties.getGroupName();

		ListView<String> servicesOfServer;
		try {
			servicesOfServer = namingService.getServicesOfServer(pageNo, pageSize, groupName);
		}
		catch (NacosException e) {
			String message = "从服务器获取所有服务名称异常。";
			result.setResult(false);
			result.setMessage(message);
			result.setError(ExceptionUtils.getMessage(e));
			log.error(message, e);
			return result;
		}
		List<String> serviceNames = servicesOfServer.getData();

		result.setServerAddr(serverAddr);
		result.setNamespace(namespace);

		List<ResponseDiscoveryVo.DiscoveryVo> discoveries = new ArrayList<>();
		result.setDiscoveries(discoveries);

		for (String serviceName : serviceNames) {

			List<ResponseDiscoveryVo.InstanceVo> instanceOnline = instances(namingService, serviceName, true);
			List<ResponseDiscoveryVo.InstanceVo> instanceOffline = instances(namingService, serviceName, false);

			ResponseDiscoveryVo.DiscoveryVo discoveryVo = new ResponseDiscoveryVo.DiscoveryVo();
			discoveryVo.setServiceName(serviceName);

			instanceOnline.addAll(instanceOffline);
			discoveryVo.setInstances(instanceOnline);
			discoveries.add(discoveryVo);
		}

		result.setResult(true);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
		String value = objectWriter.writeValueAsString(result);
		log.info("获取 Nacos 注册中心服务列表如下：\n{}", value);

		return result;
	}

	private List<ResponseDiscoveryVo.InstanceVo> instances(NamingService namingService, String serviceName,
			boolean healthy) throws NacosException {
		List<Instance> instances = namingService.selectInstances(serviceName, healthy);

		List<ResponseDiscoveryVo.InstanceVo> instanceVos = new ArrayList<>();

		for (Instance instance : instances) {
			String ip = instance.getIp();
			int port = instance.getPort();
			String clusterName = instance.getClusterName();

			ResponseDiscoveryVo.InstanceVo instanceVo = new ResponseDiscoveryVo.InstanceVo();
			instanceVo.setIp(ip);
			instanceVo.setPort(port);
			instanceVo.setClusterName(clusterName);
			instanceVo.setServiceName(StringUtils.extractAtLeft(instance.getServiceName()));
			instanceVo.setHealthy(healthy);

			instanceVos.add(instanceVo);
		}

		return instanceVos;
	}

}
