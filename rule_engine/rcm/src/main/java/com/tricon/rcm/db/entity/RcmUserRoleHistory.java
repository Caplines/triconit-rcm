package com.tricon.rcm.db.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rcm_user_role_history")
public class RcmUserRoleHistory implements Serializable{
	
	private static final long serialVersionUID = 3911626627575940850L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(nullable = false,length = 100)
	private String email;
	
	@Column(name = "first_name", nullable = false, length = 20)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 20)
	private String lastName;
	
	@Column(name = "client_name",columnDefinition ="text")
	private String clientName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "uuid",nullable = false)
	private RcmUser user;
	
	@Column(name = "roles",columnDefinition ="text")
	private String rolesDetails;
	
	@Column(name = "team_name")
	private String teamName;
	
	@CreationTimestamp
	@Column(name = "created_date", nullable = false, updatable = false)
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "updated_date", nullable = true)
	private Date updatedDate;
}
