package com.adi.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerConTimeframeCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerConTimeframeCustomApplication.class, args);
	}

}
