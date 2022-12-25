package com.melody.melody;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.melody.melody"})
@EntityScan(basePackages = {"com.melody.melody.adapter.persistence"})
@EnableTransactionManagement(order = 100)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass=true)
@EnableCaching
public class MelodyApplication {

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(MelodyApplication.class, args);
	}
}
