package com.addverb.restart_analyser.collectors;

import com.addverb.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.addverb.restart_analyser.models.SystemMetricContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class MemoryMetricCollector implements SystemMetricCollector {
    @Value("${restart.detection.heapThreshold:80}")
    double heapThreshold;

    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        MemoryMXBean memoryBean = context.memoryBean;
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();

        double heapUsedPercentage = heap.getMax() > 0 ? (heap.getUsed() * 100.0 / heap.getMax()) : 0;
        double nonHeapUsedPercentage = nonHeap.getMax() > 0 ? (nonHeap.getUsed() * 100.0 / nonHeap.getMax()) : 0;

        if (heapUsedPercentage > heapThreshold) {
            abnormalities.add("High Heap Memory Usage");
        }
        if (nonHeapUsedPercentage > heapThreshold) {
            abnormalities.add("High Non-Heap Memory Usage");
        }

        Map<String, Object> usageBlocks =  Map.of(
                "heap", Map.of(
                        "usedBytes", heap.getUsed(),
                        "committedBytes", heap.getCommitted(),
                        "maxBytes", heap.getMax() > 0 ? heap.getMax() : "Unknown",
                        "usedMB", bytesToMB(heap.getUsed()),
                        "committedMB", bytesToMB(heap.getCommitted()),
                        "maxMB", heap.getMax() > 0 ? bytesToMB(heap.getMax()) : "Unknown",
                        "usedPercentage", heapUsedPercentage
                ),
                "nonHeap", Map.of(
                        "usedBytes", nonHeap.getUsed(),
                        "committedBytes", heap.getCommitted(),
                        "maxBytes", nonHeap.getMax() > 0 ? nonHeap.getMax() : "Unknown",
                        "usedMB", bytesToMB(nonHeap.getUsed()),
                        "committedMB", bytesToMB(heap.getCommitted()),
                        "maxMB", nonHeap.getMax() > 0 ? bytesToMB(nonHeap.getMax()) : "Unknown",
                        "usedPercentage", nonHeapUsedPercentage
                )
        );

        return Map.of("memoryUsage", usageBlocks);
    }

    private long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
