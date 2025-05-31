package com.addverb.restart_analyser.controller;

import com.addverb.core.common.models.response.MetaDataResponse;
import com.addverb.restart_analyser.listener.ServiceRestartListener;
import com.addverb.restart_analyser.models.ServicePhase;
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
    public ResponseEntity<MetaDataResponse<Map<String, Object>>> getRestartMetrics() {
        Map<String, Object> metrics = serviceRestartListener.gatherSystemMetrics(ServicePhase.MANUAL_TRIGGER, ManagementFactory.getRuntimeMXBean().getUptime());
        return new ResponseEntity<>(MetaDataResponse.SUCCESS(metrics), HttpStatus.OK);
    }
}
