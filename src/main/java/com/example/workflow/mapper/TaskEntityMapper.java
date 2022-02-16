package com.example.workflow.mapper;

import com.example.workflow.domain.task_manager_core.Task;
import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface TaskEntityMapper<T extends Task> {
    T map(DelegateExecution execution);

}
