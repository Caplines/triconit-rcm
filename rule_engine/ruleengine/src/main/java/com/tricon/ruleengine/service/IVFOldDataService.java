package com.tricon.ruleengine.service;

import com.tricon.ruleengine.dto.IVFDumpDto;
import com.tricon.ruleengine.model.db.IVFormType;

public interface IVFOldDataService {
	
	public String dumpOldData(IVFDumpDto dto,IVFormType iVFormType);
	
	public String dumpOLDataOrtho(IVFormType iVFormType);

}
