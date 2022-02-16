package com.example.workflow.repository;

import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.Type;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClmTaskRepository extends AbstractTaskRepository<TaskContext> {

    Optional<TaskContext> findByBusinessKeyAndTypeAndStatus(String businessKey, Type type, String status);

    List<TaskContext> findAllByBusinessKeyAndTypeAndStatus(String businessKey, Type type, String status);

}
