package com.tricon.esdatareplication.dto;

import lombok.Data;

public @Data class PropFileDto {

	private String esDbuser;
	private String esDbPass;
	/*private String reDBuser;
	private String reDBPass;*/
	private String timer;
	private String logLocation;
	private String esVersion;
	
	
	
}
