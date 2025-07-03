package com.xksms.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class XksmsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(XksmsUserApplication.class, args);
		log.info("XksmsUserApplication started...");
	}

}
