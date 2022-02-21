package com.example.workflow.service;

import com.example.workflow.config.TaskEtaProperties;
import com.example.workflow.domain.TaskContext;
import com.example.workflow.enums.SkippedReason;
import com.example.workflow.enums.SubType;
import com.example.workflow.repository.ClmTaskRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;
import com.example.workflow.enums.Type;
import com.example.workflow.enums.Status;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.workflow.util.CamundaClmTaskVariables.*;

@Service
@RequiredArgsConstructor
public class ClmTaskService {

    private final ClmTaskRepository clmTaskRepository;
    private final RuntimeService runtimeService;
    //    private final CustomerLeadAssignmentApi customerLeadAssignmentApi;
//    private final InteractionApi interactionApi;
    private final TaskEtaProperties taskEtaProperties;

    public void execute(Map<String, Object> payload) {
        String customerLeadUuid = (String) payload.get(LEAD_ID);
        String interactionChannel = (String) payload.get("interaction_channel");
        payload.put(EVENT_SOURCE, CLM);
        payload.put("skipTask", false);
//        InteractionDTO interaction = interactionApi.getInteraction(payload.getInteractionUuid());
//        UUID interactionCreatedBy = interaction.getCreatedBy();
//          Example of request
//        {
//            "lead_id" : "cdacd28d-559b-4f7b-8a6c-74c7941b5b3e",
//            "event_source" : "clm",
//            "event_type" : "call",
//            "call_status" : "unattended",
//            "expiryDate": "2022-02-17T17:10:39.569523Z",
//            "interaction_channel": "MISSED_PHONE_CALL",
//            "skipTask": false
//        }

        if ("MISSED_PHONE_CALL".equalsIgnoreCase(interactionChannel)) {
            if (!isToDoCallTaskExists(customerLeadUuid)) {
                System.out.println("TEST: Executing MISSED_PHONE_CALL flow");
                payload.put(SUB_TYPE, SubType.CALL_BACK_AFTER_MISSED_INBOUND_CALL);
//                customerLeadAssignmentApi.getAssignmentsByCustomerLeadUuid(customerLeadUuid).stream()
//                        .filter(Objects::nonNull)
//                        .findFirst()
//                        .ifPresent(leadAssignment -> params.put(ASSIGNED_TO, leadAssignment.getAssignedTo()));
//                params.put(SOURCE, payload.getInteractionUuid());
                payload.put(LEAD_ID, customerLeadUuid);
                payload.put(TYPE, Type.CALL);
                payload.put(EVENT_TYPE, "call");
                payload.put(CALL_STATUS, "missed");
//                params.put(INTERACTION_CREATED_BY, interactionCreatedBy);
                payload.put(TARGET_MINUTES, taskEtaProperties.getCallBackAfterMissedInboundCallTargetMinutes());
                sendCamundaIncomingEvent(payload);
            }
        }

        if ("UNATTENDED_OUTBOUND_CALL".equalsIgnoreCase(interactionChannel)) {
            payload.put(EVENT_TYPE, "call");
            payload.put(LEAD_ID, customerLeadUuid);
            payload.put(CALL_STATUS, "unattended");
            payload.put(SUB_TYPE, SubType.CALL_AGAIN_AFTER_UNATTENDED_OUTBOUND_CALL);
            payload.put(TYPE, Type.CALL);
            payload.put(TARGET_MINUTES, taskEtaProperties.getCallAgainAfterUnattendedOutboundCallTargetMinutes());

            findToDoCallTaskByBusinessKey(customerLeadUuid)
                    .filter(task -> task.getUnattendedCallsCount() >= taskEtaProperties.getMaxNumberOfUnattendedCalls())
                    .ifPresent(task -> {
                        payload.put(SUB_TYPE, SubType.EMAIL_AFTER_Y_UNATTENDED_OUTBOUND_CALL);
                        payload.put(TYPE, Type.EMAIL);
                        payload.put(TARGET_MINUTES, taskEtaProperties.getEmailAfterYUnattendedOutboundCallTargetMinutes());
                    });
            sendCamundaIncomingEvent(payload);
        }

        if ("ATTENDED_OUTBOUND_CALL".equalsIgnoreCase(interactionChannel)
                || "OUTBOUND_PHONE".equalsIgnoreCase(interactionChannel)) {
            System.out.println("TEST: Executing ATTENDED_OUTBOUND_CALL flow");
            payload.put(SUB_TYPE, SubType.EMAIL_FOLLOW_UP_AFTER_SUCCESSFUL_CALL);
            payload.put(TARGET_MINUTES, taskEtaProperties.getEmailFollowUpAfterSuccessfulCallTargetMinutes());
            payload.put(LEAD_ID, customerLeadUuid);
            payload.put(EVENT_TYPE, "call");
            payload.put(TYPE, Type.EMAIL);
            payload.put(CALL_STATUS, "attended");
            sendCamundaIncomingEvent(payload);
        }

        if ("INBOUND_PHONE".equalsIgnoreCase(interactionChannel) || "INBOUND_CALL".equalsIgnoreCase(interactionChannel)) {
            payload.put(EVENT_TYPE, "call");
            payload.put(LEAD_ID, customerLeadUuid);
            payload.put("skip_task", true);
            sendCamundaIncomingEvent(payload);
        }

        if ("OUTBOUND_EMAIL".equalsIgnoreCase(interactionChannel)) {
            payload.put(LEAD_ID, customerLeadUuid);
            payload.put(EVENT_TYPE, "email");
            sendCamundaIncomingEvent(payload);
        }
    }

