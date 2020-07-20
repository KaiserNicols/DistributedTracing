package com.cognizant.tracing.AppB;


import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;


@SpringBootApplication
@RestController
public class DTDemoApplicationAppB {

	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppB.class.getName());

	private Tracer tracer;

	public DTDemoApplicationAppB(Tracer tracer) {
		this.tracer = tracer;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppB.class, args);
	}

	@RequestMapping("/appB-caught")
	public String caughtAppB(){
		Scope scope = tracer.scopeManager().active();
		Span span = scope.span();
		span.log("Called App B");
		span.log("Calling App C");
		String response = restTemplate.getForObject("http://localhost:8082/appC-caught", String.class);
		span.log("Received response from App C");
		span.finish();
		return "You've reached App B...\n" + response;
	}

	@Bean
	public static JaegerTracer getTracer() {
		Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
		Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
		Configuration config = new Configuration("jaeger tutorial").withSampler(samplerConfig).withReporter(reporterConfig);
		return config.getTracer();
	}


//	@RequestMapping("/appA-caught") public String caughtAppA() {
////		LOG.log(Level.INFO, "Called App A");
////        LOG.log(Level.INFO, "Calling App B");
////		String response = restTemplate.getForObject("http://localhost:8081/appB-caught", String.class);
////		return "You've reached App A...\n" + response;
//	}
//
//	@RequestMapping("/appA-uncaught") public String uncaughtAppA() {
//		LOG.log(Level.INFO, "Called App A");
//        LOG.log(Level.INFO, "Calling App B");
//		String response = restTemplate.getForObject("http://localhost:8081/appB-uncaught", String.class);
//		return "You've reached App A...\n" + response;
//	}
}
