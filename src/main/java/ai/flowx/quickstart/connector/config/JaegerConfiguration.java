package ai.flowx.quickstart.connector.config;

import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaegerConfiguration {

    @Bean
    public static JaegerTracer getTracer() {
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration("quickstart-connector")
            .withSampler(samplerConfig)
            .withReporter(reporterConfig);
        JaegerTracer tracer = config.getTracer();

        GlobalTracer.registerIfAbsent(tracer);

        return tracer;
    }
}
