<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
   version="1.0" >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
	<xsl:template match="/caplineIVFFormDto">
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
.br1px{ border-left: 1px solid #000; border-top: 1px solid #000;}
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
.sub-heading {font-family:helvetica;font-size:9px;}
.sub-heading1 {font-family:helvetica;font-size:9px;}
.withds {width:3%}
.underline1{text-decoration:underline}
.colourb{color:#4472c4}
.colourp{color:#7030a0}
.colourrr{color:red}
.colourbr{color:#843c0b}
.colourpos{color:#1f3864}
.colourgr{color:#385623}
.br_but{border-bottom:1px solid #000;}
.border-btn tr:last-child td {border-bottom:1px solid #000; } 
/*.last-r-border tr td:last-child, .last-r-border tr th:last-child {border-right:1px solid #000;}*/
     
	 </style>
			<title>PDF - IVF Form</title>
			<meta name="description" content="PDF - IVF Form" />
			
		</head>
		

<body>
    <form  id="myIVForm" target="_top" >
	
        
        <div id="page_1">
		   <span> 
		   
            <p  style="text-align: center">Smilepoint - Insurance Verification Form</p>
            </span>
			
           <table  id="basic"  vertical-align="top">
                <tbody>
                    <tr>
                        <td>
                           <span class="main-heading-1">Claims Timely Fillings  </span>
						    
                        </td> 
                        <td><span class="sub-heading"><xsl:value-of select="percentages12"/></span></td>
                        <td colspan="6">
                         </td>
                        
                        <td>
                         <span class="main-heading-1">IVF ID:</span> <span class="sub-heading"><xsl:value-of select="basicInfo1"/>_<xsl:value-of select="id"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="withds">
                           <span class="main-heading-1">Office Name:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo1"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Patient Name:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo2"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Insurance Name:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo3"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Tax ID:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo4"/></span>
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Policy Holder:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo5"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Patient DOB:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo6"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Insurance Contact:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo7"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">CSR Name:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo8"/></span>
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Policy Holder DOB:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo9"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Employer Name:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo10"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Continued/Recall/NP:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo11"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">REF #:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo12"/></span>
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Member SSN:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo13"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Group #:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo14"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">COB Status:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo15"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Patient ID:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo21"/></span>
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Member ID:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo16"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Appointment:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo17"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Payer ID:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo18"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Provider Last Name:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo19"/></span>
                        </td>
                    </tr>
					<tr>
                        <td  class="withds">
                          <span class="main-heading-1"> Insurance Address:</span>
						</td>
                        <td  colspan="7">						
						  <span class="sub-heading"><xsl:value-of select="basicInfo20"/></span>
                            
                        </td>
                    </tr>
					
                    
                </tbody>
            </table>
            <p id="errors" style="color:red;font-weight:200;"></p>
            <p class="p3 ft1 main-heading-1">Policy/Plan Information</p>
            <table cellpadding="5" cellspacing="2" class="t1" id="policy">
                <tbody>
				   <!--
                    <tr>
                        <td colspan="2">
                            <p class="p2 ft4"><b>Plan Type :</b>
                                <span class="sub-heading"><xsl:value-of select="policy1"/>policy17 CRA Req.
                            </p>
                        </td>
                          <td>
                            <p class="p0 ft5">
                                D0120:
                                <span class="sub-heading"><xsl:value-of select="policy18"/>
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">
                                D2391:
                                <span class="sub-heading"><xsl:value-of select="policy19"/>
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">
                                Find FS:
                                <span class="sub-heading"><xsl:value-of select="policy20"/>
                            </p>
                        </td>
                                
                    </tr>
					-->
                    <tr>
                        <td class="main-heading-1">
                            Plan Type:
						</td>
                        <td> 						
                          <span class="sub-heading"><xsl:value-of select="policy1"/></span>
                           
                        </td>
                        <td class="main-heading-1">
                                Termed Date:
						</td>
                        <td> 						
                                <span class="sub-heading"><xsl:value-of select="policy2"/></span>
                        </td>
                        <td class="main-heading-1">
                                Network:
						</td>
                        <td> 						
								<span class="sub-heading"><xsl:value-of select="policy3"/></span>
                        </td>
                        <td class="main-heading-1">
							Fee Schedule:
 						</td>
                        <td> 						
								<span class="sub-heading"><xsl:value-of select="policy4"/></span>
                        </td>
                        <td class="main-heading-1">
                            Effective Date:
						</td>
                        <td> 						
                                <span class="sub-heading"><xsl:value-of select="policy5" /></span>
                        </td>
                        <td class="main-heading-1">
                            
							Cal.Yr/Fiscal Yr/Plan Yr. :
                      	</td>
                        <td> 						
                          <span class="sub-heading"><xsl:value-of select="policy6"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="main-heading-1">
                            Annual Max:
						</td>
                        <td> 						
                            
							<span class="sub-heading"><xsl:value-of select="policy7"/></span>
                        </td>
                        <td class="main-heading-1">
                           Ann. Max Rem:
   						</td>
                        <td> 						
                                <span class="sub-heading"><xsl:value-of select="policy8"/></span>
                        </td>
                        <td class="main-heading-1">
                            Ind. Ded:
                     	</td>
                        <td> 						
                         <span class="sub-heading"><xsl:value-of select="policy9"/></span>
                        </td>
                        <td class="main-heading-1">
                            Ind. Ded Rem:
						</td>
                        <td> 						
                                <span class="sub-heading"><xsl:value-of select="policy10"/></span>
                        </td>
                        <td class="main-heading-1">
                            Dependents Covered to age:
                        </td>
                        <td> 						
                            <span class="sub-heading"><xsl:value-of select="policy11"/></span>
                         </td>
                        <td class="main-heading-1">
                            Pre-D Mandatory:
                      	</td>
                        <td> 						
                          <span class="sub-heading"><xsl:value-of select="policy12"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="main-heading-1">
                            Non-Duplicate clause?
						</td>
                        <td> 						
							<span class="sub-heading"><xsl:value-of select="policy13"/></span>
                        </td>
                        <td  class="main-heading-1" colspan="2">
						
                            Full Time Student Status Required?
						</td>
                        <td> 						
                                
								<span class="sub-heading"><xsl:value-of select="policy14"/></span>
                        </td>
                        <td class="main-heading-1" colspan="2">
                            Assignment of Benefits Accepted?
						</td>
                        <td> 						
                            
							<span class="sub-heading"><xsl:value-of select="policy15"/></span>
                        </td>
                        <td class="main-heading-1">
                                Coverage Book:
						</td>
                        <td> 						
                                <span class="sub-heading"><xsl:value-of select="policy16"/></span>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            <br/>
			<p class="p3 ft1 main-heading-1">Items to be Checked Manually by Treatment Planner and LC3</p>
            
			
			
            <table style="width: 90%" id="rules" cellpadding="0">
                <colgroup>
                    <col width="12.5%"/>
                    <col width="12.5%"/>
                    <col width="12.5%"/>
                    <col width="8.5%"/>
                    <col width="10.5%"/>
                    <col width="18.5%"/>
                    <col width="12.5%"/>
                    <col width="12.5%"/>
                </colgroup>
                <tbody>
                    <tr>
                        <td class="main-heading-11 colourgr"><span class="underline1">X-Rays</span>(Frequency)</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourb underline1">Sealants(D1351)</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealantsD"/></span></td>
                        <td class="main-heading-11 colourp underline1">Perio</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourp withds"><span class="underline1">Perio</span> Mnt.(D4910): %</td>
						<td><span class="sub-heading1"><xsl:value-of select="perioMnt1"/></span></td>
						<td class="main-heading-11 colourgr">Dentures (Frequency)</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourgr">BWX:</span><xsl:value-of select="xrays1"/><br/>
						<span class="colourgr">FMX (D0210):</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="xrays4"/></span></td>
                        <td class="main-heading-11 colourb">Frequency:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealants1"/></span></td>
                        <td class="main-heading-11 colourp">SRP (D4341)%:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="perio1"/></span></td>
                        <td class="main-heading-11 colourp withds">Frequency: %</td>
						<td><span class="sub-heading1"><xsl:value-of select="perioMnt2"/></span></td>
						<td class="main-heading-11 colourgr">Complete (D5110/D5120):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="dentures1"/></span></td>
                    </tr>
					<tr>
                        <td colspan="4" class="main-heading-11 withds"><span class="colourgr"></span>
						<span class="sub-heading1">
						</span>
						</td>
                        <td colspan="2" class="main-heading-11 colourgr"></td>
                        <td colspan="2"></td>
                        <td colspan="2"><span class="main-heading-11 colourgr">Denture(fr)(D5225):</span>
						<span class="sub-heading1"><xsl:value-of select="denf5225"/></span></td>
                    </tr>
					<tr>
                        <td colspan="4" class="main-heading-11 withds"><span class="colourgr"></span>
						<span class="sub-heading1"></span>
						</td>
						<td colspan="2" class=""></td>
						
                        <td colspan="2" class="main-heading-11 colourgr"></td>
                        <td colspan="2"><span class="main-heading-11 colourgr">Denture(fr)(D5226):</span>
						<span class="sub-heading1"><xsl:value-of select="denf5226"/></span></td>
                        
                    </tr>
					
                    <tr>
                        <td colspan="1" class="main-heading-11 withds"><span class="colourgr">Will downgrade applicable?:</span>
						</td>
                        <td class="sub-heading1 colourgr"><xsl:value-of select="cdowngrade"/></td>
                        <td></td>
                        <td colspan="7" class="main-heading-11 colourgr">
						  </td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourgr withds"><span class="colourgr">PA(D0220):</span><xsl:value-of select="xrays2"/><br/>
						<span class="colourgr">D0230:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="xrays3"/></span></td>
                        <td class="main-heading-11 colourb">Age Limit:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealants2"/></span></td>
                        <td class="main-heading-11 colourp">Frequency:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="perio2"/></span></td>
                        <td class="main-heading-11 colourp withds">Alt. with Prophy (D1110):</td>
						<td><span class="sub-heading1"><xsl:value-of select="perioMnt3"/></span></td>
						<td class="main-heading-11 colourgr">Immediate (D5130/D5140):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="dentures2"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourgr withds"><span class="colourgr">Bundling:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="xrays5"/></span></td>
                        <td class="main-heading-11 colourb">Coverage:</td>
                        <td><span class="sub-heading1"><!--<xsl:value-of select=""/>--></span></td>
                        <td class="main-heading-11 colourp">Quads Per Day:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="perio3"/></span></td>
                        <td class="main-heading-11 withds"><span class="colourp">FMD (D4355)%:</span><xsl:value-of select="perioMnt4"/><br/>
						<span class="colourp">Frqncy </span></td>
						<td><span class="sub-heading1"><xsl:value-of select="perioMnt2"/></span></td>
						<td class="main-heading-11 colourgr">Partial (D5213/D5214):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="dentures3"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourrr withds"><span class="colourrr">Fluroide (D1208):</span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourb">Primary-Molars:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealants3"/></span></td>
                        <td class="main-heading-11 colourp">Days b/w Quads:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="perio4"/></span></td>
                        <td class="main-heading-11 withds"><span class="colourp">Gingivitis (D4346)%:</span><xsl:value-of select="perioMnt6"/><br/>
						<span class="colourp">Freq.</span></td>
						<td><span class="sub-heading1"><xsl:value-of select="perioMnt7"/></span></td>
						<td class="main-heading-11 colourgr">Interim Partial (D5820):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="dentures4"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourrr withds"><span class="colourrr">Frqncy:</span>
						<span class="sub-heading1"><xsl:value-of select="fluroide1"/></span><span class="colourrr">Age Lmt:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="fluroide2"/></span></td>
                        <td class="main-heading-11 colourb">Pre-Molars:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealants4"/></span></td>
                        <td class="main-heading-11 colourp"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 withds"></td>
						<td><span class="sub-heading1"></span></td>
						<td class="main-heading-11 colourgr"></td>
                        <td></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourrr withds"><span class="colourrr">Varnish (D1206):</span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourb">Perm-Molars:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sealants5"/></span></td>
                        <td class="main-heading-11 colourgr"><span class="underline1">Prophy (Frequency):</span></td>
                        <td><span class="sub-heading1"><xsl:value-of select="prophy1"/></span></td>
                        <td class="main-heading-11 withds"><span class="colourp">D1120:</span>
						</td>
						<td><span class="sub-heading1"><xsl:value-of select="prophy2"/></span></td>
						<td class="main-heading-11 colourgr">1120/1110 Roll Age:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="rollage"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 colourrr withds"><span class="colourrr">Frqncy:</span>
						<span class="colourrr"><xsl:value-of select="fluroide3"/></span>
						<span class="colourrr">Age Lmt:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="fluroide4"/></span></td>
                        <td class="main-heading-11 colourb"></td>
                        <td></td>
                        <td class="main-heading-11 colourgr"></td>
                        <td></td>
                        <td class="main-heading-11 withds">
						</td>
						<td></td>
						<td class="main-heading-11 colourgr"></td>
                        <td></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Exams(Frequency):</span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourgr"><span class="underline1">SSC</span> Frequency</td>
                        <td></td>
                        <td class="main-heading-11 colourb"><b><span class="underline1">Oral Surgery</span></b></td>
                        <td></td>
                        <td class="main-heading-11 withds colourb"><span class="underline1"></span></td>
						<td></td>
						<td class="main-heading-11 colourbr"><span class="underline1">Prosthetics</span></td>
                        <td></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">D0120:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="exams1"/></span></td>
                        <td class="main-heading-11 colourgr"> D2930:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="ssc1"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourp">Crown Lengthening (D4249)%:</span>
                          <span class="sub-heading1"><xsl:value-of select="oral1"/></span>	
						  <span class="colourp">Frqncy:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="oral2"/></span></td>
                        <td class="main-heading-11 colourbr">Missing Tooth Clause:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="prosthetics1"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">D0140:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="exams2"/></span></td>
                        <td class="main-heading-11 colourgr">D2931:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="ssc2"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourp">Bone Graft (D7953) Covered w.EXT:</span>
                          <span class="sub-heading1"><xsl:value-of select="dentures5"/></span>	
						  <span class="colourp">Frqncy:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="dentures6"/></span></td>
                        <td class="main-heading-11 colourbr">Replacement Clause:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="prosthetics2"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">D0145:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="exams3"/></span></td>
                        <td class="main-heading-11 colourgr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourp">Alveolplasty:</span>
                          <span class="sub-heading1"></span>	
						  <span class="colourp"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourbr">Crowns<span class="sub-heading1">(D2740/D2750)Freq</span>:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior5"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">D0150:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="exams4"/></span></td>
                        <td class="main-heading-11 colourgr">Posterior Composites (D2391)%:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior1"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourp">1-3 Teeth/Qd (D7311): Covered w. Ext:</span>
                          <span class="sub-heading1"><xsl:value-of select="oral3"/></span>	
						  <span class="colourp">Frequency:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="oral4"/></span></td>
                        <td class="main-heading-11 colourbr">Paid Prep / Seat Date:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="prosthetics3"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr underline1"><b>Waiting Periods:</b></span>
						<span class="colourrr">(months)</span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourgr">Frequency:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior2"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourp">4 Teeth/Qd (D7310): Covered w. Ext:</span>
                          <span class="sub-heading1"><xsl:value-of select="oral5"/></span>	
						  <span class="colourp">Frequency:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="oral6"/></span></td>
                        <td class="main-heading-11 colourbr"></td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr">Basic:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="waitingPeriod1"/></span></td>
                        <td class="main-heading-11 colourrr">Fillings (Bundle):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="fillings"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourgr">Ortho(D8080/D8090) Age Limit:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="ortho3"/></span></td>
                        <td class="main-heading-11"><span class="colourbr">Build-Up (D2950) %:</span>
						<span class="sub-heading1"><xsl:value-of select="posterior10"/></span>
						<span class="colourbr">Frequency:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior11"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr">Major:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="waitingPeriod2"/></span></td>
                        <td class="main-heading-11 colourrr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="4">
                         </td>
                        <td class="main-heading-11"><span class="colourbr">Same day as Crown:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior12"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr">Ortho:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="waitingPeriod3"/></span></td>
                        <td class="main-heading-11 colourrr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="4">
                         </td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
					<td colspan="10"><span class="p3 ft1 main-heading-1">Items Handled by Eaglesoft Automatically</span>
					</td>
					</tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr">Preventative %:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages9"/></span></td>
                        <td class="main-heading-11 colourrr"><span class="underline1">Extractions</span>: %</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourgr">Ortho (%): D8080, D8090,D8070:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="ortho1"/></span></td>
                        <td class="main-heading-11"><span class="colourbr"><span class="underline1">Sealants</span> D1351(%):</span>
						<span class="sub-heading1"><xsl:value-of select="sealantsD"/></span>
						<span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourrr">Subject to ded:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages13"/></span></td>
                        
                        <td colspan="8"><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Diagnostic %:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages10"/></span></td>
                        <td class="main-heading-11 colourrr"><span><b>Minor</b></span>: (D7111, D7140):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="extractions1"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourgr">Ortho Max:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="ortho2"/></span></td>
                        <td class="main-heading-11"><span class="colourgr">Ortho Remaining:</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="ortho5"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Subject to ded:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages14"/></span></td>
                        <td colspan="8"><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">PA(%):</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages11"/></span></td>
                        <td class="main-heading-11 colourrr"><span><b>Major</b></span>: (D7210, D7220, D7230,D7240):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="extractions2"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourgr">Subject to Deductible:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="ortho4"/></span></td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Subject to ded:</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages15"/></span></td>
                        
                        
						<td class="main-heading-11"><span class="colourb">FMX(%)</span>
						
						</td>
						<td><span class="sub-heading1"><xsl:value-of select="percentages16"/></span></td>
						<td colspan="6"><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td colspan="4" class="main-heading-11 withds"><span class="colourgr">Denture(%)(D5225):</span>
						<span class="sub-heading1">
						<xsl:value-of select="den5225"/></span>
						</td>
                        <td colspan="2" class="main-heading-11 colourgr"></td>
                        <td colspan="2"></td>
                        <td colspan="2"><span class="main-heading-11 colourgr"></span>
						<span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td colspan="4" class="main-heading-11 withds"><span class="colourgr">Denture(%)(D5226):</span>
						<span class="sub-heading1"><xsl:value-of select="den5226"/></span>
						</td>
						<td colspan="2" class=""></td>
						
                        <td colspan="2" class="main-heading-11 colourgr"></td>
                        <td colspan="2"><span class="main-heading-11 colourgr"></span>
						<span class="sub-heading1"></span></td>
                        
                    </tr>
					<tr>
                        <td colspan="10" class="main-heading-11 withds"><span class="colourgr">Bridges(%)</span>
						</td>
                    </tr>
					<tr>
                        <td colspan="3" class="main-heading-11 withds"><span class="colourgr">(D6245 / D6740):</span>
						<span class="sub-heading1"><xsl:value-of select="bridges1"/></span>
						</td>
                        <td colspan="1" class="main-heading-11 colourgr">Frequency:</td>
                        <td><span class="sub-heading1"><xsl:value-of select="bridges2"/></span></td>
                        <td colspan="6" class="main-heading-11 colourgr">
						  </td>
                    </tr>
					
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb"></span>
						
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11 colourrr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="3">
                         </td>
                        <td></td>
                        <td class="main-heading-11"><span class="colourb">IV Sedation</span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourgr">Basic (%):</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages1"/></span></td>
                        <td class="main-heading-11 colourb"><span class="underline1">Sedation (%)</span>Nitrous (D9230):</td>
                        <td><span class="sub-heading1"><xsl:value-of select="sedations1"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourb">IV Sedation (D9243):</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="sedations2"/></span></td>
                        <td class="main-heading-11"><span class="colourb"> (D9248):</span>
						<span class="sub-heading1"><xsl:value-of select="sedations3"/></span>
						<span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourgr">Subject to Ded</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages2"/></span></td>
                        <td class="main-heading-11 colourrr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="4">
                         </td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourp">Major (%):</span>
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages3"/></span></td>
                        <td class="main-heading-11 colourrr"></td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="4">
                         </td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Subject to Ded:</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages4"/></span></td>
                        <td class="main-heading-11 colourp">Implants Coverage (%)</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourpos"></span>
                         </td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Endo (%):</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages5"/></span></td>
                        <td class="main-heading-11 colourp">Implants D6010:
						<span class="sub-heading1"><xsl:value-of select="implants1"/></span>
						<span class="main-heading-11">D6057:</span>
						<span class="sub-heading1"><xsl:value-of select="implants2"/></span>
						<span class="main-heading-11">D6190:</span>
						<span class="sub-heading1"></span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants3"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourpos">Downgraded to Amalgam(D2140):</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior3"/></span></td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourb">Subject to Ded:</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages6"/></span></td>
                        <td class="main-heading-11 colourp">Implant Supported Porc./Ceramic(D6065):
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants4"/></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourpos">Crowns (D2740 / D2750) %:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior4"/></span></td>
                        <td class="main-heading-11"><span class="colourbr"></span>
						</td>
                        <td><span class="sub-heading1"></span></td>
                    </tr>
					
					<tr>
                        <td class="main-heading-11 withds"><span class="colourp">Implants Fr(D6010):</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants5"/></span></td>
                        <td class="main-heading-11 colourp">Implants Fr(D6057):
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants6"/></span></td>
                        <td colspan="4"></td>
						
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourp">Implants Fr(D6065):</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants7"/></span></td>
                        <td class="main-heading-11 colourp">Implants Fr(D6190):
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="implants8"/></span></td>
                        <td colspan="4"></td>
						
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourgr">Perio Surgery(%):</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages7"/></span></td>
                        <td class="main-heading-11 colourp">
						</td>
                        <td><span class="sub-heading1"></span></td>
                        <td class="main-heading-11" colspan="3"><span class="colourpos">Will Crown Downgrade or not?:</span>
                         </td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior6"/></span></td>
                        <td class="main-heading-11"><span class="colourbr">Which code?</span>
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior17"/></span></td>
                    </tr>
					<tr>
                        <td class="main-heading-11 withds"><span class="colourgr">Subject to Ded:</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="percentages8"/></span></td>
                        <td class="main-heading-11 colourb">Consult (D9310) %:
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="posterior8"/></span></td>
                        <td class="main-heading-11" colspan="1"><span class="colourb">Frequency</span>
                         </td>
						 <td class="sub-heading1" colspan="1"><xsl:value-of select="posterior9"/>
                         </td>
                        <td><span class="main-heading-11 colourpos">Night Guards (D9944) %:</span></td>
                        <td class="sub-heading1"><span class="colourbr"><xsl:value-of select="posterior7"/></span>
						</td>
                        <td><span class="main-heading-11 colourpos">Night Gaurds (D9945) %:</span></td>
						<td><span class="sub-heading1"><xsl:value-of select="posterior18"/></span></td>
                    </tr>
                    
                    <tr>
                        <td class="main-heading-11 withds"><span class="">D0120:</span> 
						
						</td>
                        <td><span class="sub-heading1"><xsl:value-of select="policy18"/></span></td>
                        <td class="colourb">
						</td>
                        
                        <td class="main-heading-11" colspan="1"><span class="">D2391:</span>
                         </td>
						 <td class="sub-heading1" colspan="1"><xsl:value-of select="policy19"/>
                         </td>
                        <td><span class="main-heading-11 colourpos">Night Gaurds(D9944)FR:</span></td>
                        <td class="sub-heading1"><xsl:value-of select="posterior19"/>
						</td>
						<td><span class="sub-heading1"></span></td>
                        <td><span class="main-heading-11 colourpos">Night Gaurds(D9945)FR:</span></td>
						<td><span class="sub-heading1"><xsl:value-of select="posterior20"/></span></td>
                    </tr>

                    
                </tbody>
            </table>
            
            <br/>

            <p class="p3 ft34 main-heading-11">Complete 5 Year History</p>
			
			 <xsl:variable name="hh" select="count(history/his)"/>
             <xsl:variable name="hhi" select="num[1]"/>			 
			 <xsl:variable name="tra2" select="history/his"/> 
			 
			<div class="classname_main">  
			<!-- 
			<div class='grid-container'>
				<div class="grid-item main-heading-11">ADA Code</div>
				<div class="grid-item main-heading-11">Tooth No.</div>
				<div class="grid-item main-heading-11">DOS</div>

				</div>
			-->	
						<table  style="border:0px" cellpadding="0" cellspacing="0">
				<tr>
					<td vertical-align="top" style="vertical-align:top;">
					<table  style="border-right:1px solid #000; page-break:avoid" class="border-btn" cellspacing="0">
                <tbody>
			    <tr class="">
			    <th class="main-heading-11 br1px br_but" style="width:25%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">DOS</th>
				</tr>
				<xsl:for-each select="hdto1/hisall1">
				<tr class="">
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
             </tbody>
            </table></td>
					
			<td vertical-align="top" style="vertical-align:top;">
			<table style="border-right:1px solid #000;  margin-left:-1px;"  class="border-btn" cellspacing="0" >
             <tbody>
			    <tr>
			    <th class="main-heading-11 br1px br_but" style="width:25%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">DOS</th>
				</tr>
				<xsl:for-each select="hdto2/hisall2">
				<tr>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
				
             </tbody>	
            </table>
					</td>
					<td vertical-align="top" style="vertical-align:top;">
				<table style="border-right:1px solid #000; margin-left:-1px;" class="border-btn last-r-border" cellspacing="0">
             <tbody>
			    <tr>
			    <th class="main-heading-11 br1px br_but" style="width:25%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:25%">DOS</th>
				</tr>
				<xsl:for-each select="hdto3/hisall3">
				<tr>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
             </tbody>
            </table>
					</td>
				</tr>
			</table>
			
			
			 </div>

            <table cellpadding="0" cellspacing="0" style="width:100%; clear:both;">
                <tbody>
				<tr>
                        <td colspan="2" class="main-heading-11">
                            Comments:
							<div>
							<span class="sub-heading">
							<textarea id="comments" name="comments" rows="20" cols="48">
							<xsl:value-of select="comments"/>&#160;
							</textarea>
							</span>
							</div>
                             
                        </td>
                        
                    </tr>
                    <tr>
                        <td>
                           <span class="main-heading-11"> Benefits Verified by:</span>
							<span class="sub-heading"><xsl:value-of select="benefits"/></span>
                             
                        </td>
                        <td style="text-align:right">
                          <span class="sub-heading">Date:</span>
							<span class="sub-heading"><xsl:value-of select="date" /></span>

                           
                        </td>
                    </tr>
                   
                    
                </tbody>
            </table>
        </div>
    </form>


</body>

</html>
</xsl:template>
</xsl:stylesheet>