package cn.com.xuxiaowei.nacos.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NacosSentinelApplication {

	public static void main(String[] args) {
		SpringApplication.run(NacosSentinelApplication.class, args);
	}

}
