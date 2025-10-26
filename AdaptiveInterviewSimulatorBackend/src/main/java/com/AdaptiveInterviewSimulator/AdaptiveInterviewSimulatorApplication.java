package com.AdaptiveInterviewSimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.AdaptiveInterviewSimulator")
public class AdaptiveInterviewSimulatorApplication {

	public static void main(String[] args) {

		SpringApplication.run(AdaptiveInterviewSimulatorApplication.class, args);
		System.out.println("✅ Application started successfully!");
	}

}
