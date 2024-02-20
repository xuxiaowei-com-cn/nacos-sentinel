package cn.com.xuxiaowei.nacos.sentinel.listener;

import cn.com.xuxiaowei.nacos.sentinel.properties.NacosSentinelDiscoveryProperties;
import cn.com.xuxiaowei.nacos.sentinel.utils.StringUtils;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.client.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * Nacos 注册中心 监听程序
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
@Component
public class NacosDiscoveryListener {

	private NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties;

	@Autowired
	public void setNacosSentinelDiscoveryProperties(NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties) {
		this.nacosSentinelDiscoveryProperties = nacosSentinelDiscoveryProperties;
	}

	private NamingService namingService;

	private void createNamingService() {
		if (!(namingService != null && Constants.HealthCheck.UP.equals(namingService.getServerStatus()))) {
			Properties properties = nacosSentinelDiscoveryProperties.getProperties();
			String serverAddr = properties.getProperty(PropertyKeyConst.SERVER_ADDR);
			String namespace = properties.getProperty(PropertyKeyConst.NAMESPACE);

			log.info("");
			log.info("Nacos 连接地址: {}", serverAddr);
			log.info("Nacos 命名空间: {}", namespace);
			log.info("");

			try {
				namingService = NamingFactory.createNamingService(properties);
			}
			catch (NacosException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void ready() throws NacosException {

		createNamingService();

		int pageNo = nacosSentinelDiscoveryProperties.getPageNo();
		int pageSize = nacosSentinelDiscoveryProperties.getPageSize();
		String groupName = nacosSentinelDiscoveryProperties.getGroupName();

		ListView<String> servicesOfServer = namingService.getServicesOfServer(pageNo, pageSize, groupName);
		List<String> serviceNames = servicesOfServer.getData();

		log.info("");
		log.info("Nacos 服务总数量: {}", serviceNames.size());

		int maxLength = 0;
		for (String serviceName : serviceNames) {
			maxLength = Math.max(maxLength, serviceName.length());
		}

		log.info("");
		log.info("Nacos 服务实例:");
		for (String serviceName : serviceNames) {
			healthy(maxLength, serviceName, namingService, true);
			healthy(maxLength, serviceName, namingService, false);
		}

		log.info("");
		log.info("Nacos 服务健康状况:");
		for (String serviceName : serviceNames) {
			List<Instance> instanceOnline = namingService.selectInstances(serviceName, true);
			List<Instance> instanceOffline = namingService.selectInstances(serviceName, false);
			log.info("Nacos 服务名称: {}，健康数量: {}，不健康数量: {}", StringUtils.formatLength(serviceName, maxLength),
					StringUtils.formatLength(instanceOnline.size(), 2),
					StringUtils.formatLength(instanceOffline.size(), 2));
		}

		for (String serviceName : serviceNames) {
			namingService.subscribe(serviceName, event -> {
				NamingEvent namingEvent = (NamingEvent) event;
				List<Instance> instances = namingEvent.getInstances();

				log.info("");
				log.info("Nacos 服务订阅: {}", serviceName);
				log.info("Nacos 服务名称: {}，服务数量: {}，群组名称：{}", serviceName, instances.size(), namingEvent.getGroupName());

				for (Instance instance : instances) {
					String ip = instance.getIp();
					int port = instance.getPort();
					String clusterName = instance.getClusterName();
					log.info("Nacos 服务名称: {}，IP: {}，端口: {}，群组名称: {}，集群名称: {}", serviceName,
							StringUtils.formatLength(ip, 15), StringUtils.formatLength(port, 5),
							namingEvent.getGroupName(), clusterName);
				}
			});
		}
	}

	private void healthy(int maxLength, String serviceName, NamingService namingService, boolean healthy)
			throws NacosException {
		List<Instance> instances = namingService.selectInstances(serviceName, healthy);
		if (!instances.isEmpty()) {
			for (Instance instance : instances) {
				String ip = instance.getIp();
				int port = instance.getPort();
				String clusterName = instance.getClusterName();
				log.info("Nacos 服务名称: {}，健康状况: {}，IP: {}，端口: {}，群组名称: {}，集群名称: {}",
						StringUtils.formatLength(serviceName, maxLength), StringUtils.formatLength(healthy, 5),
						StringUtils.formatLength(ip, 15), StringUtils.formatLength(port, 5),
						StringUtils.extractAtLeft(instance.getServiceName()), clusterName);
			}
		}
	}

}
