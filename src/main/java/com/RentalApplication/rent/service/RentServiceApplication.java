package com.RentalApplication.rent.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class RentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentServiceApplication.class, args);
	}


}
