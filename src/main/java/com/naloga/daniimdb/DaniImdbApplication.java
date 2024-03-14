package com.naloga.daniimdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DaniImdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaniImdbApplication.class, args);
	}

}
