package cn.com.xuxiaowei.nacos.sentinel.properties;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Nacos 监控 注册中心 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@ConfigurationProperties("nacos.sentinel.discovery")
public class NacosSentinelDiscoveryProperties {

	private String serverAddr = "127.0.0.1:8848";

	private String namespace = "public";

	private int pageNo = 1;

	private int pageSize = Integer.MAX_VALUE;

	private String groupName = Constants.DEFAULT_GROUP;

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
		properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
		return properties;
	}

}
