package com.tricon.rcm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.tricon.rcm.dto.download.AllPendancyDownloadDto;
import com.tricon.rcm.dto.download.ClaimDetailsDownloadDto;
import com.tricon.rcm.dto.download.IssueClaimDownloadDto;
import com.tricon.rcm.dto.download.ListOfClaimDownloadDto;
import com.tricon.rcm.dto.download.PendancyDownloadDto;
import com.tricon.rcm.dto.download.ProductionDownloadDto;



public class DtoToXmlConverted {
	
	public  String convertToXMLForListOfClaims(ListOfClaimDownloadDto dto, String dir ) throws Exception{
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(ListOfClaimDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;

	}
	
	public byte[] createHtml(String xmlPath, String xslPath)
		    throws IOException, TransformerException {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    Writer writer = new OutputStreamWriter(baos);
		    StreamSource xml = new StreamSource(new File(xmlPath));
		    StreamSource xsl = new StreamSource(new File(xslPath));
		    TransformerFactory factory = TransformerFactory.newInstance();
		    Transformer transformer = factory.newTransformer(xsl);
		    transformer.transform(xml, new StreamResult(writer));
		    writer.flush();
		    writer.close();
		    return baos.toByteArray();
		}
	public ByteArrayOutputStream createHtmlOut(String xmlPath, String xslPath)
		    throws IOException, TransformerException {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    Writer writer = new OutputStreamWriter(baos);
		    StreamSource xml = new StreamSource(new File(xmlPath));
		    StreamSource xsl = new StreamSource(new File(xslPath));
		    TransformerFactory factory = TransformerFactory.newInstance();
		    Transformer transformer = factory.newTransformer(xsl);
		    transformer.transform(xml, new StreamResult(writer));
		    writer.flush();
		    writer.close();
		    return baos;
	}
	
	public ByteArrayOutputStream createPdfStream(byte[] html, String baseUri) throws IOException {
		ByteArrayOutputStream st= new ByteArrayOutputStream();
		
	    ConverterProperties properties = new ConverterProperties();
	    ByteArrayInputStream bh=null;
	    try {
	    properties.setBaseUri(baseUri);
	    bh= new ByteArrayInputStream(html);
	    HtmlConverter.convertToPdf(
	    		bh,st, properties);
	    }finally {
	    	if (bh!=null) bh.close();
	    }
	    return st;
	}
	
	public  String convertToXMLForIssueClaim(IssueClaimDownloadDto dto, String dir ) throws Exception{
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(IssueClaimDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;

	}

	public String convertToXMLForclaimDetails(ClaimDetailsDownloadDto dto, String dir) throws Exception{
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(ClaimDetailsDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;
	}

	public String convertToXMLForProduction(ProductionDownloadDto dto, String dir)throws Exception {
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(ProductionDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;
	}
	public String convertToXMLForPendancy(PendancyDownloadDto dto, String dir) throws Exception {
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(PendancyDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;
	}
	public String convertToXMLForAllPendancy(AllPendancyDownloadDto dto, String dir)throws Exception {
		String filePath=dir+dto.getFileName().replaceAll("/", "_")+".xml";
		JAXBContext contextObj = JAXBContext.newInstance(AllPendancyDownloadDto.class);
		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(filePath));
		return 	filePath;
	}

}
