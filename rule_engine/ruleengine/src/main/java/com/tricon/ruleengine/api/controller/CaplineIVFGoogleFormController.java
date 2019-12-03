package com.tricon.ruleengine.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.UserRegistrationDto;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Patient;
import com.tricon.ruleengine.pdf.CaplineIVFFormDtoToXML;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;

@CrossOrigin
@RestController
public class CaplineIVFGoogleFormController {

	@Autowired
	TreatmentValidationDao tvd;

	@Autowired
	CaplineIVFGoogleFormService civf;

	@Autowired
	OfficeDao od;
	
	@Value("${application.url}")
	private String APP_URL;

	@CrossOrigin
	@PostMapping
	@RequestMapping(value = "/savedatatore")
	public ResponseEntity<Object> saveIVFFromGoogleForm(@RequestBody CaplineIVFFormDto dto,
			HttpServletRequest request) {
		//

		//Integer i = 0;
		Object [] ob=null;
		
		Office office = od.getOfficeByName(dto.getBasicInfo1());
		try {
			

			EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(office);

			if (esDB != null && esDB.getPassword().equals(dto.getPasswordRE())) {
				 ob= civf.saveIVFFormData(dto, office);
				
			}else {
				//i = civf.saveIVFFormData(dto, office);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mess = "Data saved successfully with IVF ID "+office.getName()+"_"+ ob[0];
				
		if ((Integer)ob[0] == 0)
			mess = "Data not saved successfully- Reason-"+ob[1];

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", mess));

	}

	@CrossOrigin
	@PostMapping
	@RequestMapping(value = "/queryivdatafromdb")
	public ResponseEntity<Object> queryDataFromDB(@RequestBody CaplineIVFQueryFormDto dto, HttpServletRequest request) {
		//

		List<CaplineIVFFormDto> cap = null;
		try {
			Office office = od.getOfficeByName(dto.getOfficeNameDB());

			EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(office);

			if (esDB != null && esDB.getPassword().equals(dto.getPasswordRE())) {
				/*if(dto.getGeneralDateIVFDoneDB()!=null ) {
					if(!dto.getGeneralDateIVFDoneDB().equals("")) {
						
					}
				}*/
				cap = (List<CaplineIVFFormDto>) civf.searchIVFData(dto,office);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", cap));

	}

	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/queryivdatafromdbgoogle")
	public ResponseEntity<Object> queryDataFromDBGoogle(
			//@RequestParam(value = "selectcolumns", required = true) String selectcolumns,
			@RequestParam(value = "columns", required = true) String columns,
			//@RequestParam(value = "ids", required = false) String ids,
			@RequestParam(value = "password", required = true) String password,
            //@RequestParam(value = "columnCount", required = true) int columnCount,
            @RequestParam(value = "gndate", required = false) String generalDateIVFDoneDB,
            @RequestParam(value = "patid", required = false) String patientIdDB,
            @RequestParam(value = "patname", required = false) String patientName,
            @RequestParam(value = "ivfid", required = false) String uniqueID,
            @RequestParam(value = "empame", required = false) String employerNameDB,
            @RequestParam(value = "office", required = true) String office, HttpServletRequest request,
			HttpServletResponse response) {
		//Example
		/*
		 * localhost:8080/queryivdatafromdbgoogle?columns=concat(coalesce(first_name,''),' ',coalesce(last_name,'')),ins_name,tax_id,policy_holder,p.dob,ins_contact,cs_sr_name,policy_holder_dob,employer_name,const_tx_recall_np,ref,member_ssn,group_p,cob_status,memberId,apt_date,payer_id,provider_name,ins_address,plan_type,plan_termed_date,plan_network,plan_fee_schedule_name,plan_effective_date,plan_calendar_fiscal_year,plan_annual_max,plan_annual_max_remaining,plan_individual_dedudtible,plan_individual_deductible_remaining,plan_dependents_covered_to_age,plan_pre_d_mandatory,plan_non_duplicate_clause,plan_full_time_student_status,plan_assignment_of_benefits,plan_coverage_book,basic_percentage,basic_subject_deductible,major_percentage,major_subject_deductible,endo_dontics_percentage,endo_subjectdeductible,perio_surgrey_percentage,perio_surgery_subject_deductible,preventive_percentage,diagnostic_percentage,pa_xrays_percentage,missingtooth_clause,replacementclause,crowns_d2750D2740_pays_prep_seat_date,night_guards_d9940fl,basic_waiting_period,major_waiting_period,sscd2930fl,sscd2931fl,exam_d0120_fl,exams_d0140_fl,eexams_d0145_fl,exams_d0150_fl,x_rays_bw_sfl,x_rays_pad0220_fl,x_rays_pad0230_fl,x_rays_fm_xfl,x_rays_bundling,flouride_d1208_fl,flouride_age_limit,varnish_d1206_fl,varnish_d1206age_limit,sealants_d1351_percentage,sealants_d1351_fl,sealants_d1351_age_limit,sealants_d1351_primary_molars_covered,sealants_d1351_pre_molars_covered,sealants_d1351_permanent_molars_covered,prophy_d1110_fl,prophy_d1120_fl,name1201110_roll_over_age,s_rpd4341_percentage,s_rpd4341_fl,s_rpd4341_quads_per_day,s_rpd4341_days_bw_treatment,perio_maintenance_d4910_percentage,perio_maintenance_d4910_fl,perio_maintenance_d4910_altw_prophy_d0110,fmdd4355_percentage,fmdd4355_fl,gingivitis_d4346_percentage,gingivitis_d4346_fl,nitrous_d9230_percentage,iv_sedation_d9243_percentage,iv_sededation_d9248_percentage,extractions_minor_percentage,extractions_major_percentage,crown_length_d4249_percentage,crown_length_d4249_fl,alveo_d7311_covered_with_ext,alveo_d7311_fl,alveoD7310Covered_with_ext,alveo_d7310fl,complete_dentures_d5110_d5120_fl,immediate_dentures_d5130_d5140_fl,partial_dentures_d5213_d5214_fl,interim_partial_dentures_d5214_fl,bone_grafts_d7953_covered_with_ext,bone_grafts_d7953_fl,implant_coverage_d6010_percentage,implant_coverage_d6057_percentage,implant_coverage_d6190_percentage,implant_supported_porc_ceramic_d606_percentage,post_composites_d2391_percentage,post_composites_d2391_fl,posterior_composites_d2391_downgrade,crowns_d2750_d2740_percentage,crowns_d2750_d2740_fl,crowns_d2750_d2740_downgrade,night_guards_d9940_percentage,d9310_percentage,d9310_fl,buildups_d2950_covered,buildups_d2950_fl,buildups_d2950_same_day_crown,orthoPercentage,ortho_max,ortho_age_limit,ortho_subject_deductible,fillings_bundling,comments,general_benefits_verified_by,general_date_iv_wasdone,cra_required,claim_filling_limit,concat(unique_id,pd.id)&password=134568&patid=93&office=Jasper
		 */

		List<Object> cap = null;
		CaplineIVFQueryFormDto dto= new CaplineIVFQueryFormDto();
		dto.setOfficeNameDB(office);
		dto.setGeneralDateIVFDoneDB(generalDateIVFDoneDB);
		dto.setPasswordRE(password);
		dto.setPatientIdDB(patientIdDB);
		dto.setPatientName(patientName);
		dto.setUniqueID(uniqueID);
		dto.setEmployerNameDB(employerNameDB);
		dto.setColumns(columns);
		
		
		
		
		try {
			Office off = od.getOfficeByName(office);

			EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(off);
			

			if (esDB != null && esDB.getPassword().equals(dto.getPasswordRE())) {
				
				cap = (List<Object>) civf.searchIVFDataForGoogleSheet(dto,off);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", cap));

	}

	@CrossOrigin
	@GetMapping
	@RequestMapping(value = "/queryivdatatopdf")
	public void generatePDF(@RequestParam String o ,@RequestParam String id,
			@RequestParam String p,HttpServletResponse response) throws IOException {
		//
		CaplineIVFQueryFormDto dto= new CaplineIVFQueryFormDto();
		dto.setPasswordRE(p);
		dto.setUniqueID(id);
		dto.setOfficeNameDB(o);
		
		Office office = od.getOfficeByName(dto.getOfficeNameDB());

		EagleSoftDBDetails esDB = tvd.getESDBDetailsByOffice(office);
        Object[] obj=null; 
		
		
		if (esDB != null && esDB.getPassword().equals(dto.getPasswordRE())) {
			obj = civf.generatePDF(dto,office);
		}
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream ou =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			//String name="" java.net.URLEncoder.encode(obj[0]+ ".pdf","UTF-8")
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+java.net.URLEncoder.encode(obj[0]+ ".pdf","UTF-8")));
			response.setHeader("Content-Disposition", String.format("attachment; filename="+(obj[0] +".pdf").replaceAll(" ", "")));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(ou.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			ou.close();
		}
		

	}

}
