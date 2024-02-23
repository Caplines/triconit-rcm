package com.tricon.rcm.api.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tricon.rcm.service.impl.ClaimServiceImpl;

import io.swagger.annotations.ApiOperation;

@Controller
@CrossOrigin
public class NOAuthController {

	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	@ApiOperation(value = "Api for Viewing EOB -Pdf Link of Claim", response = String.class)
	@GetMapping("/api/vieweoblink/{name}")
	public void viewEobLink(@PathVariable("name") String fileName,HttpServletResponse response) {
		//PartialHeader partialHeader = (PartialHeader) model.getAttribute("headerInfo");
		//if (partialHeader != null) {
			try {
				InputStream in =claimServiceImpl.viewEobLink(fileName,null);
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("attachment; filename="+fileName));
				org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
				response.flushBuffer();
				in.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//}
		
	}
}
