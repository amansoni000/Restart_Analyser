package com.addverb.restart_analyser.collectors;

import com.addverb.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.addverb.restart_analyser.models.SystemMetricContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Order(5)
public class GcMetricCollector implements SystemMetricCollector {
    @Value("${restart.detection.gc.excessCount:50}")
    int excessiveGcCount;

    @Value("${restart.detection.gc.highGcTime:5000}")
    int highGcTime;

    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        List<GarbageCollectorMXBean> gcBeans = context.gcBeans;

        List<Map<String, Object>> collectors = new ArrayList<>();
        long totalCount = 0;
        long totalTime = 0;

        for (GarbageCollectorMXBean gc : gcBeans) {
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();

            if (count >= 0) totalCount += count;
            if (time >= 0) totalTime += time;

            collectors.add(Map.of(
                    "name", gc.getName(),
                    "collectionCount", count,
                    "collectionTimeMs", time
            ));
        }

        if (totalCount > excessiveGcCount)
            abnormalities.add("Excessive GC Count");

        if (totalTime > highGcTime)
            abnormalities.add("High GC Time");

        Map<String, Object> gcMetricMap =  Map.of(
                "summary", Map.of(
                        "totalCollectionCount", totalCount,
                        "totalCollectionTimeMs", totalTime,
                        "excessiveGcDetected ( count or time )", totalTime > highGcTime || totalCount > excessiveGcCount
                ),
                "collectors", collectors
        );

        return Map.of("garbageCollectors", gcMetricMap);
    }
}
