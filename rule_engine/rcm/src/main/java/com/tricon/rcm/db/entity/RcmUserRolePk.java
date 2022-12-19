package com.tricon.rcm.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class RcmUserRolePk implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1092783614663999544L;

	@Column(name = "uuid", length = 45)
	private String uuid;

	@Column(name = "role", length = 45)
	private String role;

	public RcmUserRolePk() {
	}

}
