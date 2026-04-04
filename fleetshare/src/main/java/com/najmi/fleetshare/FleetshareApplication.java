package com.najmi.fleetshare;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FleetshareApplication {

	public static void main(String[] args) {
		Dotenv.configure().load();
		SpringApplication.run(FleetshareApplication.class, args);
	}

}
