package com.tricon.ruleengine.dao.impl;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.api.enums.ReportTypeEnum;
import com.tricon.ruleengine.dao.ReportDao;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.model.db.Reports;
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
			 String queryString="SELECT DATE_FORMAT(rep.created_date,'%m/%d/%Y %T') as rep_create_date,rep.created_by as rep_created_by"
			 		+ ", DATE_FORMAT(rd.created_date,'%m/%d/%Y %T') as rd_created_date," + 
			 		"us.email as email,offi.name as office_name,rep.group_run as rep_group_run,rd.group_run as rd_group_run, " + 
			 		"rep.treatement_plan_id as treatement_plan_id,rep.patient_dob as dob,"
			 		+ " rep.patient_name as patient_name,rep.patient_id as patient_id,rep.ivf_form_id as ivf_form_id," + 
			 		"rd.rule_id as rule_id,rd.error_message as error_message,rl.name as rule_name FROM " + 
			 		" reports as rep, report_detail as rd,rules as rl " + 
			 		" ,user as us ,office as offi where rep.office_id='"+dto.getOfficeId()+"' and " + 
			 		" rep.id=rd.report_id and rl.id=rd.rule_id " + 
			 		" and us.uuid=rd.created_by and offi.uuid=rep.office_id ";
			 if (dto.getReportType().equals(ReportTypeEnum.ReportType.Date.toString())) {
				 queryString= queryString + " and "
				 		+ " ( DATE_FORMAT(rep.created_date,'%m/%d/%Y')='"+dto.getReportField1()+"'"
					     + " or DATE_FORMAT(rd.created_date,'%m/%d/%Y')='"+dto.getReportField1()+"' )";
					 
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.IvfId.toString())) {
				 queryString= queryString + "and  rep.ivf_form_id='"+dto.getReportField1()+"'";
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.PatientName.toString())) {
				 queryString= queryString + "and  upper(rep.patient_name) like '%"+dto.getReportField1().toUpperCase()+"%'";
			 }else if (dto.getReportType().equals(ReportTypeEnum.ReportType.TreatmentId.toString())) {
				 queryString= queryString + "and  rep.treatement_plan_id='"+dto.getReportField1()+"'";
						 
			 }
			 		
			 //queryString=queryString+ " group  by rep.treatement_plan_id, rd.rule_id"
			 queryString=queryString+ "	 order by rd.group_run asc";
			 System.out.println(queryString);
			 list=session.createSQLQuery(queryString).setResultTransformer(Transformers.aliasToBean(ReportResponseDto.class)). list();
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSession(session);

		}
		
		return list;
		
	}
	

}
