<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
	<xsl:template match="/selantPdfMainDto">
<html>

<head>
                <style>
				body {font-family:sans-serif;}
				table {vertical-align:text-top;}
				.grid-container {
  display: grid;
  grid-template-columns: auto auto auto;
  background-color: #bbb;
 width:100%; 
 box-sizing:border-box;
 line-height:2px;
}
.br1px{ border-left: 1px solid #000;word-break:break-all;width:20px;border-bottom:1px solid #000;border-top:1px solid #000;}
.br21px{ border-left: 1px solid #000;word-break:break-all;width:20px;border-top:1px solid #000;}
.br31px{ border-left: 1px solid #000;word-break:break-all;width:20px;border-bottom:1px solid #000;}
.br41px{ border-left: 1px solid #000;word-break:break-all;width:20px;border-bottom:1px solid #000;}


.br2px{ border-right: 1px solid #000;}

.grid-item {
.grid-item {
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 10px;
  
  text-align: center;
  width:32%;
  float:left;
  box-sizing:border-box;
}
.main-heading-1 {font-family:helvetica;font-weight:regular;font-size:13px;}
.main-heading-11 {font-family:helvetica;font-weight:regular;font-size:11px}
.sub-heading {font-family:helvetica;font-size:13px;}
.sub-heading1 {font-family:helvetica;font-size:9px;}
.withds {width:8%}
.withdlastsections {width:15%}
.sub-headinglastsections1 {width:15%;font-family:helvetica;font-size:9px;}
.underline1{text-decoration:underline}
.colourb{color:#4472c4}
.colourp{color:#7030a0}
.colourrr{color:red}
.colourmagenta{color:#741b47}
.colourbr{color:#843c0b}
.colourgr{color:#385623}
.colourgrnewbg{background-color:#00ff00}
.colourpos{color:#1f3864}

.colourprev{background-color:#76a5af}
.colourperiom{background-color:#98c3b4}
.colourfreq{background-color:#fff2cc}
.colourexam{background-color:#a4c2f4}
.colouryell{background-color:#ffff00}
.colourhow{background-color:#f6b26b}
.colourwait{background-color:#e2f0dc}

.coloura1b{background-color:#c5e13a}
.coloura1t{color:#46623a}
.coloura2t{color:#b6a876}
.coloura3t{color:#629d48}
.coloura4t{color:#4fa949}
.coloura5t{color:#927c3a}
.coloura6t{color:#82813c}
.coloura7t{color:#60706a}
.coloura8t{color:#a0913c}

.br_but{border-bottom:1px solid #000;}
.border-btn tr:last-child td {border-bottom:1px solid #000; } 
td11 {
    border: 1px solid #000;
}
.tab_with_boder ,.tab_with_boder td {
border: 1px solid #000;
border-collapse: collapse;
}
/*.last-r-border tr td:last-child, .last-r-border tr th:last-child {border-right:1px solid #000;}*/
     
	 </style>
			<title>Sealant Eligibility Report</title>
			<meta name="description" content="Sealant Eligibility Report" />
			
		</head>
		

<body>
    <form  id="myIVForm" target="_top" >
	
        
        <div id="page_1">
		   
		    <span> 
			<p  style="text-align: right" class="colourmagenta"><b><xsl:value-of select="cDate"/> </b></p>
		    <p  style="text-align: center" class=""><b>Sealant Eligibility Report </b></p>
            </span>
			<br/>
			
			<span> 
		    <xsl:if test="dto/dto2">
            <p  style="text-align: center" class=""><b>Eligibility Completed for following Patients</b></p>
			</xsl:if>
            </span>
            <xsl:if test="dto/dto2">
            <table style="width:100%" cellspacing="0" cellpadding="0">
			<tr style="border:1px solid">
                      <th style="background-color:#0b5394;width:8%"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>Office</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:8%"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>Patient</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:15%"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>Patient</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:10%"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>IV</b></span>
					 </th>
                    <th style="background-color:#38761d;width:17%"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>Tooth # Eligible</b></span>
					 </th>
                      <th colspan="3" style="background-color:#660000;"  class="br21px">
                         <span class="main-heading-1" style="color: white;"><b>Tooth # Not Eligible for Sealants</b></span>
					 </th>
            </tr>
			<tr>
                     
					 <th style="background-color:#0b5394;width:8%"  class="br31px">
                         <span class="main-heading-1" style="color: white;"><b>Name</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:8%"  class="br31px">
                         <span class="main-heading-1" style="color: white;"><b>ID</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:15%"  class="br31px">
                         <span class="main-heading-1" style="color: white;"><b>Name</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:10%"  class="br31px">
                         <span class="main-heading-1" style="color: white;"><b>Date</b></span>
					 </th>
					 <th style="background-color:#38761d;width:17%"  class="br31px">
                         <span class="main-heading-1" style="color: white;"><b>for Sealant</b></span>
					 </th>
					 <th style="background-color:#f4cccc;"  class="br1px">
                         <span class="main-heading-1" ><b>Tooth Not Covered</b></span>
					 </th>
					 <th style="background-color:#f4cccc;"  class="br1px">
                         <span class="main-heading-1" ><b>Age Limitation</b></span>
					 </th>
					 <th style="background-color:#f4cccc;"  class="br1px br2px">
                         <span class="main-heading-1"><b>Frequency Limitation</b></span>
					 </th>
            </tr>
			<xsl:for-each select="dto/dto2">
				<tr >
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="name"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="ivDate"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="te"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="tne"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="tnea"/></span></td>
				<td class="br1px br2px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="tnef"/></span></td>
				</tr>
				</xsl:for-each>
			
            </table>

			
            
        	<br/>
			<br/>
			</xsl:if>
			
			<xsl:if test="dto73/dto73">
			<span>
			<p  style="text-align: center" class=""><b>Eligibility could not be completed for following Patients:</b></p>
            </span>
            </xsl:if>
			<xsl:if test="dto73/dto73">
            <table style="width:90%" cellspacing="0" cellpadding="0" >
			<tr style="border:1px solid">
                     <th style="background-color:#0b5394;width:8%">
                         <span class="main-heading-1" style="color: white;"><b>Office Name</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:8%">
                         <span class="main-heading-1" style="color: white;"><b>Patient ID</b></span>
					 </th>
                    <th style="background-color:#0b5394;width:15%">
                         <span class="main-heading-1" style="color: white;"><b>Patient Name</b></span>
					 </th>
                    <th style="background-color:#660000;width:10%">
                         <span class="main-heading-1" style="color: white;"><b>Error Message</b></span>
					 </th>
                    
            </tr>
			
			<xsl:for-each select="dto681/dto68">
				<tr >
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="name"/></span></td>
				<td class="br1px br2px"><span class="sub-heading"><xsl:value-of select="te" disable-output-escaping="yes"/></span></td>
				</tr>
		    </xsl:for-each>
			<xsl:for-each select="dto73/dto73">
				<tr >
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading" style="padding-left:2px"></span></td>
				<td class="br1px br2px"><span class="sub-heading" style="padding-left:2px"><xsl:value-of select="te"/></span></td>
				</tr>
		    </xsl:for-each>
            </table>

			</xsl:if>
			
			
			
			
			
			</div>
    </form>


</body>

</html>
</xsl:template>
</xsl:stylesheet>