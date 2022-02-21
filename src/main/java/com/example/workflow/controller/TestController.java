package com.example.workflow.controller;

import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ClmTaskService clmTaskService;

    @PostMapping(path = "/incoming-event")
    public void incomingEvent(@RequestBody Map<String, Object> params) {
        String leadId = (String) params.get("lead_id");
        System.out.println("********* Start event for lead id " + leadId + "***********");
        clmTaskService.execute(params);
    }
}
