package com.naloga.daniimdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

// DANI-TESTING
//(exclude = {
//SecurityAutoConfiguration.class})
@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
public class DaniImdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaniImdbApplication.class, args);
	}

}
