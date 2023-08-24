package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class FinalSubmittionClaimAttachmentDto {

	   private String remarks;
	   private int assignToOtherTeamId;
	   private String submitButton;
}
