package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.repository.ClmTaskRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import com.example.workflow.enums.Status;
import com.example.workflow.enums.Type;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.example.workflow.mapper.ClmTaskEntityMapper.SOURCE;

@Component
@RequiredArgsConstructor
public class CallEventCompleted implements JavaDelegate {

    private final ClmTaskRepository clmTaskRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String leadId = delegateExecution.getVariable("lead_id").toString();
        System.out.println("CallEventCompleted \n Closing task call. leadId: " + leadId);

        System.out.println("!!!!!SOURCE" + delegateExecution.getVariable(SOURCE));
        UUID target = (UUID) delegateExecution.getVariable(SOURCE);
        clmTaskRepository.findByBusinessKeyAndTypeAndStatus(
                leadId,
                Type.CALL,
                Status.TO_DO.name()
        ).ifPresent(task -> closeTask(task, target));
    }

    private void closeTask(TaskContext task, UUID target) {
        task.setStatus(Status.DONE.name());
        task.setClosedAt(OffsetDateTime.now());
        task.setUpdatedAt(OffsetDateTime.now());
        // is this correct ?
        task.setClosedBy(task.getAssignedTo());
        task.setTarget(target);
        clmTaskRepository.saveAndFlush(task);
    }

}
