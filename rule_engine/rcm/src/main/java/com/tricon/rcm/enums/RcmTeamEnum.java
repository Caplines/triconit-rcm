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
	SYSYEM(1, "SYSTEM","System", new RcmRoleEnum[] { RcmRoleEnum.SYSTEM },true,false,false),
    ADMIN(2, "ADMIN","Admin", new RcmRoleEnum[] { RcmRoleEnum.ADMIN },true,false,false),
	PATIENT_CALLING(3,"PATIENT_CALLING","Patient Calling", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	OFFICE(4, "OFFICE", "Office",new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	INTERNAL_AUDIT(5, "INTERNAL_AUDIT","Internal Audit", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	IV_TEAM(6, "IV_TEAM","IV Team", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	BILLING(7, "BILLING", "Billing",new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	LC3(8, "LC3","LC3", new RcmRoleEnum[] { RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	CLIENT(9, "CLIENT","Client", new RcmRoleEnum[] { RcmRoleEnum.CLIENT_MANAGER,RcmRoleEnum.CLIENT_VIEW_ONLY},false,true,true),
	AGING(10, "AGING","Aging", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	POSTING(11, "POSTING","Posting", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	QUALITY(12, "QUALITY","Quality", new RcmRoleEnum[] {  RcmRoleEnum.TL, RcmRoleEnum.ASSO },true,true,true),
	UPLOAD_CLAIMS(13, "UPLOAD_CLAIMS","Upload Claims", new RcmRoleEnum[] {RcmRoleEnum.UPLOAD_CLAIMS},true,false,false),
	ACCOUNT_MANAGER(14, "ACCOUNT_MANAGER","Account Manager", new RcmRoleEnum[] {RcmRoleEnum.ACCOUNT_MANAGER},true,false,false);
	
	final private int id;
	final private String name;
	final private String description;
	final private RcmRoleEnum[] role;
	final private boolean isSmilepoint;
	final private boolean isRoleVisible;
	final private boolean isTeamMandatory;
	
	private RcmTeamEnum(int id,String name,String description,RcmRoleEnum[] role,boolean isSmilepoint,
			boolean isRoleVisible,boolean isTeamMandatory) {
		this.id = id;
		this.name = name;
		this.description=description;
		this.role = role;
		this.isSmilepoint=isSmilepoint;
		this.isRoleVisible=isRoleVisible;
		this.isTeamMandatory=isTeamMandatory;
	}


	public String getDescription() {
		return description;
	}


	public boolean isRoleVisible() {
		return isRoleVisible;
	}


	public boolean isSmilepoint() {
		return isSmilepoint;
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

	


	

	



	public boolean isTeamMandatory() {
		return isTeamMandatory;
	}


	public static String generateRole(int value, String roleType) {
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == value).findFirst();
		Optional<RcmRoleEnum> roleEnum = Arrays.stream(RcmRoleEnum.values())
				.filter(x -> x.getName().equals(roleType)).findFirst();
		String teamName = "", roleName = "", role = "";
		if (teamEnum.isPresent() && roleEnum.isPresent()) {
			teamName = teamEnum.get().getName();
			roleName = roleEnum.get().getName();
			role = Constants.ROLE_PREFIX.concat(teamName).concat(Constants.HYPHEN).concat(roleName);
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
	
	public static List<RcmRolesResponseDto> getRolesByTeamId(int teamId) {
		RcmRoleEnum rolesEnum[] = null;
		List<RcmRolesResponseDto> roles = new ArrayList<>();
		Optional<RcmTeamEnum> teamEnum = Arrays.stream(values()).filter(x -> x.getId() == teamId).findFirst();
		RcmRolesResponseDto rolesResponseDto = null;
		if (teamEnum.isPresent()) {
			rolesEnum = teamEnum.get().getRole();
			int id = teamEnum.get().getId();
			for (RcmRoleEnum data : rolesEnum) {
				rolesResponseDto = new RcmRolesResponseDto();
				rolesResponseDto.setRoleId(data.getName());
				rolesResponseDto.setRoleName(data.getFullName());
				rolesResponseDto.setFullRoleName(generateRole(id, data.getName()));
				roles.add(rolesResponseDto);
			}
			return roles;
		}
		if (teamId == -1) {
			RcmRolesResponseDto rolesResponseForAdmin = new RcmRolesResponseDto();
			rolesResponseForAdmin.setRoleId(RcmRoleEnum.ADMIN.getName());
			rolesResponseForAdmin.setRoleName(RcmRoleEnum.ADMIN.getFullName());
			rolesResponseForAdmin.setFullRoleName(generateRole(0, RcmRoleEnum.ADMIN.getName()));
			roles.add(rolesResponseForAdmin);
			RcmRolesResponseDto rolesResponseForUploadClaims = new RcmRolesResponseDto();
			rolesResponseForUploadClaims.setRoleId(RcmRoleEnum.UPLOAD_CLAIMS.getName());
			rolesResponseForUploadClaims.setRoleName(RcmRoleEnum.UPLOAD_CLAIMS.getFullName());
			rolesResponseForUploadClaims.setFullRoleName(generateRole(0, RcmRoleEnum.UPLOAD_CLAIMS.getName()));
			roles.add(rolesResponseForUploadClaims);
			return roles;
		}
		return null;
	}
}
