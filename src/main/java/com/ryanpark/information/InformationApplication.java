package com.ryanpark.information;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InformationApplication {

	public static void main(String[] args) {
		SpringApplication.run(InformationApplication.class, args);
	}

}
