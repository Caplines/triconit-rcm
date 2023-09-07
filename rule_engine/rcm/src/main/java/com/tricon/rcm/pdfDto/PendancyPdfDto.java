package com.tricon.rcm.pdfDto;

import lombok.Data;

@Data
public class PendancyPdfDto {
	private String officeUuid;
	private String officeName;
	private int count;
	private String opdtd;
	private String opdosd;
	private String fname;
	private String lname;
	private String assignedUser;
	private int remoteLiteRejections;
	private String companyName;
}
