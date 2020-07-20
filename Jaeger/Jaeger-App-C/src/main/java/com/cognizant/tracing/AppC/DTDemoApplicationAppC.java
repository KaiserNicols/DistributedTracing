package com.cognizant.tracing.AppC;


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
public class DTDemoApplicationAppC {

	private static final Logger LOG = Logger.getLogger(DTDemoApplicationAppC.class.getName());

	private Tracer tracer;

	public DTDemoApplicationAppC(Tracer tracer) {
		this.tracer = tracer;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(DTDemoApplicationAppC.class, args);
	}

	@RequestMapping("/appC-caught")
	public String caughtAppC(){
		Scope scope = tracer.scopeManager().active();
		Span span = scope.span();
		span.log("Called App C");
		try{
			int x = 5 / 0;
		}catch (ArithmeticException e){
			e.printStackTrace();
			span.log(e.getMessage());
			span.finish();
			return "An exception was caught";
		}
		span.log("You made it to App C");
		span.finish();
		return "You made it to App C";
	}

	@Bean
	public static JaegerTracer getTracer() {
		Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
		Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
		Configuration config = new Configuration("jaeger tutorial").withSampler(samplerConfig).withReporter(reporterConfig);
		return config.getTracer();
	}
}
