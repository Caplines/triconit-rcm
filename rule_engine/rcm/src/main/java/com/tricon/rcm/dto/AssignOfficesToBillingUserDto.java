package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class AssignOfficesToBillingUserDto {

	private List<AssignUserOfficeDto>assignOfficeDetails;	
	private Integer teamId;
}
