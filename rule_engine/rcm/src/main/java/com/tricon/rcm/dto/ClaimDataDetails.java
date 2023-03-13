package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class ClaimDataDetails {

	String dateLastUpdated;// This is DOS but not considered-- we have use Current Date as DOS in TP but in
							// Claim we will consider this .
	String status;
	String estSecondary;
	String description;
}
