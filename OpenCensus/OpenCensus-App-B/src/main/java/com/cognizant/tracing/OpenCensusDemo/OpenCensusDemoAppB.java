package com.cognizant.tracing.OpenCensusDemo;

import io.opencensus.trace.AttributeValue;
import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class OpenCensusDemoAppB {
    private static final Logger LOG = Logger.getLogger(OpenCensusDemoAppB.class.getName());

    @Autowired
    private RestTemplate restTemplate;
    public static void main(String[] args) {
        ZipkinTraceExporter.createAndRegister("http://localhost:9411/api/v2/spans", "tracing-App-B");
        TraceConfig traceConfig = Tracing.getTraceConfig();
        TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
        traceConfig.updateActiveTraceParams(activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());

        SpringApplication.run(OpenCensusDemoAppB.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("/appB-caught") public String caughtAppB() {
        LOG.log(Level.INFO, "Called App B");
        Tracer tracer = Tracing.getTracer();
        String response = "";
        try (Scope scope = tracer.spanBuilder("App B, Calling C").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            LOG.log(Level.INFO, "Calling App C");
            response = restTemplate.getForObject("http://localhost:8082/appC-caught", String.class);
            // 7. Annotate our span to capture metadata about our operation
            Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
            attributes.put("use", AttributeValue.stringAttributeValue("demo"));
            span.addAnnotation("Calling App C", attributes);
        }
        return "You've reached App B..." + response;
    }

    @RequestMapping("/appB-uncaught") public String uncaughtAppB() {
        LOG.log(Level.INFO, "Called App B");
        LOG.log(Level.INFO, "Calling App C");
        String response = restTemplate.getForObject("http://localhost:8082/appC-uncaught", String.class);
        return "You've reached App B..." + response;
    }
}
