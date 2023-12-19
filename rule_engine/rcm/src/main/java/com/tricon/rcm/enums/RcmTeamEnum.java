package com.tricon.rcm.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.tricon.rcm.dto.RcmRolesResponseDto;
import com.tricon.rcm.util.Constants;

public enum RcmTeamEnum {

	/*
	 * Make sure Same is present in rcm_team Table
	 */
	SYSYEM(1, "SYSTEM","System", new RcmRoleEnum[] { RcmRoleEnum.SYSTEM },false),
    ADMIN(2, "ADMIN","Admin", new RcmRoleEnum[] { RcmRoleEnum.ADMIN },false),
	INTERNAL_AUDIT(3, "INTERNAL_AUDIT","Internal Audit", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true),
	LC3(4, "LC3","LC3", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true),
	OFFICE(5, "OFFICE","Office", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true),
	PATIENT_CALLING(6, "PATIENT_CALLING","Patient Calling", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true),
	BILLING(7, "BILLING", "Billing",new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true),	
	SUPER_ADMIN(8, "SUPER_ADMIN","Super Admin", new RcmRoleEnum[] {RcmRoleEnum.SUPER_ADMIN},false),
	REPORTING(9,"REPORTING","Reporting",new RcmRoleEnum[] {RcmRoleEnum.REPORTING},false),
	ORTHO(10,"ORTHO","Ortho",new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	CDP(11,"CDP","CDP",new RcmRoleEnum[] {RcmRoleEnum.TL ,RcmRoleEnum.ASSO},true),
	PAYMENT_POSTING(12,"PAYMENT_POSTING","Payment Posting",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	PPO_IV(13,"PPO_IV","PPO IV",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	MEDICAID_IV(14,"MEDICAID_IV","Medicaid IV",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	NEED_TO_HOLD(15,"NEED_TO_HOLD","Need to hold",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	QUALITY(16,"QUALITY","Quality",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	AR(17,"AR","AR",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},false),
	PATIENT_STATEMENT(18,"PATIENT_STATEMENT","Patient Statement",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	CREDENTIALING(19,"CREDENTIALING","Credentialing",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true),
	AGING(20,"AGING","Aging",new RcmRoleEnum[] {RcmRoleEnum.TL, RcmRoleEnum.ASSO},true);
	
	final private int id;
	final private String name;
	final private String description;
	final private RcmRoleEnum[] role;
	final private boolean isRoleVisible;
	
	private RcmTeamEnum(int id,String name,String description,RcmRoleEnum[] role,
			boolean isRoleVisible) {
		this.id = id;
		this.name = name;
		this.description=description;
		this.role = role;
		this.isRoleVisible=isRoleVisible;
	}


	public String getDescription() {
		return description;
	}


	public boolean isRoleVisible() {
		return isRoleVisible;
	}


	public int getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public RcmRoleEnum[] getRole() {
		return role;
	}


	public static String generateRole(int value, String roleType) {
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == value).findFirst();
		Optional<RcmRoleEnum> roleEnum = Arrays.stream(RcmRoleEnum.values())
				.filter(x -> x.getName().equals(roleType)).findFirst();
		String roleName = "", role = "";
		if (teamEnum.isPresent() && roleEnum.isPresent()) {
			//teamName = teamEnum.get().getName();
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(roleName);
			return role;
		}
		if(value==0 &&roleEnum.isPresent()) {
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(roleName);
			return role;
		}
		return null;
	}
	
	public static int validateTeamId(int teamId)
	{
		int id=0;
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() ==teamId).findFirst();
		if(teamEnum.isPresent()) {
			 id=teamEnum.get().getId();
			 return id;
		}
		return 0;
	}
	
	public static String getTeamNameByTeamId(int teamId)
	{
		String teamName="";
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() ==teamId).findFirst();
		if(teamEnum.isPresent()) {
			 teamName=teamEnum.get().getName();
			 return teamName;
		}
		return null;
	}
	
//	public static List<RcmRolesResponseDto> getRolesByTeamId(int teamId) {
//		RcmRoleEnum rolesEnum[] = null;
//		List<RcmRolesResponseDto> roles = new ArrayList<>();
//		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == teamId).findFirst();
//		RcmRolesResponseDto rolesResponseDto = null;
//		if (teamEnum.isPresent()) {
//			rolesEnum = teamEnum.get().getRole();
//			int id = teamEnum.get().getId();
//			for (RcmRoleEnum data : rolesEnum) {
//				rolesResponseDto = new RcmRolesResponseDto();
//				rolesResponseDto.setRoleId(data.getName());
//				rolesResponseDto.setRoleName(data.getFullName());
//				//rolesResponseDto.setFullRoleName(generateRole(id, data.getName()));
//				roles.add(rolesResponseDto);
//			}
//			return roles;
//		}
//		if (teamId == -1) {
//			RcmRolesResponseDto rolesResponseForAdmin = new RcmRolesResponseDto();
//			rolesResponseForAdmin.setRoleId(RcmRoleEnum.ADMIN.getName());
//			rolesResponseForAdmin.setRoleName(RcmRoleEnum.ADMIN.getFullName());
//			//rolesResponseForAdmin.setFullRoleName(generateRole(0, RcmRoleEnum.ADMIN.getName()));
//			roles.add(rolesResponseForAdmin);
////			RcmRolesResponseDto rolesResponseForUploadClaims = new RcmRolesResponseDto();
////			rolesResponseForUploadClaims.setRoleId(RcmRoleEnum.UPLOAD_CLAIMS.getName());
////			rolesResponseForUploadClaims.setRoleName(RcmRoleEnum.UPLOAD_CLAIMS.getFullName());
////			rolesResponseForUploadClaims.setFullRoleName(generateRole(0, RcmRoleEnum.UPLOAD_CLAIMS.getName()));
////			roles.add(rolesResponseForUploadClaims);
////			RcmRolesResponseDto rolesResponseForAccountManager = new RcmRolesResponseDto();
////			rolesResponseForAccountManager.setRoleId(RcmRoleEnum.ACCOUNT_MANAGER.getName());
////			rolesResponseForAccountManager.setRoleName(RcmRoleEnum.ACCOUNT_MANAGER.getFullName());
////			rolesResponseForAccountManager.setFullRoleName(generateRole(0, RcmRoleEnum.ACCOUNT_MANAGER.getName()));
////			roles.add(rolesResponseForUploadClaims);
//			return roles;
//		}
//		return null;
//	}
	
	public static String generateRoleByRoleType(String roleType) {
		Optional<RcmRoleEnum> roleEnum = Arrays.stream(RcmRoleEnum.values())
				.filter(x -> x.getName().equals(roleType)).findFirst();
		String roleName = "", role = "";
		if (roleEnum.isPresent()) {
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(roleName);
			return role;
		}
		return null;
	}
	
	public static List<Integer> getAllTeamsIdIsRoleVisible() {

		List<Integer> teamId = new ArrayList<>();
		for (RcmTeamEnum team : RcmTeamEnum.values()) {
			if (team.isRoleVisible) {
				teamId.add(team.getId());
			}
		}
		return teamId;
	}
	
	public static String getTeamDescriptionByTeamId(int teamId)
	{
		String teamDescription="";
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() ==teamId).findFirst();
		if(teamEnum.isPresent()) {
			teamDescription=teamEnum.get().getDescription();
			 return teamDescription;
		}
		return null;
	}
	
	public static int validateTeamIdWithRoleVisible(int teamId) {
		int id = 0;
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == teamId).findFirst();
		if (teamEnum.isPresent()) {
			if (teamEnum.get().isRoleVisible) {
				id = teamEnum.get().getId();
				return id;
			}
		}
		return 0;
	}
}
