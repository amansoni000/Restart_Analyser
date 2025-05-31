package com.addverb.restart_analyser.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class RestartReasonLogger {

    public void logRestartReason(String reason, String serviceName, long uptime, Map<String, Object> systemMetrics) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put("event", reason);
        logEntry.put("serviceName", serviceName);
        logEntry.put("uptimeMillis", uptime);
        logEntry.put("metrics", systemMetrics);

        try {
            String jsonLog = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(logEntry);
            log.info("Application State Change : \n {}", jsonLog);
        } catch (JsonProcessingException e) {
            log.error("Failed to log restart reason in JSON", e);
        }
    }
}
