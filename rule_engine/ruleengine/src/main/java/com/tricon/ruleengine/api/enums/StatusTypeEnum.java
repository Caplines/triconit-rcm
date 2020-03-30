package com.tricon.ruleengine.api.enums;

public enum StatusTypeEnum {

	
	proposed  ("P","Proposed"),  //calls constructor with value proposed
	complete  ("C","Complete"),  //calls constructor with value complete
	accepted  ("A","Accepted"),  //calls constructor with value accepted
	post_to_walkout  ("W","Post to Walkout"),  //calls constructor with value post_of_walkout
	rejected  ("R","Rejected"),  //calls constructor with value rejected
	referred  ("F","Referred"), // semicolon needed when fields / methods follow
	others  ("Others","Others"); // semicolon needed when fields / methods follow


    private final String type;
    private final String name;

    private StatusTypeEnum(String type,String name) {
        this.type = type;
        this.name = name;
    }
    
    
    public String getType() {
        return this.type;
    }
    
    
    public String getName() {
        return this.name;
    }
    
    
    public static String getNameByType(String name)  {
    	
    	if (name.equalsIgnoreCase("P")) return proposed.getName();
    	else if (name.equalsIgnoreCase("C")) return complete.getName();
    	else if (name.equalsIgnoreCase("A")) return accepted.getName();
    	else if (name.equalsIgnoreCase("W")) return post_to_walkout.getName();
    	else if (name.equalsIgnoreCase("R")) return rejected.getName();
    	else if (name.equalsIgnoreCase("F")) return referred.getName();
    	else if (name.equalsIgnoreCase("Others")) return others.getName();
    	
    	else return "All";
    }

}
