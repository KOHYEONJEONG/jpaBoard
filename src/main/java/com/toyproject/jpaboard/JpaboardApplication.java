package com.toyproject.jpaboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.toyproject", "com.common"})
public class JpaboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaboardApplication.class, args);
	}

}
