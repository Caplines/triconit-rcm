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
.br1px{ border: 1px solid #000;word-break:break-all;width:20px;}
.grid-item {
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 10px;
  
  text-align: center;
  width:32%;
  float:left;
  box-sizing:border-box;
}
.main-heading-1 {font-family:helvetica;font-weight:regular;font-size:13px}
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
			<title>PDF - IVF Form</title>
			<meta name="description" content="PDF - IVF Form" />
			
		</head>
		

<body>
    <form  id="myIVForm" target="_top" >
	
        
        <div id="page_1">
		   
		    <span> 
		    <p  style="text-align: center" class="colourmagenta"><b>Sealant Eligibility Report</b></p>
            </span>
			<br/>
			
			<span> 
		   
            <p  style="text-align: center" class="colourmagenta"><b>Eligibility Completed for following Patients</b></p>
            </span>
           
            <table style="width:90%" cellspacing="0" cellpadding="0">
			<tr style="border:1px solid">
                     <th style="background-color:blue;width:8%">
                         <span class="main-heading-1"><b>Office Name</b></span>
					 </th>
                    <th style="background-color:blue;width:8%">
                         <span class="main-heading-1"><b>Patient ID</b></span>
					 </th>
                    <th style="background-color:blue;width:15%">
                         <span class="main-heading-1"><b>Patient Name</b></span>
					 </th>
                    <th style="background-color:blue;width:10%">
                         <span class="main-heading-1"><b>IV Date</b></span>
					 </th>
                    <th style="background-color:green;width:15%">
                         <span class="main-heading-1"><b>Tooth # Eligible for Sealant</b></span>
					 </th>
                      <th colspan="3" style="background-color:maroon;">
                         <span class="main-heading-1"><b>Tooth # Not Eligible for Sealants</b></span>
					 </th>
            </tr>
			<tr>
                     <th colspan="4" style="background-color:blue;">
                     </th>
					 <th style="background-color:green;">
                     </th>
                      <th style="background-color:red;">
                         <span class="main-heading-1"><b>Tooth Not Covered</b></span>
					 </th>
					 <th style="background-color:red;">
                         <span class="main-heading-1"><b>Age Limitation</b></span>
					 </th>
					 <th style="background-color:red;">
                         <span class="main-heading-1"><b>Frequency Limitation</b></span>
					 </th>
            </tr>
			<xsl:for-each select="dto/dto2">
				<tr >
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="name"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="ivDate"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="te"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="tne"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="tnea"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="tnef"/></span></td>
				</tr>
				</xsl:for-each>
			
            </table>

			
            
        	<br/>
			<br/>
			
			<span>
			<p  style="text-align: center" class="colourmagenta"><b>Eligibility could not be completed for following Patients:</b></p>
            </span>
           
            <table style="width:90%" cellspacing="0" cellpadding="0">
			<tr style="border:1px solid">
                     <th style="background-color:blue;width:8%">
                         <span class="main-heading-1"><b>Office Name</b></span>
					 </th>
                    <th style="background-color:blue;width:8%">
                         <span class="main-heading-1"><b>Patient ID</b></span>
					 </th>
                    <th style="background-color:blue;width:15%">
                         <span class="main-heading-1"><b>Patient Name</b></span>
					 </th>
                    <th style="background-color:red;width:10%">
                         <span class="main-heading-1"><b>Error Message</b></span>
					 </th>
                    
            </tr>
			
			<xsl:for-each select="dto681/dto68">
				<tr >
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="name"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="te" disable-output-escaping="yes"/></span></td>
				</tr>
		    </xsl:for-each>
			<xsl:for-each select="dto73/dto73">
				<tr >
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="fName"/></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="patientId"/></span></td>
				<td class="br1px"><span class="sub-heading"></span></td>
				<td class="br1px"><span class="sub-heading"><xsl:value-of select="te"/></span></td>
				</tr>
		    </xsl:for-each>
            </table>

			
			
			
			
			
			
			</div>
    </form>


</body>

</html>
</xsl:template>
</xsl:stylesheet>