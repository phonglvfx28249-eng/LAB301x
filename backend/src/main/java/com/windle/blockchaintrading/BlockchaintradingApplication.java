package com.windle.blockchaintrading;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BlockchaintradingApplication {

	public static void main(String[] args) {
		String dotenvPath = new File("./backend/.env").exists() ? "./backend" : "./";

		Dotenv dotenv = Dotenv.configure()
				.directory(dotenvPath)
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(BlockchaintradingApplication.class, args);
	}

}
