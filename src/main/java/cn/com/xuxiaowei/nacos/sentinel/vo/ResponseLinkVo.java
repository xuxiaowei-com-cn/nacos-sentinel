package cn.com.xuxiaowei.nacos.sentinel.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 链接 响应
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseLinkVo extends ResponseVo {

	private List<LinkVo> links;

	/**
	 * 链接 响应
	 *
	 * @author xuxiaowei
	 * @since 0.0.1
	 */
	@Data
	public static class LinkVo {

		private String url;

		private String method;

		private String description;

	}

}
