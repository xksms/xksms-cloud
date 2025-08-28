package com.xksms.notify.biz;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XksmsNotifyApplication {
	public static void main(String[] args) {
		System.setProperty("nacos.logging.default-config-enabled", "false");
		SpringApplication app = new SpringApplication(XksmsNotifyApplication.class);
		app.setWebApplicationType(WebApplicationType.REACTIVE);
		app.run(args);
	}
}