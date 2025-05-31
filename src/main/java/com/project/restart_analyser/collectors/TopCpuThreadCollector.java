package com.project.restart_analyser.collectors;

import com.project.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.project.restart_analyser.models.SystemMetricContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Order(6)
public class TopCpuThreadCollector implements SystemMetricCollector {
    @Value("${restart.detection.stackTraceCount:5}")
    int stackTraceCount;

    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        ThreadMXBean threadBean = context.threadBean;
        long[] threadIds = threadBean.getAllThreadIds();

        List<Long> topThreadIds = Arrays.stream(threadIds)
                .mapToObj(id -> Map.entry(id, threadBean.getThreadCpuTime(id)))
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(stackTraceCount)
                .map(Map.Entry::getKey)
                .toList();

        List<Map<String, Object>> topCpuThreadsMap = topThreadIds.stream()
                .map(id -> {
                    ThreadInfo info = threadBean.getThreadInfo(id, 15);
                    if (info == null) return null;

                    long cpuTime = threadBean.getThreadCpuTime(id);
                    return Map.of(
                            "threadId", info.getThreadId(),
                            "threadName", info.getThreadName(),
                            "threadState", info.getThreadState().toString(),
                            "cpuTimeMs", cpuTime / 1_000_000,
                            "stackTrace", Arrays.stream(info.getStackTrace())
                                    .map(StackTraceElement::toString)
                                    .limit(15)
                                    .toList()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return Map.of("topCpuThreads", topCpuThreadsMap);
    }
}
