package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class RcmUserDto {

	private String uuid;
	private Integer active;
	private String fullName;
	private String email;
	private Integer teamNameId;
	private String firstName;
	private String lastName;

}
