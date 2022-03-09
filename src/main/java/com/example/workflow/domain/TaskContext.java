package com.example.workflow.domain;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import com.example.workflow.enums.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TaskContext {
    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private SubType subType;

    private Boolean onTime;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID source;

    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID target;

    @Enumerated(EnumType.STRING)
    private SkippedReason skippedReason;

    private String skippedComment;

    private OffsetDateTime eta;

    private Integer unattendedCallsCount;
}
