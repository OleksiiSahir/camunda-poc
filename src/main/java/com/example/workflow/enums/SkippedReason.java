package com.example.workflow.enums;

public enum SkippedReason {
    DONE_ALREADY,//I’ve done this task already
    COLLEAGUE_DID,//A colleague of mine did this task
    NO_NEED_PERFORM,//No need to perform this task at this stage
    PHONE_NUMBER_IS_INVALID,//The phone number is invalid
    EMAIL_IS_INVALID, //The email address is invalid
    TECHNICAL_ISSUE, //I cannot perform this task due to a technical issue
    INBOUND_CALL,
    DUPLICATE, //This task is a duplicate
    ARCHIVED_LEAD,
    OTHER
}
