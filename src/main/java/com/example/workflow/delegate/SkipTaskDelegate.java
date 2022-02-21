package com.example.workflow.delegate;

import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.workflow.util.CamundaClmTaskVariables.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkipTaskDelegate implements JavaDelegate {

    private final ClmTaskService clmTaskService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("SkipTaskDelegate");
        String leadId = delegateExecution.getVariable(LEAD_ID).toString();
//        UUID source = (UUID) delegateExecution.getVariable(SOURCE);
        UUID source = UUID.randomUUID();
//        UUID interactionCreatedBy = (UUID) delegateExecution.getVariable(INTERACTION_CREATED_BY);
        UUID interactionCreatedBy = UUID.randomUUID();

        clmTaskService.findToDoCallTaskByBusinessKey(leadId)
                .ifPresent(task -> clmTaskService.skipTask(task, source, interactionCreatedBy));

    }

}