    public boolean isToDoCallTaskExists(String leadUuid) {
        return findToDoCallTaskByBusinessKey(leadUuid).isPresent();
    }

    public Optional<TaskContext> findToDoCallTaskByBusinessKey(String leadUuid) {
        return clmTaskRepository.findByBusinessKeyAndTypeAndStatus(
                leadUuid,
                Type.CALL,
                Status.TO_DO.name()
        );
    }

    public Optional<TaskContext> findToDoEmailTaskByBusinessKey(String leadUuid) {
        return clmTaskRepository.findByBusinessKeyAndTypeAndStatus(
                leadUuid,
                Type.EMAIL,
                Status.TO_DO.name()
        );
    }

    public Optional<TaskContext> getClosestTimeToEtaEmailTask(String leadUuid) {
        List<TaskContext> tasks = findAllToDoEmailTasksByBusinessKey(leadUuid);
        if (!tasks.isEmpty()) {
            return Optional.of(Collections.min(tasks));
        }
        return Optional.empty();
    }

    public void sendCamundaIncomingEvent(Map<String, Object> params) {
        String leadId = params.get("lead_id").toString();
        System.out.println("********* Start event for lead id " + leadId + "***********");
        runtimeService.createConditionEvaluation()
                .processInstanceBusinessKey(leadId)
                .setVariables(params)
                .evaluateStartConditions();
    }

    public void closeTask(TaskContext task, UUID interactionUuid, UUID interactionCreatedBy) {
        task.setStatus(Status.DONE.name());
        task.setClosedAt(OffsetDateTime.now());
        task.setUpdatedAt(OffsetDateTime.now());
        task.setUpdatedBy(interactionCreatedBy);
        task.setClosedBy(interactionCreatedBy);
        task.setTarget(interactionUuid);
        clmTaskRepository.saveAndFlush(task);
    }

    public void overdueTask(TaskContext task) {
        task.setOnTime(false);
        task.setUpdatedAt(OffsetDateTime.now());
        clmTaskRepository.saveAndFlush(task);
    }

    public void skipTask(TaskContext task, UUID interactionUuid, UUID interactionCreatedBy) {
        task.setStatus(Status.SKIPPED.name());
        task.setSkippedReason(SkippedReason.INBOUND_CALL);
        task.setClosedBy(interactionCreatedBy);
        task.setClosedAt(OffsetDateTime.now());
        task.setUpdatedAt(OffsetDateTime.now());
        task.setUpdatedBy(interactionCreatedBy);
        task.setTarget(interactionUuid);
        clmTaskRepository.saveAndFlush(task);
    }

    private List<TaskContext> findAllToDoEmailTasksByBusinessKey(String leadUuid) {
        return clmTaskRepository.findAllByBusinessKeyAndTypeAndStatus(
                leadUuid,
                Type.EMAIL,
                Status.TO_DO.name()
        );
    }

}
