package com.project.restart_analyser.collectors.interfaces;

import com.project.restart_analyser.models.SystemMetricContext;

import java.util.List;
import java.util.Map;

public interface SystemMetricCollector {
    Map<String, Object> collect(List<String> abnormalities, SystemMetricContext context);
}
