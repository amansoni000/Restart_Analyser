package com.addverb.restart_analyser.listener;

import com.addverb.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.addverb.restart_analyser.util.RestartReasonLogger;
import com.addverb.restart_analyser.models.ServicePhase;
import com.addverb.restart_analyser.models.SystemMetricContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ServiceRestartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.application.name:Unknown-service}")
    String applicationName;

    @Value("${restart.detection.enabled:false}")
    boolean enabled;

    private final RestartReasonLogger logger;
    private final SystemMetricContext context;
    private final List<SystemMetricCollector> systemMetricCollectors;

    public ServiceRestartListener(RestartReasonLogger logger, List<SystemMetricCollector> systemMetricCollectors) {
        this.logger = logger;
        this.systemMetricCollectors = systemMetricCollectors;
        this.context = new SystemMetricContext();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!enabled){
            return;
        }

        long uptime = System.currentTimeMillis() - event.getApplicationContext().getStartupDate();

        logger.logRestartReason(
                "Application Startup",
                getServiceName(),
                uptime,
                gatherSystemMetrics(ServicePhase.STARTUP, uptime)
        );
    }

    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        if(!enabled){
            return;
        }

        long uptime = System.currentTimeMillis() - event.getApplicationContext().getStartupDate();

        logger.logRestartReason(
                "Application Shutdown",
                getServiceName(),
                uptime,
                gatherSystemMetrics(ServicePhase.SHUTDOWN, uptime)
        );
    }

    public Map<String, Object> gatherSystemMetrics(ServicePhase phase, long uptimeMillis) {
        List<String> abnormalities = new ArrayList<>();

        context.setUptimeMillis(uptimeMillis);

        LinkedHashMap<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("timestamp", getCurrentUtcTimestamp());
        metrics.put("phase", phase);

        metrics.putAll(getMatricsFromCollectors(abnormalities));

        if (uptimeMillis > 0 && uptimeMillis < 300_000) {
            abnormalities.add("Application overall uptime < 5 minute)");
        }

        metrics.put("abnormalities", abnormalities);

        return metrics;
    }

    private LinkedHashMap<String, Object> getMatricsFromCollectors(List<String> abnormalities) {
        LinkedHashMap<String, Object> metrics = new LinkedHashMap<>();
        for (SystemMetricCollector collector : systemMetricCollectors) {
            metrics.putAll(collector.collect(abnormalities, context));
        }
        return metrics;
    }


    private String getServiceName() {
        return applicationName;
    }

    private String getCurrentUtcTimestamp() {
        return DateTimeFormatter.ISO_INSTANT
                .format(Instant.now());
    }
}
