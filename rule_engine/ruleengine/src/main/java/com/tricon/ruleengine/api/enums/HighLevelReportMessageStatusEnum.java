package com.tricon.ruleengine.api.enums;

public enum HighLevelReportMessageStatusEnum {
	
	FAIL  (1),  //calls constructor with value 1
    PASS(2),  //calls constructor with value 2
    ALERT(3)
    ; // semicolon needed when fields / methods follow


    private final int status;

    private HighLevelReportMessageStatusEnum(int status) {
        this.status = status;
    }
    
    
    public int getStatus() {
        return this.status;
    }


}
