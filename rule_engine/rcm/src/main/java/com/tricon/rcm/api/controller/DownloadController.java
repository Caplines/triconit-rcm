package com.tricon.rcm.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.GenericResponse;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.download.AllPendancyDownloadDto;
import com.tricon.rcm.dto.download.AttachmentDownloadDto;
import com.tricon.rcm.dto.download.ClaimDetailsDownloadDto;
import com.tricon.rcm.dto.download.IssueClaimDownloadDto;
import com.tricon.rcm.dto.download.IvfDownloadDto;
import com.tricon.rcm.dto.download.ListOfClaimDownloadDto;
import com.tricon.rcm.dto.download.OthersTeamWorkDownloadDto;
import com.tricon.rcm.dto.download.PendancyDownloadDto;
import com.tricon.rcm.dto.download.ProductionDownloadDto;
import com.tricon.rcm.dto.download.TreatmentPlanDownloadDto;
import com.tricon.rcm.service.impl.AttachmentServiceImpl;
import com.tricon.rcm.service.impl.DownLoadService;
import com.tricon.rcm.util.MessageConstants;


@RestController
@CrossOrigin
public class DownloadController extends BaseHeaderController{

	@Autowired
	DownLoadService service;
	
	@Autowired
	private AttachmentServiceImpl attachmentServiceImpl;
	
	
	
	@PostMapping
	@RequestMapping(value = "/api/list-of-claim/d/pdf")
	public void generatePDF(@RequestBody ListOfClaimDownloadDto dto, HttpServletResponse response) throws IOException {

		Object[] obj=null; 
	     obj = service.generatePdfForListOfClaims(dto);
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+dto.getFileName().toString().replaceAll(",", "")+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
		

	}
	
	@GetMapping
	@RequestMapping(value = "/api/claim/d/pdf/{uuid}")
	public void generateClaimPDF(@PathVariable("uuid") String claimUuid,HttpServletResponse response,
			Model model) throws IOException {
		//
		Object[] obj=null; 
		PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		if (partialHeader!=null) {
		
	     obj = service.generateClaimPdf(claimUuid,partialHeader);
	     /*
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+dto.getFileName().toString().replaceAll(",", "")+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
		*/
		}
	}
	
	@PostMapping
	@RequestMapping(value = "/api/issue-claim/d/pdf")
	public void generatePDFForIssueClaim(@RequestBody IssueClaimDownloadDto dto, HttpServletResponse response)
			throws IOException {
		Object[] obj = null;
		obj = service.generatePdfForIssueClaim(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping
	@RequestMapping(value = "/api/pendancy/d/pdf")
	public void generatePDFForPendancy(@RequestBody PendancyDownloadDto dto, HttpServletResponse response) throws IOException {
		Object[] obj = null;
		obj = service.generatePDFForPendancy(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}

	
	@PostMapping
	@RequestMapping(value = "/api/claim-details/d/pdf")
	public void generatePDFForclaimDetails(@RequestBody ClaimDetailsDownloadDto dto, HttpServletResponse response) throws IOException {
		System.out.println(dto);
		Object[] obj = null;
		obj = service.generatePDFForclaimDetails(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping
	@RequestMapping(value = "/api/production/d/pdf")
	public void generatePDFForProduction(@RequestBody ProductionDownloadDto dto, HttpServletResponse response)
			throws IOException {
		Object[] obj = null;
		obj = service.generatePDFForProduction(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping
	@RequestMapping(value = "/api/allPendancy/d/pdf")
	public void generatePDFForAllPendancy(@RequestBody AllPendancyDownloadDto dto, HttpServletResponse response)
			throws IOException {
		Object[] obj = null;
		obj = service.generatePDFForAllPendancy(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping
	@RequestMapping(value = "/api/tp-link/d/pdf")
	public void generatePDFForTreatmentPlan(@RequestBody TreatmentPlanDownloadDto dto, HttpServletResponse response)
			throws IOException {
		Object[] obj = null;
		obj = service.generatePDFForTpPlan(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping
	@RequestMapping(value = "/api/ivf/d/pdf")
	public void generatePDFForIvf(@RequestBody IvfDownloadDto dto, HttpServletResponse response)
			throws IOException {
		Object[] obj = null;
		obj = service.generatePDFForIvf(dto);
		if (obj != null && obj[1] != null) {
			ByteArrayOutputStream o = (ByteArrayOutputStream) obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + dto.getFileName().toString().replaceAll(",", "") + ".pdf"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}

	}
	
	@PostMapping(value = "/api/download-attachment-file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> attachmentFile(@RequestBody AttachmentDownloadDto dto, HttpServletResponse response) {
		Object[] data = null;
		FileInputStream input = null;
		String fileName = null;
		String fullPath = null;
		String msg = null;
		if(dto.getFileId()==null) {
			return ResponseEntity
					.ok(new GenericResponse(HttpStatus.BAD_REQUEST, MessageConstants.EMPTY_RESOURCE, null));
		}
		try {
			data = attachmentServiceImpl.getAttachmentFile(dto.getFileId());
			fullPath = (String) data[0];
			fileName = (String) data[1];
			if (fullPath != null && fileName != null) {
				input = new FileInputStream(fullPath);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", String.format("attachment; filename=" + fileName + " "));
				StreamUtils.copy(input, response.getOutputStream());
				response.flushBuffer();
				input.close();
			} else
				msg = MessageConstants.SOMETHING_WENT_WRONG;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR, "", null));
		}
		return ResponseEntity.ok(new GenericResponse(HttpStatus.OK, "", msg));
	}
	
	@PostMapping
	@RequestMapping(value = "/api/other-teams-work/d/pdf")
	public void generatePDF(@RequestBody OthersTeamWorkDownloadDto dto, HttpServletResponse response) throws IOException {

		Object[] obj=null; 
	     obj = service.generatePdfForOthersTeamWorks(dto);
		if (obj != null && obj[1]!=null) {
			ByteArrayOutputStream o =(ByteArrayOutputStream)  obj[1];
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment; filename="+dto.getFileName().toString().replaceAll(",", "")+ ".pdf"));
			//response.setHeader("Content-Disposition", String.format("attachment; filename="+obj[0] +".html"));
			InputStream in = new ByteArrayInputStream(o.toByteArray());
			org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			o.close();
		}
		

	}
}
