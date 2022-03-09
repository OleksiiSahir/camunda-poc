package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.domain.TaskWithContext;
import com.example.workflow.repository.ClmTaskRepository;
import com.example.workflow.service.ClmTaskService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import com.example.workflow.enums.Status;
import com.example.workflow.enums.Type;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.example.workflow.util.CamundaClmTaskVariables.TASK_SOURCE;

@Component
@RequiredArgsConstructor
public class CallEventCompleted implements JavaDelegate {

    private final ClmTaskService clmTaskService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("SkipTask: " + delegateExecution.getVariable("skipTask"));
        Boolean skipTask = (Boolean) delegateExecution.getVariable("skipTask");
        if (skipTask) {
            System.out.println("CallEventCompleted step skipped as skipTask: " + skipTask);
            return;
        }
        String leadId = delegateExecution.getVariable("lead_id").toString();
        System.out.println("CallEventCompleted \n Closing task call. leadId: " + leadId);

        System.out.println("!!!!!SOURCE" + delegateExecution.getVariable(TASK_SOURCE));
        UUID target = (UUID) delegateExecution.getVariable(TASK_SOURCE);
        clmTaskService.findToDoCallTaskByBusinessKey(
                leadId).ifPresent(task -> clmTaskService.closeTask(task, target, UUID.randomUUID()));
    }

}
