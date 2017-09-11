package com.tech.cadt;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import com.tech.cadt.product.repository.ProductRepository;


@SpringBootApplication
@EnableAutoConfiguration
@Component
@ComponentScan("com.tech.cadt.catalogue.rest")
public class CatalogueService {
	

	
	public static void main(String[] args) {
		SpringApplication.run(CatalogueService.class, args);
		 final Logger LOGGER = LoggerFactory.getLogger(CatalogueService.class);
		   String customlogger ="ADMS Loger::::";
		   LOGGER.info(customlogger+"CustomerService Started");
	}
	
	
}