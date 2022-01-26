package com.example.workflow.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TestController {

    private final RuntimeService runtimeService;

    public TestController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping(path = "/incoming-event")
    public void incomingEvent(@RequestBody Map<String, Object> params) {
        String leadId = (String) params.get("lead_id");
        System.out.println("********* Start event for lead id " + leadId + "***********");
        runtimeService.createConditionEvaluation()
                .processInstanceBusinessKey(leadId)
                .setVariables(params)
                .evaluateStartConditions();
    }
}
