package com.cognizant.tracing.AppA;


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

import java.util.logging.Level;
import java.util.logging.Logger;


@SpringBootApplication
@RestController
public class DTDemoApplicationAppA {

	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppA.class.getName());

	private Tracer tracer;

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public DTDemoApplicationAppA(Tracer tracer) {
		this.tracer = tracer;
	}

	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppA.class, args);
	}

	@RequestMapping("/appA-caught")
	public String caughtAppA(){
		Scope scope = tracer.scopeManager().active();
		Span span = scope.span();
		span.log("Called App A");
		span.log("Calling App B");
		String response = restTemplate.getForObject("http://localhost:8081/appB-caught", String.class);
		span.log("Received response from App B");
		span.finish();
		return "You've reached App A...\n" + response;
	}

	@Bean
	public static JaegerTracer getTracer() {
		Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
		Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
		Configuration config = new Configuration("jaeger tutorial").withSampler(samplerConfig).withReporter(reporterConfig);
		return config.getTracer();
	}

}
