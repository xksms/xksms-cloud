package com.xksms.user.biz;


import com.xksms.redis.RedisHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

	@Resource
	private RedisHelper redisHelper;

	@GetMapping("/trace")
	public String trace() {
		log.info("traceIdasdas");
		redisHelper.set("traceIdasdas", "sadsadsa");
		Object traceIdasdas = redisHelper.get("traceIdasdas").get();
		System.out.println("redis.get" + traceIdasdas);
		return "traceIdasdas: sadsadsa";
	}

	//错误

	@GetMapping("/error")
	public String error() {
		log.error("error");
		throw new RuntimeException("error");
	}

	//一个占用cpu 和 200m内存的线程
	@GetMapping("/cpu")
	public String cpu() {
		for (int i = 0; i < 1000000000; i++) {
			System.out.println(i);
		}
		return "success";
	}

	public static void main(String[] args) {

		//测试ArrayList	 自动扩容后地址是否变化
		List<String> list = new ArrayList<>();
		list.add("test1");
		log.info("Initial list address: {}", System.identityHashCode(list));
		//看内部elementData数组内存地址是否变化


		for (int i = 0; i < 20; i++) {
			list.add("test" + (i + 2));
			log.info("List address after adding element {}: {}", i + 2, System.identityHashCode(list));
		}
		log.info("Final list size: {}, Final list address: {}", list.size(), System.identityHashCode(list));

	}
}
