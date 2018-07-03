package com.tricon.ruleengine.model.db;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1235991165458564124L;

	/**
	 * 
	 */

	@Id
	@EmbeddedId
	private UserRolePk id = new UserRolePk();

	@ManyToOne
	@JoinColumn(name = "uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
	private User user;

	public void setRole(String role) {
		id.setRole(role);
	}

	public String getRole() {
		return id.getRole();
	}

	/**
	 * @return the securityUser
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param securityUser
	 *            the securityUser to set
	 */
	public void setUser(User user) {
		this.user = user;
		id.settUuId(user.getUuid());
	}

	/**
	 * @return the id
	 */
	public UserRolePk getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UserRolePk id) {
		this.id = id;
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
		UserRole other = (UserRole) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
