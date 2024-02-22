package cn.com.xuxiaowei.nacos.sentinel.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author xuxiaowei
 * @since 0.0.1
 */
@Slf4j
public class WebHookUtils {

	public static Map<String, Object> post(String url, WebHook webHook) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<WebHook> httpEntity = new HttpEntity<>(webHook, httpHeaders);

		return restTemplate.postForObject(url, httpEntity, Map.class);
	}

}
