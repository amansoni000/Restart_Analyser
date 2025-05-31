package com.project.restart_analyser.controller;

import com.project.restart_analyser.listener.ServiceRestartListener;
import com.project.restart_analyser.models.ServicePhase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class RestartMetricsController {
    private final ServiceRestartListener serviceRestartListener;

    @GetMapping("/get-report")
    public ResponseEntity<Map<String, Object>> getRestartMetrics() {
        Map<String, Object> metrics = serviceRestartListener.gatherSystemMetrics(ServicePhase.MANUAL_TRIGGER, ManagementFactory.getRuntimeMXBean().getUptime());
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }
}
