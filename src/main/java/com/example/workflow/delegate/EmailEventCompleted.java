package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.Status;
import com.example.workflow.enums.Type;
import com.example.workflow.repository.ClmTaskRepository;
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

    private final ClmTaskRepository clmTaskRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Boolean skipTask = (Boolean) delegateExecution.getVariable("skipTask");
        if (skipTask) {
            System.out.println("EmailEventCompleted step skipped as skipTask: " + skipTask);
            return;
        }
        String leadId = delegateExecution.getVariable("lead_id").toString();
        System.out.println("EmailEventCompleted \n Closing task email. leadId: " + leadId);

        System.out.println("!!!!!SOURCE" + delegateExecution.getVariable(TASK_SOURCE));
        UUID target = (UUID) delegateExecution.getVariable(TASK_SOURCE);
        List<TaskContext> tasks = clmTaskRepository.findAllByBusinessKeyAndTypeAndStatus(
                leadId,
                Type.EMAIL,
                Status.TO_DO.name()
        );

        if (!tasks.isEmpty()) {
            TaskContext oldestTask = Collections.min(tasks);
            closeTask(oldestTask, target);
        }
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
