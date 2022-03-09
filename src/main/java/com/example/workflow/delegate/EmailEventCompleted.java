package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.domain.TaskWithContext;
import com.example.workflow.enums.Status;
import com.example.workflow.enums.Type;
import com.example.workflow.repository.ClmTaskRepository;
import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;

import static com.example.workflow.util.CamundaClmTaskVariables.*;

@Component
@RequiredArgsConstructor
public class EmailEventCompleted implements JavaDelegate {

    private final ClmTaskService clmTaskService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Boolean skipTask = (Boolean) delegateExecution.getVariable("skipTask");
        if (skipTask) {
            System.out.println("EmailEventCompleted step skipped as skipTask: " + skipTask);
            return;
        }
        String leadId = delegateExecution.getVariable(LEAD_ID).toString();
        UUID interactionCreatedBy = (UUID) delegateExecution.getVariable(INTERACTION_CREATED_BY);
        UUID target = (UUID) delegateExecution.getVariable(INTERACTION_SOURCE);
        clmTaskService.getClosestTimeToEtaEmailTask(leadId)
                .ifPresent(task -> clmTaskService.closeTask(task, target, interactionCreatedBy));
    }

}
