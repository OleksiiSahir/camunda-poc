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

    private static final Map<String, String> ACTIVITIES = Map.of("call", "Activity_162ra50", "email", "Activity_0ioegrb");

    public TestController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping(path = "/incoming-event")
    public void incomingEvent(@RequestBody Map<String, Object> params) {
        String leadId = (String) params.get("lead_id");
        String type = (String) params.get("type");

        System.out.println("Received event with key " + leadId + " type " + type);

        List<ProcessInstance> processes = findProcess(leadId);

        if (processes.isEmpty()) {
            createNewProcess(params, leadId);
        } else {
            if ("call".equalsIgnoreCase(type)) {
                Optional<ActivityInstance> callActivity = findCallActivities(processes, type).stream().findFirst();
                if (callActivity.isPresent()) {
                    if (!"missed".equalsIgnoreCase((String) params.get("status"))) {
                        runtimeService.createMessageCorrelation("customer-lead-" + type + "-" + leadId)
                                .processInstanceId(callActivity.get().getProcessInstanceId())
                                .setVariables(params)
                                .correlate();
                    } else {
                        System.out.println("Skipping 'missed' event");
                    }
                    System.out.println("Proceed flow with key " + leadId + " for calls");
                } else {
                    createNewProcess(params, leadId);
                }
            }

            if ("email".equalsIgnoreCase(type)) {
                Optional<ProcessInstance> emailProcesses = findEmailActivities(processes, type).stream().findFirst();
                if (emailProcesses.isPresent()) {
                    runtimeService.createMessageCorrelation("customer-lead-" + type + "-" + leadId)
                            .processInstanceId(emailProcesses.get().getProcessInstanceId())
                            .setVariables(params)
                            .correlate();
                    System.out.println("Proceed flow with key " + leadId + " for emails");
                }
            }
        }
    }

    @PostMapping(path = "/message-incoming-event")
    public void messageIncomingEvent(@RequestBody Map<String, Object> params) {
        String leadId = (String) params.get("lead_id");
        String type = (String) params.get("type");

        runtimeService.createMessageCorrelation("customer-lead-" + type + "-" + leadId)
                .setVariables(params)
                .correlateAll();

    }

    private void createNewProcess(Map<String, Object> params, String leadId) {
        runtimeService.createConditionEvaluation()
                .processInstanceBusinessKey(leadId)
                .setVariables(params)
                .evaluateStartConditions();
        System.out.println("Start new flow with key " + leadId);
    }

    private List<ProcessInstance> findProcess(String key) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(key).list();
    }

    private List<ActivityInstance> findCallActivities(List<ProcessInstance> processes, String type) {
        return processes
                .stream()
                .map(p -> runtimeService.getActivityInstance(p.getProcessInstanceId()))
                .filter(a -> Arrays.stream(a.getChildActivityInstances()).anyMatch(c -> c.getActivityId().equalsIgnoreCase(ACTIVITIES.get(type))))
                .collect(Collectors.toList());
    }

    private List<ProcessInstance> findEmailActivities(List<ProcessInstance> processes, String type) {
        return processes
                .stream()
                .filter(p -> {
                    ActivityInstance activityInstance = runtimeService.getActivityInstance(p.getProcessInstanceId());
                    return Arrays.stream(activityInstance.getChildActivityInstances()).anyMatch(c -> c.getActivityId().equalsIgnoreCase(ACTIVITIES.get(type)));
                })
                .collect(Collectors.toList());
    }
}
