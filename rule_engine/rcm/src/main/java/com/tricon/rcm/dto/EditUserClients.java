package com.tricon.rcm.dto;

import java.util.List;

import lombok.Data;

@Data
public class EditUserClients {

	private String uuid;
	private String role;
	private List<String>companyUuid;
}
