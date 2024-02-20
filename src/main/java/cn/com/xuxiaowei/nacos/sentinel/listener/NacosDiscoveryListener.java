package cn.com.xuxiaowei.nacos.sentinel.listener;

import cn.com.xuxiaowei.nacos.sentinel.properties.NacosSentinelDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
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

        log.info("service size: {}", serviceNames.size());

        int maxLength = 0;
        for (String serviceName : serviceNames) {
            maxLength = Math.max(maxLength, serviceName.length());
        }

        log.info("service healthy:");
        for (String serviceName : serviceNames) {
            List<Instance> instanceOnline = namingService.selectInstances(serviceName, true);
            List<Instance> instanceOffline = namingService.selectInstances(serviceName, false);
            log.info("{}:\ttrue: {}\tfalse: {}", String.format("%-" + maxLength + "s", serviceName), instanceOnline.size(), instanceOffline.size());
        }

        log.info("service instance:");
        for (String serviceName : serviceNames) {
            List<Instance> instanceOnline = namingService.selectInstances(serviceName, true);
            if (!instanceOnline.isEmpty()) {
                for (Instance instance : instanceOnline) {
                    String ip = instance.getIp();
                    int port = instance.getPort();
                    log.info("{} instance healthy true: {}:{}", String.format("%-" + maxLength + "s", serviceName), ip, port);
                }
            }

            List<Instance> instanceOffline = namingService.selectInstances(serviceName, false);
            if (!instanceOffline.isEmpty()) {
                for (Instance instance : instanceOffline) {
                    String ip = instance.getIp();
                    int port = instance.getPort();
                    log.info("{} instance healthy false: {}:{}", String.format("%-" + maxLength + "s", serviceName), ip, port);
                }
            }
        }
    }

}
