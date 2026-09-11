package com.tr.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class ComTrMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComTrMessageApplication.class, args);
	}

}
