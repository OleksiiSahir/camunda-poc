package com.example.workflow.mapper;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.Status;
import com.example.workflow.enums.SubType;
import com.example.workflow.enums.Type;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@Primary
public class ClmTaskEntityMapper implements TaskEntityMapper<TaskContext> {

    public static final String LEAD_ID = "lead_id";
    public static final String SUB_TYPE = "sub_type";
    public static final String SOURCE = "sourceC";
    public static final String TYPE = "type";
    public static final String ASSIGNED_TO = "assigned_to";
    public static final String COUNT = "count";
    private final long MINUTES = 10;

    @Override
    public TaskContext map(DelegateExecution delegateExecution) {
        TaskContext taskContext = new TaskContext();
        taskContext.setUuid(UUID.randomUUID());
        taskContext.setProcessInstanceId(delegateExecution.getProcessInstanceId());
        taskContext.setActivityInstanceId(delegateExecution.getActivityInstanceId());
        taskContext.setProcessName(((ExecutionEntity) delegateExecution).getProcessDefinition().getKey());
        taskContext.setTaskDefinitionKey(delegateExecution.getProcessDefinitionId());
        taskContext.setVariables(delegateExecution.getVariables());
        taskContext.setStatus(Status.TO_DO.name());
        taskContext.setBusinessKey(delegateExecution.getVariable(LEAD_ID).toString());
        taskContext.setSource((UUID) delegateExecution.getVariable(SOURCE));
        taskContext.setSubType((SubType) delegateExecution.getVariable(SUB_TYPE));
        taskContext.setOnTime(true);
        OffsetDateTime createdAt = OffsetDateTime.now();
        taskContext.setCreatedAt(createdAt);
        taskContext.setEta(createdAt.plusMinutes(MINUTES));
        taskContext.setType((Type) delegateExecution.getVariable(TYPE));
//        taskContext.setAssignedTo((UUID) delegateExecution.getVariable(ASSIGNED_TO));
        taskContext.setAssignedTo(UUID.randomUUID());

        Optional.ofNullable(delegateExecution.getVariable(ASSIGNED_TO))
                .map(el -> (UUID) el)
                .ifPresent(taskContext::setAssignedTo);

        Optional.ofNullable(delegateExecution.getVariable(COUNT))
                .map(el -> (Integer) el)
                .ifPresent(taskContext::setUnattendedCallsCount);
        return taskContext;
    }

}
