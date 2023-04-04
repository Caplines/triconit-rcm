package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;
@Data
@ToString
@NoArgsConstructor
public class RcmUserDto {

	private String uuid;
	private Integer active;
	private String fullName;
	private String email;
	private String firstName;
	private String lastName;
	private List<Integer> teamNameId;
    private RcmRolesResponseDto roles;
	private List<String> clientName;
}
