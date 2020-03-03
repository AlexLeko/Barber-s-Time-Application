package com.alexleko.barberstime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;

@SpringBootApplication
public class BarbersTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarbersTimeApplication.class, args);

		System.out.println("Acesse via Swagger: http://localhost:8080/swagger-ui.html");
	}



}
