package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public  class UserRolePk implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059259310375624191L;

	/**
	 * 
	 */

	@Column(name = "uuid", length = 45)
	private String uuid;

	@Column(name = "role", length = 45)
	private String role;

	public UserRolePk() {
	}

	/**
	 * @return the userName
	 */
	public String getUuId() {
		return uuid;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void settUuId(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the authority
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param authority
	 *            the authority to set
	 */
	public void setRole(String authority) {
		this.role = authority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		result = prime * result
				+ ((role == null) ? 0 : role.hashCode());
		result = prime * result
				+ ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserRolePk))
			return false;
		UserRolePk other = (UserRolePk) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	private Object getOuterType() {
		return new Object();
	}
}
