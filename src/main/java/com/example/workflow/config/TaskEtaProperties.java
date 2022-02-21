package com.example.workflow.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TaskEtaProperties {

    @Value("${wkda.task.target.minutes.CALL_BACK_AFTER_MISSED_INBOUND_CALL}")
    private Integer callBackAfterMissedInboundCallTargetMinutes;
    @Value("${wkda.task.target.minutes.CALL_AGAIN_AFTER_UNATTENDED_OUTBOUND_CALL}")
    private Integer callAgainAfterUnattendedOutboundCallTargetMinutes;
    @Value("${wkda.task.target.minutes.EMAIL_FOLLOW_UP_AFTER_SUCCESSFUL_CALL}")
    private Integer emailFollowUpAfterSuccessfulCallTargetMinutes;
    @Value("${wkda.task.target.minutes.EMAIL_AFTER_Y_UNATTENDED_OUTBOUND_CALL}")
    private Integer emailAfterYUnattendedOutboundCallTargetMinutes;
    @Value("${wkda.task.target.minutes.REACT_TO_LAST_INTERACTION}")
    private Integer reactToLastInteractionTargetMinutes;
    @Value("${wkda.task.number.unattended.calls}")
    private Integer maxNumberOfUnattendedCalls;

}
