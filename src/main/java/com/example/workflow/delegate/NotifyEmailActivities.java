package com.example.workflow.delegate;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotifyEmailActivities implements JavaDelegate {

    private final static String ACTIVITY_ID = "Activity_0ioegrb";

    private final RuntimeService runtimeService;

    public NotifyEmailActivities(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        List<ProcessInstance> processes = findProcess(execution.getProcessBusinessKey());
        Optional<ProcessInstance> emailProcesses = findEmailActivities(processes).stream().findFirst();
        if (emailProcesses.isPresent()) {
            runtimeService.createMessageCorrelation("customer-lead-email-" + execution.getProcessBusinessKey())
                    .processInstanceId(emailProcesses.get().getProcessInstanceId())
                    .setVariables(execution.getVariables())
                    .correlate();
            System.out.println("Proceed flow with key " + execution.getProcessBusinessKey() + " for emails");
        } else {
            System.out.println("Did not find email events by key " + execution.getProcessBusinessKey());
        }

    }

    private List<ProcessInstance> findProcess(String key) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(key)
                .list();
    }

    private List<ProcessInstance> findEmailActivities(List<ProcessInstance> processes) {
        return processes
                .stream()
                .filter(p -> {
                    ActivityInstance activityInstance = runtimeService.getActivityInstance(p.getProcessInstanceId());
                    return Arrays.stream(activityInstance.getChildActivityInstances()).anyMatch(c -> c.getActivityId().equalsIgnoreCase(ACTIVITY_ID));
                })
                .collect(Collectors.toList());
    }
}
