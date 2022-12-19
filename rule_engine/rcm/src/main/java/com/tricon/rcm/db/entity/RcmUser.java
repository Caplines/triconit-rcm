package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.tricon.rcm.db.BaseAuditEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "rcm_user")
public class RcmUser extends BaseAuditEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7843710590373922972L;

	@GeneratedValue(generator = "uuid2") // only possible for Id's
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, length = 45)
	private String uuid;

	@Column(nullable = false, unique = true, length = 45)
	private String userName;

	@Column(nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String password;

	@Column(name = "first_name", nullable = false, length = 20)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 20)
	private String lastName;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<RcmUserRole> roles;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id",referencedColumnName="uuid")
	private RcmCompany company;

	@Column(name = "last_password_reset_date")
	private Date lastPasswordResetDate;
	
	@Column(name = "active")
	private int active;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id",referencedColumnName="id")
	private RcmTeam team;
	
}
