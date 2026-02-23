package com.tricon.ruleengine.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tricon.ruleengine.api.enums.ReportTypeEnum;
import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.DigitizationRuleEngineResult;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.ReportResponseTeamWiseDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TeamwiseDataExcelDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.pdf.SelantPdfMainDto;
import com.tricon.ruleengine.pdf.SelantPdfPatDto;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
import com.tricon.ruleengine.service.IVformTypeService;
import com.tricon.ruleengine.service.ReportService;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Controller
public class ReportController {

	@Autowired
	OfficeDao od;

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private IVformTypeService iVformTypeService;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	CaplineIVFGoogleFormService civf;
	
	@Autowired
	TreatmentValidationDao tvd;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@CrossOrigin
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReport(@RequestBody ReportDto dto) {
		dto.setmType("t");
		Object o = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		if (dto.getReportType().equals("ivfRDBMS")) {
			// List<CaplineIVFFormDto> cap=null;
			try {
				
				CaplineIVFQueryFormDto d = new CaplineIVFQueryFormDto();

				d.setPatientIdDB(dto.getReportField1());
				//d.setOfficeNameDB(officeNameDB);
				d.setEmployerNameDB(dto.getEmployerName());
				d.setGeneralDateIVFDoneDB(dto.getGeneralDateRun());
				d.setPatientName(dto.getPatientName());
				d.setIvformTypeId(dto.getIvformTypeId());
				o = (List<CaplineIVFFormDto>) civf.searchIVFDataforApp(d,od.getOfficeByUuid(dto.getOfficeId(),user.getCompany().getUuid()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (dto.getReportType().equals("ivfRDBMSWebsiteParse")) {
		   try {	
			CaplineIVFQueryFormDto d = new CaplineIVFQueryFormDto();
			
			d.setPatientIdDB(dto.getReportField1());
			//d.setOfficeNameDB(officeNameDB);
			d.setEmployerNameDB(dto.getEmployerName());
			d.setPatientDobDB(dto.getDob());
			d.setPatientName(dto.getPatientName());
			d.setIvformTypeId(dto.getIvformTypeId());
			o = (List<CaplineIVFFormDto>) civf.searchIVFDataforAppScrap(d,od.getOfficeByUuid(dto.getOfficeId(),user.getCompany().getUuid()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		}else if (dto.getReportType().equals("ruledatasheet")) {
			   try {
				   List<DigitizationRuleEngineResult> finalData= reportService.getReportsForGoogleSheet(dto);
				   String p = "Check Google sheet for Data";
					if (finalData.size()<=1)  p="No Data Found.";
					o=p;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
				
		}else if (dto.getReportType().equals("sealantElig")) {
			   try {
				    o= reportService.getReportsForSealant(dto,false);
				   } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
				
		}
		else {
			o = prepareData(dto, "TR. ID-");
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", o));
	}

	@CrossOrigin
	@RequestMapping(value = "/reportcl", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateReportCL(@RequestBody ReportDto dto) {
		dto.setmType("c");
		Object o = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		if (dto.getReportType().equals("ivfRDBMS")) {
			// List<CaplineIVFFormDto> cap=null;
			try {
				CaplineIVFQueryFormDto d = new CaplineIVFQueryFormDto();
				
				d.setPatientIdDB(dto.getReportField1());
				//d.setOfficeNameDB(officeNameDB);
				d.setEmployerNameDB(dto.getEmployerName());
				d.setGeneralDateIVFDoneDB(dto.getGeneralDateRun());
				d.setPatientName(dto.getPatientName());

				o = (List<CaplineIVFFormDto>) civf.searchIVFDataforApp(d,od.getOfficeByUuid(dto.getOfficeId(),user.getCompany().getUuid()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (dto.getReportType().equals("ivfRDBMSWebsiteParse")) {
		   try {	
			CaplineIVFQueryFormDto d = new CaplineIVFQueryFormDto();
			
			d.setPatientIdDB(dto.getReportField1());
			//d.setOfficeNameDB(officeNameDB);
			d.setEmployerNameDB(dto.getEmployerName());
			d.setPatientDobDB(dto.getDob());
			d.setPatientName(dto.getPatientName());
			o = (List<CaplineIVFFormDto>) civf.searchIVFDataforAppScrap(d,od.getOfficeByUuid(dto.getOfficeId(),user.getCompany().getUuid()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		}else if (dto.getReportType().equals("ruledatasheet")) {
			   try {
				   List<DigitizationRuleEngineResult> finalData= reportService.getReportsForGoogleSheet(dto);
				   String p = "Check Google sheet for Data";
					if (finalData.size()<=1)  p="No Data Found.";
					o=p;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
				
		}else {
			o = prepareData(dto, "CL. ID-");
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", o));
		/*return ResponseEntity
				.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", prepareData(dto, "CL. ID-")));*/
	}

	private Map<String, List<ReportResponseDto>> prepareData(ReportDto dto, String inv) {
		List<ReportResponseDto> li = reportService.getReports(dto);
		Map<String, List<ReportResponseDto>> map = new LinkedHashMap<>();
		List<ReportResponseDto> a = new ArrayList<>();
		String k = "";
		int ctr=0;
		if (li != null)
			for (ReportResponseDto d : li) {
				String uniqueGen="";
				try {
				String[] d2= d.getRd_created_date().split("/");
				//01/31/2024 21:54:56 
				//24402379456902
				uniqueGen = d2[0]+d2[2].substring(2,4)+d.getIvf_form_id()+d.getTreatement_plan_id()+d.getRd_group_run();
				d.setUnique_id(uniqueGen);
				}catch(Exception g) {
					g.printStackTrace();
				}
				if (dto.getReportType().equalsIgnoreCase(ReportTypeEnum.ReportType.Teamwise.toString())
					|| dto.getReportType().equalsIgnoreCase(ReportTypeEnum.ReportType.TeamwiseDOS.toString())) {
					k = (++ctr)  +").UNI Id - "+uniqueGen+" - "+ " Patient ID- " + d.getPatient_id() + " Patient Name- "
							+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
							+ " Run By-" + d.getName();
					
				}else {
					k = d.getRd_group_run() +").UNI Id - "+uniqueGen+" - "+ " Patient ID- " + d.getPatient_id() + " Patient Name- "
							+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
							+ " Run By-" + d.getName();
				}
				if (map.containsKey(k)) {
					// if the key has already been used,
					// we'll just grab the array list and add the value to it
					a = (List<ReportResponseDto>) map.get(k + "");

					a.add(d);
				} else {
					// if the key hasn't been used yet,
					// we'll create a new ArrayList<String> object, add the value
					// and put it in the array list with the new key
					a = new ArrayList<>();
					a.add(d);
					map.put(k + "", a);
				}

			}
		return map;

	}
	
	/*private Map<String, List<ReportResponseTeamWiseDto>> prepareDataTeamWise(ReportDto dto, String inv) {
		List<ReportResponseDto> li = reportService.getReports(dto);
		Map<String, List<ReportResponseTeamWiseDto>> map = new LinkedHashMap<>();
		List<ReportResponseTeamWiseDto> a = new ArrayList<>();
		ReportResponseTeamWiseDto teamWise= null;
		String k = "";
		int ctr=0;
		if (li != null)
			for (ReportResponseDto d : li) {
				
				if (dto.getReportType().equalsIgnoreCase(ReportTypeEnum.ReportType.Teamwise.toString())) {
					k = (++ctr) + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
							+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
							+ " Run By-" + d.getName();
				}else {
					k = d.getRd_group_run() + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
							+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
							+ " Run By-" + d.getName();
				}
				if (map.containsKey(k)) {
					// if the key has already been used,
					// we'll just grab the array list and add the value to it
					a = (List<ReportResponseTeamWiseDto>) map.get(k + "");
					teamWise= new ReportResponseTeamWiseDto();
					a.add(teamWise);
				} else {
					// if the key hasn't been used yet,
					// we'll create a new ArrayList<String> object, add the value
					// and put it in the array list with the new key
					a = new ArrayList<>();
					a.add(d);
					map.put(k + "", a);
				}

			}
		return map;

	}*/

	@CrossOrigin
	@RequestMapping(value = "/enreport", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateEnancedReport(@RequestBody EnhancedReportDto dto) {
		dto.setmType("t");
		List<?> li = reportService.getEnancedReport(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", li));
	}

	@CrossOrigin
	@RequestMapping(value = "/enreportcl", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateEnancedReportCL(@RequestBody EnhancedReportDto dto) {
		dto.setmType("c");
		List<?> li = reportService.getEnancedReport(dto);
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", li));
	}

	@PostMapping
	@RequestMapping(value = "/genereatePdf")
	public void generatePDF(@RequestBody ReportDto rdto, HttpServletResponse response) throws IOException {
		//
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		
		CaplineIVFQueryFormDto dto= new CaplineIVFQueryFormDto();
		dto.setEmployerNameDB("");
		dto.setGeneralDateIVFDoneDB(rdto.getGeneralDateRun());
		dto.setOfficeNameDB(rdto.getOfficeId());
		//dto.setPatientIdDB(rdto.getReportField1());
		dto.setUniqueID(rdto.getReportField1()); 
		dto.setPatientName("");
		dto.setNewFormat("");
		Office office = od.getOfficeByUuid(dto.getOfficeNameDB(),user.getCompany().getUuid());
        Object[] obj=null; 
		
      obj = civf.generatePDF(dto, office,iVformTypeService.getIVFormTypeById(Integer.parseInt(rdto.getIvformTypeId())));
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0].toString().replaceAll(",", "")+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
		

	}

	@PostMapping
	@RequestMapping(value = "/genereatePdfSealant")
	public void generatePDFSealant(@RequestBody ReportDto rdto, HttpServletResponse response) throws IOException {
		//
	 Object[] obj=null; 
	 
	  obj = reportService.generateSealntPDF(rdto);
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+"sealant_rep"+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
    
	}

	@PostMapping
	@RequestMapping(value = "/genereatePdfSealantUI")
	public void generatePDFSealant(@RequestBody HashMap<String,List<TPValidationResponseDto>> rdto, HttpServletResponse response) throws IOException {
		//
	 Object[] obj=null; 
	 
	  obj = reportService.generateSealntPDByUIData(rdto);
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+"sealant_rep"+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
    
	}
	
	@PostMapping
	@RequestMapping(value = "/fillupgsheet")
	public void fillupGheet(@RequestBody ReportDto rdto, HttpServletResponse response) throws IOException {
		
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;

		CaplineIVFQueryFormDto dto= new CaplineIVFQueryFormDto();
		dto.setEmployerNameDB("");
		dto.setGeneralDateIVFDoneDB(rdto.getGeneralDateRun());
		dto.setOfficeNameDB(rdto.getOfficeId());
		dto.setPatientIdDB(rdto.getReportField1());
		dto.setPatientName("");
		dto.setSheetId("");
		dto.setSheetSubId("0");
		Office office = od.getOfficeByUuid(dto.getOfficeNameDB(),user.getCompany().getUuid());
        civf.fillUpGoogleSheet(dto, office);
		
		

	}
	
	@CrossOrigin
	@RequestMapping(value = "/rulereportdata", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateRuleReport(@RequestBody RuleReportDto dto) {
		
	  return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", reportService.getRuleReport(dto)));
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/rulereportdataAllMess", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> generateRuleReportAllMess(@RequestBody RuleReportDto dto) {
		
	  return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "Report Created Successfully", reportService.getRuleReportAllMessage(dto)));
	}
	
	
	@PostMapping
	@RequestMapping(value = "/generateTeamwiseExcel")
	public void generateTeamwiseExcel(@RequestBody TeamwiseDataExcelDto  dto, HttpServletResponse response) throws IOException {
		//
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		final UserDetails userDetails = userDetailsService.loadUserByUsername(((UserDetails)principal).getUsername());
		JwtUser user = (JwtUser) userDetails;
		
		
		 Object[] obj=null; 
		 
		  obj = reportService.generateTeamwiseExcel(dto);
			if (obj != null && obj[1]!=null) {
				ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", String.format("attachment; filename="+"team_wise"+ ".xlsx"));
				//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
				InputStream in = new ByteArrayInputStream(o.toByteArray());
				org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
				response.flushBuffer();
				o.close();
			}
		
		

	}
	

}
