package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class EditUserTeams {

	private String uuid;
	private String role;
	private List<Integer>teamId;
}
