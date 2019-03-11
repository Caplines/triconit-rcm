package com.tricon.ruleengine.api.enums;

public enum HighLevelReportTypeEnum {
	
	TXPLAN  (1),  //calls constructor with value 1
    BATCH(2),  //calls constructor with value 2
	TXPLAN_NUM  (3),  //calls constructor with value 1
    BATCH_NUM(4)  //calls constructor with value 2
    ; // semicolon needed when fields / methods follow


    private final int type;

    private HighLevelReportTypeEnum(int type) {
        this.type = type;
    }
    
    
    public int getType() {
        return this.type;
    }

}
