package com.RentalApplication.rent.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

@SpringBootApplication
public class RentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentServiceApplication.class, args);
	}


}
