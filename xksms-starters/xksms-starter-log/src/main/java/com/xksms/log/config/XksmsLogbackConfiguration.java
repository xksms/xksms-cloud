package com.xksms.log.config;


import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XksmsLogbackConfiguration {


	@Bean
	public LogstashTcpSocketAppender logstashTcpSocketAppender() {
		LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();

		appender.setEncoder(logstashEncoder());
		return appender;
	}

	@Bean
	public LogstashEncoder logstashEncoder() {
		LogstashEncoder encoder = new LogstashEncoder();
		encoder.setContext((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());
		return encoder;
	}

	@Bean
	public PatternLayoutEncoder logPatternEncoder() {
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setPattern("[%date{ISO8601}] %level [%thread] %logger{10} - %msg%n");
		encoder.setContext((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());
		encoder.start();
		return encoder;
	}
}
