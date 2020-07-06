package com.cognizant.tracing.SleuthAppB;

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
public class SleuthAppBApplication {

	private static final Logger LOG = Logger.getLogger(SleuthAppBApplication.class.getName());

	@Autowired
	private RestTemplate restTemplate;
	public static void main(String[] args) {
		SpringApplication.run(SleuthAppBApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/appB-caught") public String caughtAppB() {
		LOG.log(Level.INFO, "Called App B");
		String response = restTemplate.getForObject("http://localhost:8082/appC-caught", String.class);
		return "You've reached App B..." + response;
	}

	@RequestMapping("/appB-uncaught") public String uncaughtAppB() {
		LOG.log(Level.INFO, "Called App B");
		String response = restTemplate.getForObject("http://localhost:8082/appC-uncaught", String.class);
		return "You've reached App B..." + response;
	}

}
