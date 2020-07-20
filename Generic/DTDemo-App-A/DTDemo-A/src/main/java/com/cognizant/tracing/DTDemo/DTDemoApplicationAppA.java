package com.cognizant.tracing.DTDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class DTDemoApplicationAppA {
	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppA.class.getName());

	@Autowired
	private RestTemplate restTemplate;
	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppA.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/appA-caught") public String caughtAppA() {
		LOG.log(Level.INFO, "Called App A");
        LOG.log(Level.INFO, "Calling App B");
		String response = restTemplate.getForObject("http://localhost:8081/appB-caught", String.class);
		return "You've reached App A...\n" + response;
	}

	@RequestMapping("/appA-uncaught") public String uncaughtAppA() {
		LOG.log(Level.INFO, "Called App A");
        LOG.log(Level.INFO, "Calling App B");
		String response = restTemplate.getForObject("http://localhost:8081/appB-uncaught", String.class);
		return "You've reached App A...\n" + response;
	}
}
