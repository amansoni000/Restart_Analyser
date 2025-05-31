package com.addverb.restart_analyser.collectors;

import com.addverb.restart_analyser.collectors.interfaces.SystemMetricCollector;
import com.addverb.restart_analyser.models.SystemMetricContext;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(3)
public class CpuMetricCollector implements SystemMetricCollector {

    @Override
    public Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context) {
        OperatingSystemMXBean osBean = context.osBean;

        int cores = osBean.getAvailableProcessors();
        double load = osBean.getSystemLoadAverage();
        double cpuLoad = osBean.getCpuLoad();
        double jvmCpuLoad = osBean.getProcessCpuLoad();

        boolean isOverloaded = load >= 0 && load > cores;

        long totalMem = osBean.getTotalMemorySize();
        long freeMem = osBean.getFreeMemorySize();
        long usedMem = totalMem - freeMem;

        long totalSwap = osBean.getTotalSwapSpaceSize();
        long freeSwap = osBean.getFreeSwapSpaceSize();
        long usedSwap = totalSwap - freeSwap;

        long processCpuTime = osBean.getProcessCpuTime();
        long committedVirtualMem = osBean.getCommittedVirtualMemorySize();

        // Abnormality checks
        if (load >= 0 && load > cores) {
            abnormalities.add("High CPU Load Average");
        }
        if (cpuLoad >= 0.9) {
            abnormalities.add("High System CPU Load");
        }
        if (jvmCpuLoad >= 0.8) {
            abnormalities.add("High JVM CPU Load");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("osName", osBean.getName());
        result.put("osVersion", osBean.getVersion());
        result.put("osArchitecture", osBean.getArch());
        result.put("isOverloaded", isOverloaded);
        result.put("availableProcessors", cores);
        result.put("systemLoadAverage", load >= 0 ? load : "Unknown");
        result.put("loadPerCore", load >= 0 ? load / cores : "Unknown");
        result.put("systemCpuLoad", cpuLoad >= 0 ? cpuLoad : "Unknown");
        result.put("jvmCpuLoad", jvmCpuLoad >= 0 ? jvmCpuLoad : "Unknown");
        result.put("processCpuTimeNanos", processCpuTime >= 0 ? processCpuTime : "Unknown");
        result.put("totalMemoryMB", bytesToMB(totalMem));
        result.put("freeMemoryMB", bytesToMB(freeMem));
        result.put("usedMemoryMB", bytesToMB(usedMem));
        result.put("totalSwapMB", bytesToMB(totalSwap));
        result.put("freeSwapMB", bytesToMB(freeSwap));
        result.put("usedSwapMB", bytesToMB(usedSwap));
        result.put("committedVirtualMemoryMB", bytesToMB(committedVirtualMem));
        return Map.of("cpu", result);

    }

    private long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
