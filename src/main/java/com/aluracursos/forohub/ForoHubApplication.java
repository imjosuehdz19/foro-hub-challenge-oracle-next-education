package com.aluracursos.forohub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ForoHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForoHubApplication.class, args);

//        // Línea para generar tu hash personalizado
//        System.out.println("NUEVO HASH: " + new BCryptPasswordEncoder().encode("123456"));
	}


}
