package cn.com.xuxiaowei.nacos.sentinel.vo;

import lombok.Data;

/**
 * 响应
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Data
public class ResponseVo {

	private boolean result;

	private String message;

	private String error;

}
