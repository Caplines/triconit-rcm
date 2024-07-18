package com.tricon.rcm.pdfDto;

import java.util.List;

import lombok.Data;

@Data
public class CdpPdfDto {

	private List<CdpForAppealDto>cdpForAppeal;
	private List<CdpForFollowUp>cdpForInsuranceFollowUp;
}
