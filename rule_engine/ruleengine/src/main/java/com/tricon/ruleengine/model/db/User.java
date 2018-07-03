package com.tricon.ruleengine.model.db;
import java.io.Serializable;
//https://medium.com/@gustavo.ponce.ch/spring-boot-spring-mvc-spring-security-mysql-a5d8545d837d
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

/**
 * @author Deepak.Dogra
 *
 */
@Entity
@Table(name = "user")
public class User implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5596282062518216537L;
	@GeneratedValue(generator = "uuid2")//only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2",strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;
	@Column(name = "email",unique=true)
	@Email(message = "*Please provide a valid Email")
	@NotEmpty(message = "*Please provide an email")
	private String email;
	@Column(name = "password")
	@Length(min = 5, message = "*Your password must have at least 5 characters")
	@NotEmpty(message = "*Please provide your password")
	@Transient
	private String password;
	@Column(name = "name")
	@NotEmpty(message = "*Please provide your name")
	private String name;
	@Column(name = "last_name")
	@NotEmpty(message = "*Please provide your last name")
	private String lastName;
	@Column(name = "active")
	private int active;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<UserRole> roles;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_id")
	private Office office;
	

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}
	
	public Office getOffice() {
		return this.office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}


}