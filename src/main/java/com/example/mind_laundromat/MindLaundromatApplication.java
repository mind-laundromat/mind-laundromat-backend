package com.example.mind_laundromat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MindLaundromatApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindLaundromatApplication.class, args);
	}

}
