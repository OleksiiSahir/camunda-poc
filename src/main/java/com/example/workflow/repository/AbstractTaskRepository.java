package com.example.workflow.repository;

import com.example.workflow.domain.task_manager_core.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface AbstractTaskRepository<T extends Task> extends JpaRepository<T, UUID> {
    T findByBusinessKey(String businessKey);

}
