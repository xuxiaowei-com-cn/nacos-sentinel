package cn.com.xuxiaowei.nacos.sentinel.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Nacos 注册中心
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@Accessors(chain = true)
public class Discovery {

	private String id;

	private String serviceName;

	private String ip;

	private int port;

}
