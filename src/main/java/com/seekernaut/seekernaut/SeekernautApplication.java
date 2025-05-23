package com.seekernaut.seekernaut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan("com.seekernaut.seekernaut")
public class SeekernautApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeekernautApplication.class, args);
	}

}
