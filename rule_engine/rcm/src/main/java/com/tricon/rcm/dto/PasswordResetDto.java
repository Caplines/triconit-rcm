package com.tricon.rcm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class PasswordResetDto {

	private String uuid;
	private String password;
}
