package com.cognizant.tracing.DTDemoB;

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
public class DTDemoApplicationAppB {

	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppB.class.getName());

	@Autowired
	private RestTemplate restTemplate;
	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppB.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/appB-caught") public String caughtAppB() {
		LOG.log(Level.INFO, "Called App B");
		LOG.log(Level.INFO, "Calling App C");
		String response = restTemplate.getForObject("http://localhost:8082/appC-caught", String.class);
		return "You've reached App B..." + response;
	}

	@RequestMapping("/appB-uncaught") public String uncaughtAppB() {
		LOG.log(Level.INFO, "Called App B");
		LOG.log(Level.INFO, "Calling App C");
		String response = restTemplate.getForObject("http://localhost:8082/appC-uncaught", String.class);
		return "You've reached App B..." + response;
	}

}
