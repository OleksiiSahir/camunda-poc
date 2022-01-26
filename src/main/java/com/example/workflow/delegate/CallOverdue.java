package com.example.workflow.delegate;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class CallOverdue implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if (execution.getVariable("status").equals("attended")) {
            execution.setVariable("count", 0);
        }

        System.out.println("CallOverdue \n publishing event call overdue");
    }
}
