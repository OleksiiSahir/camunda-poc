package com.example.workflow.delegate;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.SubType;
import com.example.workflow.mapper.TaskEntityMapper;
import com.example.workflow.repository.AbstractTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import com.example.workflow.enums.Type;

import java.util.UUID;

import static com.example.workflow.mapper.ClmTaskEntityMapper.SUB_TYPE;
import static com.example.workflow.mapper.ClmTaskEntityMapper.TYPE;
import static com.example.workflow.mapper.ClmTaskEntityMapper.SOURCE;

@Component
@Slf4j
public class CallTaskCreation extends DbTaskDelegate<TaskContext> {

    public CallTaskCreation(AbstractTaskRepository<TaskContext> taskRepository, TaskEntityMapper<TaskContext> taskMapper) {
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
        execution.setVariable(SUB_TYPE, SubType.CALL_AGAIN_AFTER_UNATTENDED_OUTBOUND_CALL);
        execution.setVariable(TYPE, Type.CALL);
        execution.setVariable(SOURCE, UUID.randomUUID());
        super.execute(execution);
    }

}
