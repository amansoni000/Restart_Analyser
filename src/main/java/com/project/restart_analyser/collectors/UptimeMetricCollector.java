package com.project.restart_analyser.collectors;

import com.project.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.project.restart_analyser.models.SystemMetricContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class UptimeMetricCollector implements SystemMetricCollector {
    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        return Map.of("uptime", getUptimeBlock(context.getUptimeMillis()));
    }

    private Map<String, Object> getUptimeBlock(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long mins = seconds / 60;
        long hrs = mins / 60;
        String formatted = String.format("%02dh %02dm %02ds", hrs, mins % 60, seconds % 60);

        return Map.of(
                "milliseconds", uptimeMillis,
                "seconds", seconds,
                "formatted", formatted
        );
    }
}
