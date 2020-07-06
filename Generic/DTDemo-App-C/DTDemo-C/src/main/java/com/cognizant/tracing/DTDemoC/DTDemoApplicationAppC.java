package com.cognizant.tracing.DTDemoC;

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
public class DTDemoApplicationAppC {
	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppC.class.getName());

	@Autowired
	private RestTemplate restTemplate;
	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppC.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/appC-caught") public String caughtAppC() {
		LOG.log(Level.INFO, "Called App C");
		try{
			int x = 1/0;
			System.out.println(x);
		}catch(ArithmeticException e){
			return "Caught Exception in App C";
		}
		return "You've reached App C...";
	}

	@RequestMapping("/appC-uncaught") public String uncaughtAppC() {
		LOG.log(Level.INFO, "Called App C");
			int x = 1/0;
			System.out.println(x);
		return "You've reached App C...";
	}
}
