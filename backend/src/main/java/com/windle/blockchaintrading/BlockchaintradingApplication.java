package com.windle.blockchaintrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlockchaintradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchaintradingApplication.class, args);
	}

}
