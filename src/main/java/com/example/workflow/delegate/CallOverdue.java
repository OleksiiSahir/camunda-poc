package com.example.workflow.delegate;


import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallOverdue implements JavaDelegate {

    private final ClmTaskService clmTaskService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String leadId = execution.getVariable("lead_id").toString();
        System.out.println("CallOverdue \n Setting onTime false. leadId: " + leadId);
        clmTaskService.findToDoCallTaskByBusinessKey(leadId).ifPresent(clmTaskService::overdueTask);
    }
}
