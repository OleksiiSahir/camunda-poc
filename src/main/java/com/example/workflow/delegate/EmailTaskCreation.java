package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.SubType;
import com.example.workflow.enums.Type;
import com.example.workflow.mapper.TaskEntityMapper;
import com.example.workflow.repository.AbstractTaskRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.workflow.util.CamundaClmTaskVariables.*;

@Component
public class EmailTaskCreation extends DbTaskDelegate<TaskContext> {

    public EmailTaskCreation(AbstractTaskRepository<TaskContext> taskRepository, TaskEntityMapper<TaskContext> taskMapper) {
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
