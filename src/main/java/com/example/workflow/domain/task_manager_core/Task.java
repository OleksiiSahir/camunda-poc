package com.example.workflow.domain.task_manager_core;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task", schema = "clm_task")
@EntityListeners(AuditingEntityListener.class)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Task {

    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "pg-uuid")
    private UUID uuid;

    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Type(type = "pg-uuid")
    private UUID createdBy;

    @LastModifiedBy
    @Type(type = "pg-uuid")
    private UUID updatedBy;

    @Column(name = "assigned_to")
    @Type(type = "pg-uuid")
    private UUID assignedTo;

    @Column(name = "status")
    private String status;

    @Column(name = "business_key")
    private String businessKey;

    @Column(name = "process_name")
    private String processName;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Column(name = "task_definition_key")
    private String taskDefinitionKey;

    @Column(name = "activity_instance_id")
    private String activityInstanceId;

    @Column(
            name = "closed_at"
    )
    private OffsetDateTime closedAt;

    @Column(
            name = "closed_by"
    )
    private UUID closedBy;

    @Column(name = "variables", columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<String, Object> variables;

}

