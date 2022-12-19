package com.tricon.ruleengine.pdf;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Date;

public class CreatePDFDocument {
	
	
	public static void main(String... args) throws FileNotFoundException, DocumentException {

		
		
        // create document
        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\Project\\Tricon\\RuleEngine(Capline Dental Management)\\capline\\pdf\\HeaderFooter.pdf"));

        // add header and footer
        HeaderFooterPageEvent event = new HeaderFooterPageEvent("D:\\Project\\Tricon\\RuleEngine(Capline Dental Management)\\capline\\pdf\\capline-logo.png",
        		"Report for Treatment plan","http://18.223.22.149");
        writer.setPageEvent(event);

        // write to document
        document.open();
        Paragraph p=new Paragraph("Report for Treatment plan9");
        PdfPTable table = new PdfPTable(2);
        
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(10000);
        //table.setWidthPercentage(160 / 5.23f);
        PdfPCell cell = new PdfPCell(new Phrase(" Rule 1 "));
        //cell.setBackgroundColor(BaseColor.BLACK);
        cell.setBorderWidth(1f);
        table.addCell(cell);
        
        
        StringReader strReader = new StringReader("<bold>Hi everyone</bold><b>asdsd</b>");
        
        PdfPCell cell2 = new PdfPCell(new Phrase(" Data sd asdasdasdas <bold>dasdsa</bold> da dasd a dasd as das das dsad asd as dasd as dsadasd as"
        		+ "asd asd asd asd as dasd asd as dasdasdasd"));
        //cell2.setBackgroundColor(BaseColor.BLACK);
        cell2.setBorderWidth(1f);
        table.addCell(cell2);
        
        p.add(table);
        document.add(p);
               ///
        document.newPage();
        //document.add(new Paragraph(new Date()+"Footer to PDF Document using ."));
        document.close();
    }
	
	

}
