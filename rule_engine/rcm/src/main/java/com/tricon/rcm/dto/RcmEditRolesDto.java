package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RcmEditRolesDto {

	private String uuid;
	private List<String>roles;
}
