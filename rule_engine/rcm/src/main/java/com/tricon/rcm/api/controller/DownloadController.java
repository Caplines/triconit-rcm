package com.tricon.rcm.api.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.download.ClaimListDownloadDto;
import com.tricon.rcm.service.impl.DownLoadService;


@RestController
@CrossOrigin
public class DownloadController extends BaseHeaderController{

	@Autowired
	DownLoadService service;
	
	
	
	@PostMapping
	@RequestMapping(value = "/api/list-of-claim/d/pdf")
	public void generatePDF(@RequestBody ClaimListDownloadDto dto, HttpServletResponse response) throws IOException {
		//
		Object[] obj=null; 
	     obj = service.generatePdf(dto);
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
}
