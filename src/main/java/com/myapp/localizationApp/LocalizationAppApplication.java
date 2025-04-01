package com.myapp.localizationApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LocalizationAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(LocalizationAppApplication.class, args);
	}

}
