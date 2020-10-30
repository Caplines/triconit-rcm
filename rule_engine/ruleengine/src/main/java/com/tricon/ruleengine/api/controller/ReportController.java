package com.tricon.ruleengine.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;
import com.tricon.ruleengine.dto.CaplineIVFQueryFormDto;
import com.tricon.ruleengine.dto.EnhancedReportDto;
import com.tricon.ruleengine.dto.GenericResponse;
import com.tricon.ruleengine.dto.ReportDto;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.RuleReportDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.security.JwtUser;
import com.tricon.ruleengine.service.CaplineIVFGoogleFormService;
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
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	CaplineIVFGoogleFormService civf;

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
		} else {
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
		} else {
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
		if (li != null)
			for (ReportResponseDto d : li) {
				k = d.getRd_group_run() + "). Patient ID- " + d.getPatient_id() + " Patient Name- "
						+ d.getPatient_name() + " IVF ID-" + d.getIvf_form_id() + " " + inv + d.getTreatement_plan_id()
						+ " Run By-" + d.getName();
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
		
		obj = civf.generatePDF(dto, office);
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

}
