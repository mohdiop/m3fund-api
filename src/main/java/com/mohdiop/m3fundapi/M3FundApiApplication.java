package com.mohdiop.m3fundapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableAsync
@EnableMethodSecurity
@EnableScheduling
public class M3FundApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(M3FundApiApplication.class, args);
	}

}
