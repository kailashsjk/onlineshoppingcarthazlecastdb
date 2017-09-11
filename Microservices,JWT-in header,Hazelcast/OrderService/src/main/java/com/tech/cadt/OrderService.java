package com.tech.cadt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAutoConfiguration
@Component
@ComponentScan("com.tech.cadt.order.rest")
public class OrderService {
	public static void main(String[] args) {
		SpringApplication.run(OrderService.class, args);
	}
}