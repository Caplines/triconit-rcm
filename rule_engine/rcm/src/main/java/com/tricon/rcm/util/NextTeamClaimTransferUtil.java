package com.tricon.rcm.util;

import com.tricon.rcm.dto.RcmTeamDto;
import com.tricon.rcm.enums.RcmTeamEnum;

public class NextTeamClaimTransferUtil {
	
	
	public static RcmTeamDto getNextTeam(int currentTeamId) {
		RcmTeamDto dto=null;
		if(currentTeamId==RcmTeamEnum.BILLING.getId()) {
			dto=new RcmTeamDto();
			dto.setTeamId(RcmTeamEnum.AR.getId());
			dto.setTeamName(RcmTeamEnum.AR.getDescription());//aging
		}
		return dto;
	}

}
