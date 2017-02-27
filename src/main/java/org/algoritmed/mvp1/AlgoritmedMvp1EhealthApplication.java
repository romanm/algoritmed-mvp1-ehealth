package org.algoritmed.mvp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:config-app-spring.xml")
public class AlgoritmedMvp1EhealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgoritmedMvp1EhealthApplication.class, args);
	}
}
