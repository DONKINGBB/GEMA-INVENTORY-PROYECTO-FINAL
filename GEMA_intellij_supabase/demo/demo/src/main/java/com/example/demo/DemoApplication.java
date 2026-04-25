package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.demo.controller",
		"com.example.demo.service",
		"com.example.demo.repository",
		"com.example.demo.model"
})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public CommandLineRunner listarControladores(ApplicationContext ctx) {
		return args -> {
			System.out.println("🧭 Controladores detectados:");
			String[] beans = ctx.getBeanDefinitionNames();
			Arrays.stream(beans)
					.filter(b -> b.toLowerCase().contains("controller"))
					.forEach(System.out::println);
		};
	}

}
