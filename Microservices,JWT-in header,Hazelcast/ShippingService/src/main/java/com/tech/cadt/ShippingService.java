package com.tech.cadt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
@ComponentScan("com.tech.cadt.shipping")
public class ShippingService {
	public static void main(String[] args) {
		SpringApplication.run(ShippingService.class, args);
	}
}