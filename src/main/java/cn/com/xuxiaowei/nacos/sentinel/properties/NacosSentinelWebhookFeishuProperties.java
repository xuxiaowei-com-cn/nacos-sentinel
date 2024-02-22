package cn.com.xuxiaowei.nacos.sentinel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Nacos 监控 飞书 Webhook 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@ConfigurationProperties("nacos.sentinel.webhook.feishu")
public class NacosSentinelWebhookFeishuProperties {

	private String url;

}
