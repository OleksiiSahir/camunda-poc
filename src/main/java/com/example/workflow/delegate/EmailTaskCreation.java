package com.example.workflow.delegate;

import com.example.workflow.domain.TaskWithContext;
import com.example.workflow.mapper.TaskEntityMapper;
import com.example.workflow.repository.AbstractTaskRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
public class EmailTaskCreation extends DbTaskDelegate<TaskWithContext> {

    public EmailTaskCreation(AbstractTaskRepository<TaskWithContext> taskRepository, TaskEntityMapper<TaskWithContext> taskMapper) {
        super(taskRepository, taskMapper);
    }

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("AttendedCall executed");
        if (execution.getVariable("call_status").equals("attended")) {
            execution.setVariable("count", 0);
        }
        super.execute(execution);
    }
}
