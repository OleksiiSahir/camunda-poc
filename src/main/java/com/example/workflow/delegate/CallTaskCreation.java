package com.example.workflow.delegate;

import com.example.workflow.domain.TaskWithContext;
import com.example.workflow.mapper.TaskEntityMapper;
import com.example.workflow.repository.AbstractTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CallTaskCreation extends DbTaskDelegate<TaskWithContext> {

    public CallTaskCreation(AbstractTaskRepository<TaskWithContext> taskRepository, TaskEntityMapper<TaskWithContext> taskMapper) {
        super(taskRepository, taskMapper);
    }

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("CallTaskCreation");
        Object count = execution.getVariable("count");
        if (count == null) {
            execution.setVariable("count", 1);
        } else {
            execution.setVariable("count", ((int) count) + 1);
        }
        super.execute(execution);
    }

}
