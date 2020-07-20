package com.cognizant.tracing.OpenCensusDemo;

import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.*;
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
public class OpenCensusDemoAppA {
    private static final Logger LOG = Logger.getLogger(OpenCensusDemoAppA.class.getName());

    @Autowired
    private RestTemplate restTemplate;
    public static void main(String[] args) {
        ZipkinTraceExporter.createAndRegister("http://localhost:9411/api/v2/spans", "tracing-App-A");
        TraceConfig traceConfig = Tracing.getTraceConfig();
        TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
        traceConfig.updateActiveTraceParams(activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());

        SpringApplication.run(OpenCensusDemoAppA.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("/appA-caught") public String caughtAppA() {
        Tracer tracer = Tracing.getTracer();
        String response = "";
        LOG.log(Level.INFO, "Called App A");
        try (Scope scope = tracer.spanBuilder("App A, Calling B").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            LOG.log(Level.INFO, "Calling App B");
            response = restTemplate.getForObject("http://localhost:8081/appB-caught", String.class);
        }
        return "You've reached App A...\n" + response;
    }

    @RequestMapping("/appA-longTrace") public String multipleTracesAppA() {
        Tracer tracer = Tracing.getTracer();
        String response = "";
        LOG.log(Level.INFO, "Called App A");
        try (Scope scope = tracer.spanBuilder("Starting Long Trace").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            callOne();
            callTwo();
        }
        return "You've reached App A...\n" + response;
    }

    private void callOne(){
        Tracer tracer = Tracing.getTracer();
        try (Scope scope = tracer.spanBuilder("Call 1").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            try {
                Thread.sleep(100L);
                callTwo();
            }
            catch (InterruptedException e) {
                span.setStatus(Status.INTERNAL.withDescription(e.toString()));
            }
            Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
            attributes.put("use", AttributeValue.stringAttributeValue("demo"));
            span.addAnnotation("Invoking call 1", attributes);
        }
    }

    private void callTwo(){
        Tracer tracer = Tracing.getTracer();
        try (Scope scope = tracer.spanBuilder("Call 2").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException e) {
                span.setStatus(Status.INTERNAL.withDescription(e.toString()));
            }
            Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
            attributes.put("use", AttributeValue.stringAttributeValue("demo"));
            span.addAnnotation("Invoking call 2", attributes);
        }
    }

    @RequestMapping("/appA-uncaught") public String uncaughtAppA() {
        Tracer tracer = Tracing.getTracer();
        Scope scope = tracer.spanBuilder("uncaughtAppA").startScopedSpan();
        LOG.log(Level.INFO, "Called App A");
        LOG.log(Level.INFO, "Calling App B");
        String response = restTemplate.getForObject("http://localhost:8081/appB-uncaught", String.class);
        return "You've reached App A...\n" + response;
    }
}
