package cn.com.xuxiaowei.nacos.sentinel.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class WebHookUtils {

	public static Object post(String url, WebHookWeixinText webHook) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<WebHookWeixinText> httpEntity = new HttpEntity<>(webHook, httpHeaders);

		return restTemplate.postForObject(url, httpEntity, Object.class);
	}

}
