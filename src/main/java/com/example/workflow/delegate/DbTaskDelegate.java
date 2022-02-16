package com.example.workflow.delegate;

import com.example.workflow.domain.task_manager_core.Task;
import com.example.workflow.repository.AbstractTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import com.example.workflow.mapper.TaskEntityMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbTaskDelegate<T extends Task> implements JavaDelegate {

    private final AbstractTaskRepository<T> taskRepository;
    private final TaskEntityMapper<T> taskMapper;

    @Override
    public void execute(DelegateExecution execution) {
        final var task = taskMapper.map(execution);
        taskRepository.save(task);
    }

}
