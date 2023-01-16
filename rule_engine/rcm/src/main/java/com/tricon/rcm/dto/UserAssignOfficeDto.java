package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAssignOfficeDto {

	private String userId;
	private List<String>officeId;
}
