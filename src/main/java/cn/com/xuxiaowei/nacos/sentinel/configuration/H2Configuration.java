package cn.com.xuxiaowei.nacos.sentinel.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * H2 配置
 *
 * @author xuxiaowei
 * @since 0.0.1
 */
@Configuration
public class H2Configuration {

	@Bean
	public EmbeddedDatabase embeddedDatabase() {
		return new EmbeddedDatabaseBuilder().generateUniqueName(true)
			.setType(EmbeddedDatabaseType.H2)
			.setScriptEncoding("UTF-8")
			.addScript("cn/com/xuxiaowei/nacos/sentinel/schema.sql")
			.build();
	}

}
