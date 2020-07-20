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
public class OpenCensusDemoAppC {
    private static final Logger LOG = Logger.getLogger(OpenCensusDemoAppC.class.getName());

    @Autowired
    private RestTemplate restTemplate;
    public static void main(String[] args) {
        ZipkinTraceExporter.createAndRegister("http://localhost:9411/api/v2/spans", "tracing-App-C");
        TraceConfig traceConfig = Tracing.getTraceConfig();
        TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
        traceConfig.updateActiveTraceParams(activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());

        SpringApplication.run(OpenCensusDemoAppC.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @RequestMapping("/appC-caught") public String caughtAppC() {
        Tracer tracer = Tracing.getTracer();
        LOG.log(Level.INFO, "Called App C");
        try (Scope scope = tracer.spanBuilder("App C, Do Something").startScopedSpan()) {
            Span span = tracer.getCurrentSpan();
            try{
                int x = 1/0;
                System.out.println(x);
            }catch(ArithmeticException e){
                span.setStatus(Status.INTERNAL.withDescription(e.toString()));
                return "Caught Exception in App C";
            }
            Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
            attributes.put("use", AttributeValue.stringAttributeValue("demo"));
            span.addAnnotation("Called App C", attributes);
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
