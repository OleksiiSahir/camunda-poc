package com.example.workflow.mapper;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.domain.TaskWithContext;
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
public class ClmTaskEntityMapper implements TaskEntityMapper<TaskWithContext> {

    @Override
    public TaskWithContext map(DelegateExecution delegateExecution) {
        TaskWithContext taskWithContext = new TaskWithContext();
        taskWithContext.setUuid(UUID.randomUUID());
        OffsetDateTime createdAt = OffsetDateTime.now();
        taskWithContext.setCreatedAt(createdAt);
        Optional.ofNullable(delegateExecution.getVariable(INTERACTION_CREATED_BY))
                .map(el -> (UUID) el)
                .ifPresent(taskWithContext::setCreatedBy);
        Optional.ofNullable(delegateExecution.getVariable(ASSIGNED_TO))
                .map(el -> (UUID) el)
                .ifPresent(taskWithContext::setAssignedTo);
        taskWithContext.setStatus(Status.TO_DO.name());
        taskWithContext.setBusinessKey(delegateExecution.getVariable(LEAD_ID).toString());
        taskWithContext.setProcessName(((ExecutionEntity) delegateExecution).getProcessDefinition().getKey());
        taskWithContext.setProcessInstanceId(delegateExecution.getProcessInstanceId());
        taskWithContext.setTaskDefinitionKey(delegateExecution.getProcessDefinitionId());
        taskWithContext.setActivityInstanceId(delegateExecution.getActivityInstanceId());
        taskWithContext.setVariables(delegateExecution.getVariables());
//        OffsetDateTime eta2 = createdAt.plusMinutes((Integer) delegateExecution.getVariable(TARGET_MINUTES));
        OffsetDateTime eta = createdAt.plusMinutes(1);
        setContext(taskWithContext, delegateExecution, eta);
        delegateExecution.setVariable("expiryDate", eta.toInstant().toString());
        return taskWithContext;
    }

    private void setContext(TaskWithContext taskWithContext, DelegateExecution delegateExecution, OffsetDateTime eta) {
        TaskContext context = new TaskContext();
        context.setType((Type) delegateExecution.getVariable(TYPE));
        context.setSubType((SubType) delegateExecution.getVariable(SUB_TYPE));
        context.setOnTime(true);
        context.setSource(UUID.randomUUID());
        context.setEta(eta);
        Optional.ofNullable(delegateExecution.getVariable(COUNT))
                .map(el -> (Integer) el)
                .ifPresent(context::setUnattendedCallsCount);
        taskWithContext.setContext(context);
    }

}
