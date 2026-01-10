package com.crumbcookie.crumbcookieresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrumbcookieresponseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrumbcookieresponseApplication.class, args);
	}

}
