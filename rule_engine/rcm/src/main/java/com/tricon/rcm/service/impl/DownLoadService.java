package com.tricon.rcm.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.download.ClaimListDownloadDto;
import com.tricon.rcm.util.DtoToXmlConverted;


@Service
public class DownLoadService {
	
	@Value("${list_claim.xslt.file}")
	private String LIST_CLAIM_XSLT_FILE;
	
	@Value("${rcm.xslt.path}")
	private String XSLT_PATH;
	
	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	
	public Object[]  generatePdf(ClaimListDownloadDto data) {
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXML(data, XSLT_PATH);
		File file = new File(filePath);
		String xslt=LIST_CLAIM_XSLT_FILE;
		o= xml.createPdfStream(

				xml.createHtml(filePath, xslt), "");
	    
		if (file!=null) file.delete(); 
		
		obj[1]=o;
		}catch(Exception c) {
			
		}
		return obj;
	}
	
	
	public Object[]  generateClaimPdf(String claimUuid,PartialHeader partialHeader) {
		
		
		
		
		FreshClaimDataImplDto dto = claimServiceImpl.fetchIndividualClaim(claimUuid, partialHeader, true);
		
		
		
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		/*
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXML(data, XSLT_PATH);
		File file = new File(filePath);
		String xslt=LIST_CLAIM_XSLT_FILE;
		o= xml.createPdfStream(

				xml.createHtml(filePath, xslt), "");
	    
		if (file!=null) file.delete(); 
		
		obj[1]=o;
		}catch(Exception c) {
			
		}*/
		return obj;
	}

}
