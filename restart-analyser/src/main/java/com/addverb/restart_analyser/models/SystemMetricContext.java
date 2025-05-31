package com.addverb.restart_analyser.models;

import com.sun.management.OperatingSystemMXBean;
import lombok.Data;

import java.lang.management.*;
import java.util.List;

@Data
public class SystemMetricContext {
    public final ThreadMXBean threadBean;
    public final MemoryMXBean memoryBean;
    public final OperatingSystemMXBean osBean;
    public final List<GarbageCollectorMXBean> gcBeans;
    public final RuntimeMXBean runtimeMXBean;
    private long uptimeMillis;

    public SystemMetricContext() {
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        if (!threadBean.isThreadCpuTimeEnabled()) {
            threadBean.setThreadCpuTimeEnabled(true);
        }
    }
}
