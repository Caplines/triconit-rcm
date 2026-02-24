package com.tricon.ruleengine.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.bind.JAXBContext;  
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.tricon.ruleengine.dto.CaplineIVFFormDto;

public class CaplineIVFFormDtoToXML {
	
	public  String convertToXML(CaplineIVFFormDto dto, String dir ) throws Exception{
		String filePath=dir+dto.getBasicInfo21()+"_"+dto.getDate().replaceAll("/", "_")+".xml";
		File outputFile = new File(filePath);
		File parentDir = outputFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}
		JAXBContext contextObj = JAXBContext.newInstance(CaplineIVFFormDto.class);

		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(outputFile));
		return 	filePath;

		
	}
	
	public  String convertToXML(SelantPdfMainDto dto, String dir ) throws Exception{
		String filePath=dir+new Date().getTime()+".xml";
		File outputFile = new File(filePath);
		File parentDir = outputFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}
		JAXBContext contextObj = JAXBContext.newInstance(SelantPdfMainDto.class);

		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(dto, new FileOutputStream(outputFile));
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
	/*
	public void createPdf(byte[] html, String baseUri, String dest) throws IOException {
	    ConverterProperties properties = new ConverterProperties();
	    properties.setBaseUri(baseUri);
	    HtmlConverter.convertToPdf(
	        new ByteArrayInputStream(html), new FileOutputStream(dest), properties);
	}
*/
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
}
