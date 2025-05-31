package com.addverb.restart_analyser.collectors;

import com.addverb.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.addverb.restart_analyser.models.SystemMetricContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Map;

@Component
@Order(4)
public class ThreadMetricCollector implements SystemMetricCollector {

    @Value("${restart.detection.peakThreadCount:300}")
    int peakThreadCount;

    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        ThreadMXBean threadBean = context.threadBean;
        if (threadBean.getPeakThreadCount() > peakThreadCount) {
            abnormalities.add("High Peak Thread Count");
        }

        long[] deadlockedThreads = threadBean.findDeadlockedThreads();

        if (deadlockedThreads != null && deadlockedThreads.length > 0) {
            abnormalities.add("Deadlocked threads detected: " + deadlockedThreads.length);
        }

        Map<String, Object> threadMatric = Map.of(
                "currentThreadCount", threadBean.getThreadCount(),
                "peakThreadCount", threadBean.getPeakThreadCount(),
                "daemonThreadCount", threadBean.getDaemonThreadCount(),
                "totalStartedThreadCount", threadBean.getTotalStartedThreadCount(),
                "highThreadUsageDetected", threadBean.getPeakThreadCount() > peakThreadCount,
                "deadlockedThreads", deadlockedThreads != null ? deadlockedThreads.length : 0
        );

        return Map.of("threads", threadMatric);
    }
}
