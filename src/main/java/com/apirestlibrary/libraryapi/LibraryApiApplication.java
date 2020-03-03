package com.apirestlibrary.libraryapi;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAdminServer
public class LibraryApiApplication extends SpringBootServletInitializer {
/*
	@Autowired
	private EmailService emailService;

	@Bean
	public CommandLineRunner runner(){
		return args -> {
			List<String> emails = Arrays.asList("26054c6a99-b108c0@inbox.mailtrap.io");
			emailService.sendEmailsList("Test service emails", emails);
		};
	}
*/
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
