package cn.com.xuxiaowei.nacos.sentinel.listener;

import cn.com.xuxiaowei.nacos.sentinel.properties.NacosSentinelDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
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
 * @see 0.0.1
 */
@Slf4j
@Component
public class NacosDiscoveryListener {

    private NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties;

    @Autowired
    public void setNacosSentinelDiscoveryProperties(NacosSentinelDiscoveryProperties nacosSentinelDiscoveryProperties) {
        this.nacosSentinelDiscoveryProperties = nacosSentinelDiscoveryProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() throws NacosException {
        Properties properties = nacosSentinelDiscoveryProperties.getProperties();

        log.info("{}: {}", PropertyKeyConst.SERVER_ADDR, properties.getProperty(PropertyKeyConst.SERVER_ADDR));
        log.info("{}: {}", PropertyKeyConst.NAMESPACE, properties.getProperty(PropertyKeyConst.NAMESPACE));

        NamingService namingService = NamingFactory.createNamingService(properties);

        int pageNo = nacosSentinelDiscoveryProperties.getPageNo();
        int pageSize = nacosSentinelDiscoveryProperties.getPageSize();
        String groupName = nacosSentinelDiscoveryProperties.getGroupName();

        ListView<String> servicesOfServer = namingService.getServicesOfServer(pageNo, pageSize, groupName);
        List<String> serviceNames = servicesOfServer.getData();

        log.info("");
        log.info("service size: {}", serviceNames.size());

        int maxLength = 0;
        for (String serviceName : serviceNames) {
            maxLength = Math.max(maxLength, serviceName.length());
        }

        log.info("");
        log.info("service instance:");
        for (String serviceName : serviceNames) {
            healthy(maxLength, serviceName, namingService, true);
            healthy(maxLength, serviceName, namingService, false);
        }

        log.info("");
        log.info("service healthy:");
        for (String serviceName : serviceNames) {
            List<Instance> instanceOnline = namingService.selectInstances(serviceName, true);
            List<Instance> instanceOffline = namingService.selectInstances(serviceName, false);
            log.info("{} service healthy:\ttrue: {}\tfalse: {}", String.format("%-" + maxLength + "s", serviceName), instanceOnline.size(), instanceOffline.size());
        }

        for (String serviceName : serviceNames) {
            namingService.subscribe(serviceName, event -> {
                log.info("");
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> instances = namingEvent.getInstances();

                log.info("{} instance subscribe {} change: \tsize: {}", serviceName, namingEvent.getGroupName(), instances.size());

                for (Instance instance : instances) {
                    String ip = instance.getIp();
                    int port = instance.getPort();
                    log.info("{} instance subscribe {}: {}:{}", serviceName, namingEvent.getGroupName(), ip, port);
                }
            });
        }
    }

    private void healthy(int maxLength, String serviceName, NamingService namingService, boolean healthy) throws NacosException {
        List<Instance> instances = namingService.selectInstances(serviceName, healthy);
        if (!instances.isEmpty()) {
            for (Instance instance : instances) {
                String ip = instance.getIp();
                int port = instance.getPort();
                log.info("{} service instance healthy {}: {}:{} {}", String.format("%-" + maxLength + "s", serviceName), healthy, ip, port, instance.getServiceName());
            }
        }
    }

}
