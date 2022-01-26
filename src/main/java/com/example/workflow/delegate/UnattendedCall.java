package com.example.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class UnattendedCall implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Object count = execution.getVariable("count");
        if (count == null) {
            execution.setVariable("count", 1);
        } else {
            execution.setVariable("count", ((int) count) + 1);
        }

        System.out.println("UnattendedCall \n publishing event create task call");
    }
}
