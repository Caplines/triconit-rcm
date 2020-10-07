package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.api.enums.HighLevelReportTypeEnum;
import com.tricon.ruleengine.api.enums.ReportTypeEnum;
import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.EnhancedValidateBatchNumReportDto;
import com.tricon.ruleengine.dto.EnhancedValidateBatchReportDto;
import com.tricon.ruleengine.dto.EnhancedValidateTxNumReportDto;
import com.tricon.ruleengine.dto.EnhancedValidateTxReportDto;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.utils.Constants;

@Repository
public class ReportDaoImpl extends BaseDaoImpl implements ReportDao{

	@Override
	public List<ReportResponseDto> getReports(ReportDto dto) {
		Session session=null;
		List<ReportResponseDto>  list=null;
		
		try {
			 session=getSession();
			 /*
			System.out.println( ReportTypeEnum.ReportType.Date);
				Criteria criteria = session.createCriteria(Reports.class);
				if (dto.getReportType().equals(ReportTypeEnum.ReportType.Date.toString())) {
					Criterion cdate = Restrictions.eq("createdDate",Constants.SIMPLE_DATE_FORMAT.parse(dto.getReportField1()));
					Criterion udate = Restrictions.gt("updatedDate",Constants.SIMPLE_DATE_FORMAT.parse(dto.getReportField1()));
					criteria.add(Restrictions.or(cdate,udate));
				}else if (dto.getReportType().equals(ReportTypeEnum.ReportType.OfficeId.toString())) {
					criteria.createAlias("office", "off");
					criteria.add(Restrictions.eq("off.uuid", dto.getReportField1()));
					//
				}else if (dto.getReportType().equals(ReportTypeEnum.ReportType.PatientName.toString())) {
					criteria.add(Restrictions.ilike("patientName",dto.getReportField1()));
					
				}else if (dto.getReportType().equals(ReportTypeEnum.ReportType.TreatmentId.toString())) {
					criteria.add(Restrictions.eq("treatementPlanId",dto.getReportField1()));
					
				}
				criteria.createAlias("reportDetails", "rd");
				criteria.createAlias("createdBy", "cr");
				
				*/
			 String x="rep.treatement_plan_id as treatement_plan_id";
			 String t="reports as rep, report_detail as rd";
			 String z="rep.treatement_plan_id";
			 
			 if (dto.getmType()!=null && dto.getmType().equals("c")) {
				 x="rep.claim_id as treatement_plan_id";
				 t="reports_claim as rep, report_claim_detail as rd";
				 z="rep.claim_id";
			 }
			 String queryString="SELECT DATE_FORMAT(rep.created_date,'%m/%d/%Y %T') as rep_create_date,rep.created_by as rep_created_by, "+
					 " CONCAT( first_name ,' ' ,last_name ) as name "
			 		+ ", DATE_FORMAT(rd.created_date,'%m/%d/%Y %T') as rd_created_date," + 
			 		"us.email as email,offi.name as office_name,rep.group_run as rep_group_run,rd.group_run as rd_group_run, " + 
			 		x+",rep.patient_dob as dob,"
			 		+ " rep.patient_name as patient_name,rep.patient_id as patient_id,rep.ivf_form_id as ivf_form_id," + 
			 		"rd.rule_id as rule_id,rd.error_message as error_message,rl.name as rule_name FROM " + 
		            t+",rules as rl " + 
			 		" ,user as us ,office as offi where rep.office_id='"+dto.getOfficeId()+"' and " + 
			 		" rep.id=rd.report_id and rl.id=rd.rule_id " + 
			 		" and us.uuid=rd.created_by and offi.uuid=rep.office_id ";
			 if (dto.getReportType().equals(ReportTypeEnum.ReportType.Date.toString())) {
				 queryString= queryString + " and "
				 		+ " ( DATE_FORMAT(rep.created_date,'%m/%d/%Y')='"+dto.getReportField1()+"'"
					     + " or DATE_FORMAT(rd.created_date,'%m/%d/%Y')='"+dto.getReportField1()+"' )";
					 
			 }else if(dto.getReportType().equals(ReportTypeEnum.ReportType.DateFromTo.toString())) {
				 queryString= queryString+	"  and (" + 
							"  (rd.created_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
							"                 or" + 
							"  (rd.updated_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
							"                 or" + 
							"  (rep.created_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
							"                 or" + 
							"  (rep.updated_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
							
							"" + 
							" )" ;
				 
				 
						 
			 }else if(dto.getReportType().equals(ReportTypeEnum.ReportType.UserName.toString())) {
				 queryString= queryString+	"  and rd.created_by='"+dto.getEmployerName()+"'" ;
				 
			 }else if(dto.getReportType().equals(ReportTypeEnum.ReportType.DateFromToUserName.toString())) {
				 queryString= queryString+	"  and (" + 
							"  (rd.created_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
							"                 or" + 
							"  (rd.updated_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
							"                 or" + 
							"  (rep.created_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
							"                 or" + 
							"  (rep.updated_date between STR_TO_DATE( '"+dto.getReportField1()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
							"  and STR_TO_DATE('"+dto.getReportField2()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
							"" + 
							" )  and rd.created_by='"+dto.getEmployerName() +"'" ;
				 
				 
						 
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.IvfId.toString())) {
				 queryString= queryString + "and  rep.ivf_form_id='"+dto.getReportField1()+"'";
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.PatientName.toString())) {
				 queryString= queryString + "and  upper(rep.patient_name) like '%"+dto.getReportField1().toUpperCase()+"%'";
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.TreatmentId.toString())) {
				 queryString= queryString + "and  "+z+"='"+dto.getReportField1()+"'";
						 
			 }
			 		
			 //queryString=queryString+ " group  by rep.treatement_plan_id, rd.rule_id"
			 queryString=queryString+ "	 order by rep.patient_id,rd.group_run desc";
			 //System.out.println(queryString);
			 list=session.createSQLQuery(queryString).setResultTransformer(Transformers.aliasToBean(ReportResponseDto.class)). list();
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSession(session);

		}
		
		return list;
		
	}

	@Override
	public List<?> getEnancedReport(EnhancedReportDto dto) {
		// TODO Auto-generated method stub
		Session session=null;
        List<?> list =null;
        Class<?> clazz=null;
        
        //At least one data need to be present
        if (dto.getIvfId() == null && dto.getOfficeId()== null && dto.getpId()== null && dto.getTpId()== null && 
        		(dto.getEndDate()==null || dto.getStartDate()==null )) {
        	return null;
        }
		try {
			 session=getSession();
			 
		String queryString=null;
		if (HighLevelReportTypeEnum.BATCH.getType()== dto.getReportType()) {
			queryString=getBatchReport(dto);
			clazz=EnhancedValidateBatchReportDto.class;
		}
		else if (HighLevelReportTypeEnum.BATCH_NUM.getType()== dto.getReportType()) {
			queryString=getBatchNumReport(dto);
			clazz=EnhancedValidateBatchNumReportDto.class;
		}
		else if (HighLevelReportTypeEnum.TXPLAN.getType()== dto.getReportType()) {
			queryString=getTxReport(dto);
			clazz=EnhancedValidateTxReportDto.class;
		}
		else if (HighLevelReportTypeEnum.TXPLAN_NUM.getType()== dto.getReportType()) {
			queryString=getTxNumReport(dto);
			clazz=EnhancedValidateTxNumReportDto.class;
		}
		RuleEngineLogger.generateLogs(clazz, "Query run -"+queryString, Constants.rule_log_debug, null);
		list= session.createSQLQuery(queryString).setResultTransformer(Transformers.aliasToBean(clazz)). list();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private String getTxReport(EnhancedReportDto dto) {
		String x="reports rep, report_detail repd";
		String t="treatement_plan_id";
		
		if (dto.getmType().equals("c")) {
			 x="reports_claim rep, report_claim_detail repd";
			 t="claim_id";
		}

		String query=" select * from (" + 
				 "select CONCAT(fn,' ',ln) as name, "+t+" as txP," + 
				" GROUP_CONCAT( concat (ct,'"+Constants.EN_REP_TYPE_SEP+"',message_type) SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as resultsum," + 
				" GROUP_CONCAT(distinct name SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as office," + 
				" GROUP_CONCAT(distinct patient_id SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as pid," + 
				" GROUP_CONCAT(distinct patient_name SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as pname," + 
				" GROUP_CONCAT(distinct ivf_form_id SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as ivfId from (" + 
				" select count(message_type) as ct,message_type,us.first_name as fn,us.last_name as ln,"+t+",ivf_form_id ,patient_id,patient_name,off.name" + 
				" from "+x+" ,office off,user as us where "+
				" repd.report_id=rep.id and repd.report_type="+HighLevelReportTypeEnum.TXPLAN.getType()+" and  off.uuid=rep.office_id  "+ 
				//" and rep.group_run = repd.group_run "+
		        " and us.uuid=repd.created_by " ;
		if (dto.getEmployerName() != null && !dto.getEmployerName().equals("")) 	query=query	+ "  and repd.created_by ='"+dto.getEmployerName()+"' " ; 
		if (dto.getOfficeId() != null && !dto.getOfficeId().equalsIgnoreCase("All") && !dto.getOfficeId().equals("")) 	query=query	+ "  and rep.office_id ='"+dto.getOfficeId()+"' " ; 
		if (dto.getpId() != null && !dto.getpId().equals("")) 	query=query	+ "  and rep.patient_id ='"+dto.getpId()+"' " ; 
		if (dto.getTpId() != null && !dto.getTpId().equals("")) 	    query=query	+ "  and rep."+t+" ='"+dto.getTpId()+"' " ; 
		if (dto.getIvfId() != null  && !dto.getIvfId().equals("")) 	query=query	+ "  and rep.ivf_form_id ='"+dto.getIvfId()+"' " ; 

		if (dto.getEndDate()!=null && dto.getStartDate()!=null && !dto.getEndDate().equals("") && !dto.getStartDate().equals("")) {
			query=query+	"  and (" + 
				"  (repd.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
				"                 or" + 
				"  (repd.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
				"                 or" + 
				"  (rep.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
				"                 or" + 
				"  (rep.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" +
				
				
				"" + 
				" )" ;
		}
			query=query+ " group by repd.message_type,"+t+",ivf_form_id,patient_id,patient_name  order by repd.message_type asc) a" + 
				"  group by a."+t+",a.patient_id ) as b where b.resultsum is not null	"; 
		return query;
	}
	
	private String getTxNumReport(EnhancedReportDto dto) {
		String query=queryNumber(dto, HighLevelReportTypeEnum.TXPLAN.getType());
		return query;
	}
	private String getBatchReport(EnhancedReportDto dto) {
		String x="reports rep, report_detail repd";
		String t="treatement_plan_id";
		if (dto.getmType().equals("c")) {
			 x="reports_claim rep, report_claim_detail repd";
			 t="claim_id";
		}
		
		String query=" select * from (" + 
				 "select "+ 
				" CONCAT(fn,' ',ln) as name,"+
				" GROUP_CONCAT( concat (ct,'"+Constants.EN_REP_TYPE_SEP+"',message_type) SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as resultsum," + 
				" GROUP_CONCAT(distinct name SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as office," + 
				" GROUP_CONCAT(distinct patient_id SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as pid," + 
				" GROUP_CONCAT(distinct patient_name SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as pname," + 
				" GROUP_CONCAT(distinct ivf_form_id SEPARATOR '"+Constants.EN_REP_COUNT_SEP+"') as ivfId from (" + 
				" select count(message_type) as ct,us.first_name as fn,us.last_name as ln,message_type,"+t+",ivf_form_id,patient_id,patient_name,off.name" + 
				" from "+x+" ,office off,user as us where "+
				" repd.report_id=rep.id and repd.report_type="+HighLevelReportTypeEnum.BATCH.getType()+" and  off.uuid=rep.office_id and rep.group_run = repd.group_run  " +
				" and us.uuid=repd.created_by ";
		if (dto.getEmployerName() != null && !dto.getEmployerName().equals("")) 	query=query	+ "  and us.uuid ='"+dto.getEmployerName()+"' " ; 
		if (dto.getOfficeId() != null && !dto.getOfficeId().equalsIgnoreCase("All") && !dto.getOfficeId().equals("")) 	query=query	+ "  and rep.office_id ='"+dto.getOfficeId()+"' " ; 
		if (dto.getpId() != null && !dto.getpId().equals("")) 	query=query	+ "  and rep.patient_id ='"+dto.getpId()+"' " ; 
		//if (dto.getTpId() != null && !dto.getTpId().equals("")) 	    query=query	+ "  and rep.treatement_plan_id ='"+dto.getTpId()+"' " ; 
		if (dto.getIvfId() != null  && !dto.getIvfId().equals("")) 	query=query	+ "  and rep.ivf_form_id ='"+dto.getIvfId()+"' " ; 
		
		
		
		
		if (dto.getEndDate()!=null && dto.getStartDate()!=null  && !dto.getEndDate().equals("") && !dto.getStartDate().equals("")) {
			query=query+	"  and (" + 
				"  (repd.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
				"                 or" + 
				"  (repd.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
				"                 or" + 
				"  (rep.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
				"                 or" + 
				"  (rep.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
				"" + 
				" )" ;
		}
			query=query+ " group by repd.message_type,ivf_form_id,patient_id,patient_name  order by repd.message_type asc) a" + 
				"  group by a.ivf_form_id ) as b where b.resultsum is not null	"; 
		return query;
	}

	private String getBatchNumReport(EnhancedReportDto dto) {
		String query=queryNumber(dto, HighLevelReportTypeEnum.BATCH.getType());
		return query;
	}
	
	private String queryNumber(EnhancedReportDto dto, int reportType) {
		
		String x="reports rep, report_detail repd";
		String t=" treatement_plan_id ";
		if (dto.getmType().equals("c")) {
			 x="reports_claim rep, report_claim_detail repd";
			 t=" claim_id ";
		}

		
		
		String rep=" ivf_form_id ";
		if (HighLevelReportTypeEnum.TXPLAN.getType()==reportType) rep=t;
		String query=" "+
		"select GROUP_CONCAT(distinct "+rep+" SEPARATOR ';') as ct," + 
		"GROUP_CONCAT(distinct name SEPARATOR ';') as office," + 
		"GROUP_CONCAT( concat (ct,'TYPE',message_type) SEPARATOR ';') as resultsum" + 
		" from" + 
		"(" + 
		"select sum(ct) as ct,message_type,name,GROUP_CONCAT(distinct "+rep+" SEPARATOR ';') as "+rep+"   from (" + 
		"select count(message_type) as ct,message_type," + 
		" "+rep+" ,off.name    from "+x+" ,office off where  repd.report_id=rep.id and" + 
		"     repd.report_type="+reportType+" and     off.uuid=rep.office_id and rep.group_run = repd.group_run";
		         if (dto.getOfficeId() != null && !dto.getOfficeId().equalsIgnoreCase("All") && !dto.getOfficeId().equals("")) 	query=query	+ "  and rep.office_id ='"+dto.getOfficeId()+"' " ; 
		         if (dto.getEndDate()!=null && dto.getStartDate()!=null  && !dto.getEndDate().equals("") && !dto.getStartDate().equals("")) {
		 			query=query+	"  and (" + 
		 				"  (repd.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
		 				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
		 				"                 or" + 
		 				"  (repd.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
		 				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
		 				"                 or" + 
		 				"  (rep.created_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
		 				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
		 				"                 or" + 
		 				"  (rep.updated_date between STR_TO_DATE( '"+dto.getStartDate()+" 00:00:00', '%m/%d/%Y %H:%i:%s')" + 
		 				"  and STR_TO_DATE('"+dto.getEndDate()+" 23:59:59', '%m/%d/%Y %H:%i:%s') )" + 
		 				"" + 
		 				" )" ;
		 		  }
		         query=query+ "    group by repd.message_type, "+rep+"   order by repd.message_type asc" + 
		         		"   )a group by name,message_type" + 
		         		")b group by name" ;

		return query;
		
	}
}
