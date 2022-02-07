package com.tricon.esdatareplication.entity.repdb;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

import com.tricon.esdatareplication.entity.BaseInfoAudit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "es_table")
public class ESTable extends BaseInfoAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7166507280314149556L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column(name = "table_name", unique = true, nullable = false)
	private String tableName;
	
	@Column(name = "code_witten", nullable = false)
	private int codeWritten;
	
	@Column(name = "uploaded_to_local", nullable = false)
	private int uploadedToLocal;
	
	@Column(name = "order_table", nullable = false)
	private int orderTable;

	@Column(name = "static_table", nullable = false)
	private int staticTable;

	@Column(name = "iteration_count", nullable = false)
	private int iterationCount;

	@Column(name = "records_inserted_last_iteration", nullable = false)
	private int recordsInsertedLastIteration;

	@Column(name = "last_isssue_detail", nullable = true, columnDefinition="text")
	private String lastIssueDetail;
	
	@Column(name = "last_back_date_check")
	private long lastBackDateCheck;//To see issue in Data like Transaction //for Walk-out case

	@Column(name = "last_back_date_deletion_check")
	private long lastBackDateDeletionCheck;//To see issue in Data Delete like Appointment //for Walk-out case
	@Column(name = "updated_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	//@Column(name = "last_iteration_start_date", nullable = true)
	//@Temporal(TemporalType.TIMESTAMP)
	//private Date lastIterationStartDate;

}
