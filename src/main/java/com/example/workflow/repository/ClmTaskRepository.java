package com.example.workflow.repository;

import com.example.workflow.domain.TaskWithContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClmTaskRepository extends AbstractTaskRepository<TaskWithContext> {
    List<TaskWithContext> findAllByBusinessKeyAndStatus(String businessKey, String status);
}
