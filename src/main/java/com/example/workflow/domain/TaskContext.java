package com.example.workflow.domain;

import com.example.workflow.domain.task_manager_core.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.example.workflow.enums.Type;
import com.example.workflow.enums.SubType;
import com.example.workflow.enums.SkippedReason;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class TaskContext extends Task implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_type")
    private SubType subType;

    @Column(name = "on_time")
    private Boolean onTime;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID source;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID target;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Column(name = "closed_by")
    private UUID closedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "skipped_reason")
    private SkippedReason skippedReason;

    @Column(name = "skipped_comment")
    private String skippedComment;

    private OffsetDateTime eta;

    @Column(name = "unattended_calls_count")
    private Integer unattendedCallsCount;
}
