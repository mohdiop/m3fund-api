package com.mohdiop.m3fundapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class M3FundApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(M3FundApiApplication.class, args);
	}

}
