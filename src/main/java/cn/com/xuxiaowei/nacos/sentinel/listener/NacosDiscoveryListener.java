package cn.com.xuxiaowei.nacos.sentinel.listener;

import cn.com.xuxiaowei.nacos.sentinel.entity.Discovery;
import cn.com.xuxiaowei.nacos.sentinel.properties.NacosSentinelDiscoveryProperties;
import cn.com.xuxiaowei.nacos.sentinel.repository.NacosDiscoveryRepository;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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

	private NacosDiscoveryRepository nacosDiscoveryRepository;

	@Autowired
	public void setNacosSentinelDiscoveryProperties(NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties) {
		this.nacosSentinelDiscoveryProperties = nacosSentinelDiscoveryProperties;
	}

	@Autowired
	public void setNacosDiscoveryRepository(NacosDiscoveryRepository nacosDiscoveryRepository) {
		this.nacosDiscoveryRepository = nacosDiscoveryRepository;
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
		String logId = RandomStringUtils.randomAlphanumeric(6);
		log.info("【{}】Nacos 服务实例:", logId);
		for (String serviceName : serviceNames) {
			healthy(logId, maxLength, serviceName, namingService, true);
			healthy(logId, maxLength, serviceName, namingService, false);
		}

		log.info("");
		logId = RandomStringUtils.randomAlphanumeric(6);
		log.info("【{}】Nacos 服务健康状况:", logId);
		for (String serviceName : serviceNames) {
			List<Instance> instanceOnline = namingService.selectInstances(serviceName, true);
			List<Instance> instanceOffline = namingService.selectInstances(serviceName, false);
			log.info("【{}】Nacos 服务名称: {}，健康数量: {}，不健康数量: {}", logId, StringUtils.formatLength(serviceName, maxLength),
					StringUtils.formatLength(instanceOnline.size(), 2),
					StringUtils.formatLength(instanceOffline.size(), 2));
		}

		for (String serviceName : serviceNames) {
			namingService.subscribe(serviceName, event -> {
				NamingEvent namingEvent = (NamingEvent) event;
				List<Instance> instances = namingEvent.getInstances();

				log.info("");
				String subscribeLogId = RandomStringUtils.randomAlphanumeric(6);
				log.info("【{}】Nacos 服务订阅: {}", subscribeLogId, serviceName);
				log.info("【{}】Nacos 服务名称: {}，服务数量: {}，群组名称：{}", subscribeLogId, serviceName, instances.size(),
						namingEvent.getGroupName());

				List<Discovery> discoveries = nacosDiscoveryRepository.listByServiceName(serviceName);

				List<Instance> adds = new ArrayList<>();
				for (Instance instance : instances) {
					String ip = instance.getIp();
					int port = instance.getPort();
					String clusterName = instance.getClusterName();
					log.info("【{}】Nacos 服务名称: {}，IP: {}，端口: {}，群组名称: {}，集群名称: {}", subscribeLogId, serviceName,
							StringUtils.formatLength(ip, 15), StringUtils.formatLength(port, 5),
							namingEvent.getGroupName(), clusterName);

					boolean contains = false;
					for (Discovery discovery : discoveries) {
						if (discovery.getIp().equals(instance.getIp()) && discovery.getPort() == instance.getPort()) {
							contains = true;
						}
					}

					if (!contains) {
						adds.add(instance);
					}

				}

				List<Discovery> deletes = new ArrayList<>();
				for (Discovery discovery : discoveries) {

					boolean contains = false;
					for (Instance instance : instances) {
						if (discovery.getIp().equals(instance.getIp()) && discovery.getPort() == instance.getPort()) {
							contains = true;
						}
					}

					if (!contains) {
						deletes.add(discovery);
					}
				}

				if (!adds.isEmpty()) {
					log.info("");
					log.info("【{}】Nacos 上线服务: {}", subscribeLogId, serviceName);

					for (Instance instance : adds) {
						String ip = instance.getIp();
						int port = instance.getPort();

						String clusterName = instance.getClusterName();
						log.info("【{}】Nacos 上线服务名称: {}，IP: {}，端口: {}，群组名称: {}，集群名称: {}", subscribeLogId, serviceName,
								StringUtils.formatLength(ip, 15), StringUtils.formatLength(port, 5),
								StringUtils.extractAtLeft(instance.getServiceName()), clusterName);

						Discovery discovery = new Discovery().setId(UUID.randomUUID().toString())
							.setServiceName(serviceName)
							.setIp(ip)
							.setPort(port);

						nacosDiscoveryRepository.save(discovery);
					}
				}

				if (!deletes.isEmpty()) {
					log.warn("");
					log.warn("【{}】Nacos 下线服务: {}", subscribeLogId, serviceName);

					for (Discovery discovery : deletes) {
						String id = discovery.getId();
						String ip = discovery.getIp();
						int port = discovery.getPort();
						log.warn("【{}】Nacos 下线服务名称: {}，IP: {}，端口: {}", subscribeLogId, serviceName, ip, port);

						nacosDiscoveryRepository.deleteById(id);
					}
				}

			});
		}
	}

	private void healthy(String logId, int maxLength, String serviceName, NamingService namingService, boolean healthy)
			throws NacosException {
		List<Instance> instances = namingService.selectInstances(serviceName, healthy);
		if (!instances.isEmpty()) {
			for (Instance instance : instances) {
				String ip = instance.getIp();
				int port = instance.getPort();
				String clusterName = instance.getClusterName();
				log.info("【{}】Nacos 服务名称: {}，健康状况: {}，IP: {}，端口: {}，群组名称: {}，集群名称: {}", logId,
						StringUtils.formatLength(serviceName, maxLength), StringUtils.formatLength(healthy, 5),
						StringUtils.formatLength(ip, 15), StringUtils.formatLength(port, 5),
						StringUtils.extractAtLeft(instance.getServiceName()), clusterName);

				Discovery getByUnique = nacosDiscoveryRepository.getByUnique(serviceName, ip, port);

				if (getByUnique == null) {
					Discovery discovery = new Discovery().setId(UUID.randomUUID().toString())
						.setServiceName(serviceName)
						.setIp(ip)
						.setPort(port);
					nacosDiscoveryRepository.save(discovery);
				}
			}
		}
	}

}
