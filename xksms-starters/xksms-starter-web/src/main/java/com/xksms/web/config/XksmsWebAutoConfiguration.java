package com.xksms.web.config;


import com.xksms.log.properties.LogstashProperties;
import com.xksms.web.aspect.WebLogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LogstashProperties.class)
public class XksmsWebAutoConfiguration {


	@Bean
	// 当业务项目引入了 aop starter 时，这个 Bean 才会生效
	@ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
	public WebLogAspect webLogAspect() {
		return new WebLogAspect();
	}


}
