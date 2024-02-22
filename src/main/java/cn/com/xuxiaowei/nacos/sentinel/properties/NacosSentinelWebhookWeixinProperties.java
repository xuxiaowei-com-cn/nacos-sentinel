package cn.com.xuxiaowei.nacos.sentinel.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Nacos 监控 企业微信 Webhook 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Component
@ConfigurationProperties("nacos.sentinel.webhook.weixin")
public class NacosSentinelWebhookWeixinProperties {

	private String url;

	/**
	 * userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
	 */
	@JsonProperty("mentioned_list")
	private List<String> mentionedList;

	/**
	 * 手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
	 */
	@JsonProperty("mentioned_mobile_list")
	private List<String> mentionedMobileList;

}
