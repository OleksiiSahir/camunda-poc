package com.example.workflow.service;

import com.example.workflow.repository.ClmTaskRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;
import com.example.workflow.enums.Type;
import com.example.workflow.enums.Status;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClmTaskService {

    private final ClmTaskRepository clmTaskRepository;
    private final RuntimeService runtimeService;

    public boolean isCallTaskExists(UUID leadUuid) {
        return clmTaskRepository.findByBusinessKeyAndTypeAndStatus(
                leadUuid.toString(),
                Type.CALL,
                Status.TO_DO.name()
        ).isPresent();
    }

    public void sendCamundaIncomingEvent(Map<String, Object> params) {
        String leadId = params.get("lead_id").toString();
        System.out.println("********* Start event for lead id " + leadId + "***********");
        runtimeService.createConditionEvaluation()
                .processInstanceBusinessKey(leadId)
                .setVariables(params)
                .evaluateStartConditions();
    }

}
