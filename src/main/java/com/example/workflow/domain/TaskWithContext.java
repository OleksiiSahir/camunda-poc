package com.example.workflow.domain;

import com.example.workflow.domain.task_manager_core.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.io.Serializable;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class TaskWithContext extends Task implements Serializable, Comparable<TaskWithContext>{

    @Column(name = "context", columnDefinition = "json")
    @Type(type = "jsonb")
    private TaskContext context;

    @Override
    public int compareTo(TaskWithContext o) {
        return this.context.getEta().compareTo(o.context.getEta());
    }

}
