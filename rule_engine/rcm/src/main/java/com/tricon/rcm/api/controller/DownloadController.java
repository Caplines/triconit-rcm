package com.tricon.rcm.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.download.AllPendancyDownloadDto;
import com.tricon.rcm.dto.download.ClaimDetailsDownloadDto;
import com.tricon.rcm.dto.download.IssueClaimDownloadDto;
import com.tricon.rcm.dto.download.ListOfClaimDownloadDto;
import com.tricon.rcm.dto.download.PendancyDownloadDto;
import com.tricon.rcm.dto.download.ProductionDownloadDto;
import com.tricon.rcm.service.impl.DownLoadService;


@RestController
@CrossOrigin
public class DownloadController extends BaseHeaderController{

	@Autowired
	DownLoadService service;
	
	
	
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
}
