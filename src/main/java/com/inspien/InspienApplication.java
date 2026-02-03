package com.inspien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InspienApplication {

	public static void main(String[] args) {
		SpringApplication.run(InspienApplication.class, args);
	}

}
