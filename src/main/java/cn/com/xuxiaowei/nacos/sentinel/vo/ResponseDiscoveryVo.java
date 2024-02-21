package cn.com.xuxiaowei.nacos.sentinel.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Nacos 注册中心 响应
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseDiscoveryVo extends ResponseVo {

	private String serverAddr;

	private String namespace;

	private List<DiscoveryVo> discoveries;

	/**
	 * Nacos 注册中心 实例 响应
	 *
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	public static class InstanceVo {

		private String ip;

		private int port;

		private String clusterName;

		private String serviceName;

		private boolean healthy;

	}

	/**
	 * Nacos 注册中心 响应
	 *
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	public static class DiscoveryVo {

		private String serviceName;

		private List<InstanceVo> instances;

	}

}
