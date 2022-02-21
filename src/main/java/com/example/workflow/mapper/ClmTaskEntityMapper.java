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

import static com.example.workflow.util.CamundaClmTaskVariables.*;

@Component
@Primary
public class ClmTaskEntityMapper implements TaskEntityMapper<TaskContext> {

    @Override
    public TaskContext map(DelegateExecution delegateExecution) {
        TaskContext taskContext = new TaskContext();
        taskContext.setUuid(UUID.randomUUID());
        OffsetDateTime createdAt = OffsetDateTime.now();
        taskContext.setCreatedAt(createdAt);
//        taskContext.setCreatedBy((UUID) delegateExecution.getVariable(INTERACTION_CREATED_BY));
        Optional.ofNullable(delegateExecution.getVariable(INTERACTION_CREATED_BY))
                .map(el -> (UUID) el)
                .ifPresent(taskContext::setCreatedBy);
        Optional.ofNullable(delegateExecution.getVariable(ASSIGNED_TO))
                .map(el -> (UUID) el)
                .ifPresent(taskContext::setAssignedTo);
        taskContext.setStatus(Status.TO_DO.name());
        taskContext.setBusinessKey(delegateExecution.getVariable(LEAD_ID).toString());
        taskContext.setProcessName(((ExecutionEntity) delegateExecution).getProcessDefinition().getKey());
        taskContext.setType((Type) delegateExecution.getVariable(TYPE));
        taskContext.setSubType((SubType) delegateExecution.getVariable(SUB_TYPE));
        taskContext.setProcessInstanceId(delegateExecution.getProcessInstanceId());
        taskContext.setTaskDefinitionKey(delegateExecution.getProcessDefinitionId());
        taskContext.setActivityInstanceId(delegateExecution.getActivityInstanceId());
        taskContext.setVariables(delegateExecution.getVariables());
//        taskContext.setContext();
        taskContext.setOnTime(true);
//        taskContext.setSource((UUID) delegateExecution.getVariable(SOURCE));
        taskContext.setSource(UUID.randomUUID());
        Optional.ofNullable(delegateExecution.getVariable(TASK_SOURCE))
                .map(el -> (UUID) el)
                .ifPresent(taskContext::setSource);
        OffsetDateTime eta = createdAt.plusMinutes((Integer) delegateExecution.getVariable(TARGET_MINUTES));
        taskContext.setEta(eta);
        Optional.ofNullable(delegateExecution.getVariable(COUNT))
                .map(el -> (Integer) el)
                .ifPresent(taskContext::setUnattendedCallsCount);


        delegateExecution.setVariable("expiryDate", eta.toInstant().toString());

        return taskContext;
    }

}
