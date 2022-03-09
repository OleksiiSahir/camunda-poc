package com.example.workflow.delegate;

import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.example.workflow.util.CamundaClmTaskVariables.LEAD_ID;

@Component
@RequiredArgsConstructor
public class EmailOverdue implements JavaDelegate {

    private final ClmTaskService clmTaskService;

    @Override
    public void execute(DelegateExecution execution) {
        String leadId = execution.getVariable(LEAD_ID).toString();
        System.out.println("EmailOverdue. Setting onTime false. leadId: {}" + leadId);
        clmTaskService.findToDoEmailTaskByBusinessKey(leadId).ifPresent(clmTaskService::overdueTask);
    }
}
