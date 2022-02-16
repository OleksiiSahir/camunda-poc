package com.example.workflow.delegate;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotifyCallActivities implements JavaDelegate {

    private final static String ACTIVITY_ID = "Activity_162ra50";

    private final RuntimeService runtimeService;

    public NotifyCallActivities(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("NotifyCallActivities executed");
        List<ProcessInstance> processes = findProcess(execution.getProcessBusinessKey(), execution.getProcessInstanceId());
        Optional<ActivityInstance> callActivity = findCallActivities(processes).stream().findFirst();
        if (callActivity.isPresent()) {
            String status = (String) execution.getVariable("status");
            if (!"missed".equalsIgnoreCase(status)) {
                runtimeService.createMessageCorrelation("customer-lead-call-" + execution.getProcessBusinessKey())
                        .processInstanceId(callActivity.get().getProcessInstanceId())
                        .setVariables(execution.getVariables())
                        .correlate();
                skipEvent(execution, status);
            }
            skipEvent(execution, status);
        }
        System.out.println("Proceed flow with key " + execution.getProcessBusinessKey() + " for calls");
    }

    private void skipEvent(DelegateExecution execution, String type) {
        execution.setVariable("skipEvent", true);
        System.out.printf("Skipping call event with type '%s'", type);
    }

    private List<ProcessInstance> findProcess(String key, String currentId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(key)
                .list().stream()
                .filter(p -> !p.getProcessInstanceId().equalsIgnoreCase(currentId))
                .collect(Collectors.toList());
    }

    private List<ActivityInstance> findCallActivities(List<ProcessInstance> processes) {
        return processes
                .stream()
                .map(p -> runtimeService.getActivityInstance(p.getProcessInstanceId()))
                .filter(a -> Arrays.stream(a.getChildActivityInstances()).anyMatch(c -> c.getActivityId().equalsIgnoreCase(ACTIVITY_ID)))
                .collect(Collectors.toList());
    }

}
