package com.tricon.rcm.db.entity;

import java.io.Serializable;

import javax.persistence.*;



import lombok.Data;

@Data
@Entity
@Table(name = "rcm_user_role")
public class RcmUserRole implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 9043330816732239655L;

	@Id
	@EmbeddedId
	private RcmUserRolePk id = new RcmUserRolePk();

	@ManyToOne
	@JoinColumn(name = "uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
	private RcmUser user;

	public void setRole(String role) {
		id.setRole(role);
	}

	public String getRole() {
		return id.getRole();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		RcmUserRole other = (RcmUserRole) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
