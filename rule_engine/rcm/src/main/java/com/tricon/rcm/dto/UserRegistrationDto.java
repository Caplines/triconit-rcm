package com.tricon.rcm.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserRegistrationDto {

	@NotEmpty(message = "Please provide your first name")
	private String firstName;
	@NotEmpty(message = "Please provide your last name")
	private String lastName;
	@Email(message = "Please provide a valid Email")
	@NotEmpty(message = "Please provide an email")
	private String email;
	@NotEmpty(message = "Please provide your password")
	private String password;
	private String officeId;
	private Integer teamId;
	private List<String> userRole;
	private String companyName;
}
