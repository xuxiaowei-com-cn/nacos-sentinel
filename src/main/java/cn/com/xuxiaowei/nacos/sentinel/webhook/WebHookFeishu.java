package cn.com.xuxiaowei.nacos.sentinel.webhook;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Accessors(chain = true)
public class WebHookFeishu implements WebHook {

	private String logId;

	private String serverAddr;

	private String online;

	private String namespace;

	private String serviceName;

	private String ip;

	private String port;

	private String groupName;

	private String clusterName;

}
