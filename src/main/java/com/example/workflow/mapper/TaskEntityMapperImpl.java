package com.example.workflow.mapper;

import com.example.workflow.domain.task_manager_core.Task;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class TaskEntityMapperImpl implements TaskEntityMapper<Task> {

    private static final String STATUS = "status";
    private static final String LEAD_ID = "lead_id";

    @Override
    public Task map(DelegateExecution execution) {
        return Task.builder()
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .status(execution.getVariable(STATUS).toString())
                .businessKey(execution.getVariable(LEAD_ID).toString())
                .processName(execution.getEventName())
                .processInstanceId(execution.getProcessInstance().getId())
                .activityInstanceId(execution.getActivityInstanceId())
                .variables(execution.getVariables())
                .context(Map.of())
                .build();
    }

}
