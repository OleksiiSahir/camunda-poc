package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.SubType;
import com.example.workflow.enums.Type;
import com.example.workflow.mapper.TaskEntityMapper;
import com.example.workflow.repository.AbstractTaskRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.workflow.mapper.ClmTaskEntityMapper.*;

@Component
public class AttendedCall extends DbTaskDelegate<TaskContext> {

    public AttendedCall(AbstractTaskRepository<TaskContext> taskRepository, TaskEntityMapper<TaskContext> taskMapper) {
        super(taskRepository, taskMapper);
    }

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("AttendedCall executed");
        if (execution.getVariable("status").equals("attended")) {
            execution.setVariable("count", 0);
        }
        execution.setVariable(SUB_TYPE, SubType.EMAIL_AFTER_Y_UNATTENDED_OUTBOUND_CALL);
        execution.setVariable(TYPE, Type.EMAIL);
        execution.setVariable(SOURCE, UUID.randomUUID());
        // create email task
        super.execute(execution);
    }
}
