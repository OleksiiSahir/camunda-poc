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

@Component
@RequiredArgsConstructor
public class EmailOverdue implements JavaDelegate {

    private final ClmTaskRepository clmTaskRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String leadId = execution.getVariable("lead_id").toString();
        System.out.println("EmailOverdue \n Setting onTime false. leadId: " + leadId);
        clmTaskRepository.findByBusinessKeyAndTypeAndStatus(
                leadId,
                Type.EMAIL,
                Status.TO_DO.name()
        ).ifPresent(this::overdueTask);
    }


    private void overdueTask(TaskContext task) {
        task.setOnTime(false);
        task.setUpdatedAt(OffsetDateTime.now());
        clmTaskRepository.saveAndFlush(task);
    }
}
