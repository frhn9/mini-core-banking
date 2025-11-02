package org.fd.mcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniCoreBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniCoreBankingApplication.class, args);
	}

}
