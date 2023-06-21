package com.tricon.rcm.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.FreshClaimDataImplDto;
import com.tricon.rcm.dto.PartialHeader;
import com.tricon.rcm.dto.download.AllPendancyDownloadDto;
import com.tricon.rcm.dto.download.ClaimDetailsDownloadDto;
import com.tricon.rcm.dto.download.IssueClaimDownloadDto;
import com.tricon.rcm.dto.download.ListOfClaimDownloadDto;
import com.tricon.rcm.dto.download.PendancyDownloadDto;
import com.tricon.rcm.dto.download.ProductionDownloadDto;
import com.tricon.rcm.util.DtoToXmlConverted;


@Service
public class DownLoadService {
	
	@Value("${list_claim.xslt.file}")
	private String LIST_CLAIM_XSLT_FILE;
	
	@Value("${Issue_claim.xslt.file}")
	private String ISSUE_CLAIM_XSLT_FILE;
	
	@Value("${Claim_Details.xslt.file}")
	private String CLAIM_DETAILS_XSLT_FILE;
	
	@Value("${Production.xslt.file}")
	private String PRODUCTION_XSLT_FILE;
	
	@Value("${Pendancy.xslt.file}")
	private String PENDANCY_XSLT_FILE;
	
	@Value("${rcm.xslt.path}")
	private String XSLT_PATH;
	
	@Value("${All_Pendancy.xslt.file}")
	private String ALL_PENDANCY_XSLT_FILE;
	
	@Autowired
	ClaimServiceImpl claimServiceImpl;
	
	
	public Object[] generatePdfForListOfClaims(ListOfClaimDownloadDto data) {
		
		ByteArrayOutputStream o = null;
		Object[] obj=new Object[2]; 
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
		String filePath = xml.convertToXMLForListOfClaims(data, XSLT_PATH);
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
	
	public Object[] generatePdfForIssueClaim(IssueClaimDownloadDto data) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForIssueClaim(data, XSLT_PATH);
			File file = new File(filePath);
			String xslt = ISSUE_CLAIM_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}


	public Object[] generatePDFForclaimDetails(ClaimDetailsDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForclaimDetails(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = CLAIM_DETAILS_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}
	
	public Object[] generatePDFForProduction(ProductionDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForProduction(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = PRODUCTION_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}
	
	public Object[] generatePDFForPendancy(PendancyDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForPendancy(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = PENDANCY_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}


	public Object[] generatePDFForAllPendancy(AllPendancyDownloadDto dto) {
		ByteArrayOutputStream o = null;
		Object[] obj = new Object[2];
		DtoToXmlConverted xml = new DtoToXmlConverted();
		try {
			String filePath = xml.convertToXMLForAllPendancy(dto, XSLT_PATH);
			File file = new File(filePath);
			String xslt = ALL_PENDANCY_XSLT_FILE;
			o = xml.createPdfStream(
					xml.createHtml(filePath, xslt), "");
			if (file != null)
				file.delete();

			obj[1] = o;
		} catch (Exception c) {

		}
		return obj;
	}

}
