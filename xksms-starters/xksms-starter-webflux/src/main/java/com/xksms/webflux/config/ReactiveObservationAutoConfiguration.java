package com.xksms.webflux.config;// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/config/ReactiveObservationAutoConfiguration.java
// ...

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveObservationAutoConfiguration {

}