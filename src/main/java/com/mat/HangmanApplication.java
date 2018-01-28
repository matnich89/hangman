package com.mat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HangmanApplication extends AsyncConfigurerSupport {

	public static void main(String[] args) {
		SpringApplication.run(HangmanApplication.class, args);
	}


}
