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
		   
            <p  style="text-align: center" class="colourmagenta"><b>Smilepoint - OS Insurance Verification Form</b></p>
            </span>
			
           <table  id="basic"  vertical-align="top">
                <tbody>
                    <tr> 
                        <td>
                           
						 </td> 
                        <td></td>
                        <td colspan="6">
                         </td>
                        
                        <td>
                         <span class="main-heading-1">IVF ID:</span> 
                        </td>
						<td>
						<span class="sub-heading"><xsl:value-of select="basicInfo1"/>_<xsl:value-of select="id"/></span>
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
                           <span class="main-heading-1">Patient ID:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo21"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Suscriber's Name:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo5"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Suscriber's DOB:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo9"/></span>
                        </td>
						<td>
                         <span class="main-heading-1">Patient's Name:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading" ><xsl:value-of select="basicInfo2"/> </span>
                        </td>
                    </tr>
                       <tr>
                        <td class="withds">
                           <span class="main-heading-1">Patient's DOB:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo6"/> </span>
                        </td>
                        <td colspan="8">						 
						 
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">CSR Name:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo8"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Ref#:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo12"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">Provider's Name:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo19"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Tax ID:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo4"/></span>
                        </td>
						<td>
                         <span class="main-heading-1">NPI:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading" ><xsl:value-of select="npi"/> </span>
                        </td>
                    </tr>
                       <tr>
                        <td class="withds">
                           <span class="main-heading-1">Licence #:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="licence"/> </span>
                        </td>
                        <td colspan="8">						 
						 
                        </td>
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Plan Type:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="policy1"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Network:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="policy3"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">SSN#</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo13"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Member ID:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo16"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Effective Date:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="policy5"/></span>
                        </td>
                    </tr>
                       <tr>
                        <td class="withds">
                           <span class="main-heading-1">CY/FY:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="policy6"/> </span>
                        </td>
                        <td colspan="8">						 
						 
                        </td>
                    </tr>

					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Employer's Name:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="basicInfo10"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Group Number:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="basicInfo14"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">Insurance Name#</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo3"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Insurance Telephone:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo7"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Insurance Address:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="basicInfo20"/></span>
                        </td>
                    </tr>
                       

					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Fee Schedule:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="policy4"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Dependents covered upto age?</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="policy11"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Coordination of benefits:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="corrdOfBenefits"/></span>
                        </td>
						<td>
                           <span class="main-heading-1">Payor ID:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo18"/></span>
                        </td>
                        <td colspan="3">
                         
                         </td>
                        					 
						
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">What is allowable amount for D7210:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="whatAmountD7210"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Maximum $</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="policy7"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Deductible:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="policy9"/></span>
                        </td>
						<td>
                           <span class="main-heading-1">Appointment Date:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="basicInfo17"/></span>
                        </td>
                        <td colspan="2">
                         
                         </td>
                        						 
						
                    </tr>
					<tr>
                        <td class="withds">
                           <span class="main-heading-1">What is allowable amount for D7240:</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="allowAmountD7240"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Remaining benefits $</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="policy8"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">Remaining Deductible:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="policy10"/></span>
                        </td>
                        <td>
                           <span class="main-heading-1">Waiting Period Duration:</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="waitingPeriodDuration"/></span>
                        </td
                        <td colspan="2">
                         
                         </td>
                        					 
						
                    </tr>

					<tr>
                        <td class="withds">
                           <span class="main-heading-1">Is there a MTC?</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="radio3"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Eligible for D3330?</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="radio4"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">Is there any waiting period?</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="radio5"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Out of Network Benefits:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="radio1"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Do you file OS under medical first?</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="radio2"/></span>
                        </td>
                    </tr>

					
                </tbody>
            </table>
            <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Exams</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
            <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D0140(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d0140"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="exams2"/></span>
						 </td>
                        
				</tr>
			</table>
			
          <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Extractions</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>

            <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7210(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7210"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7210fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D7220(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d7220"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d7220fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D7230(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7230"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7230fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7240(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7240"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7240fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D7250(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d7250"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d7250fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D7310(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7310"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7310fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7311(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7311"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7311fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D7320(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d7320"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d7320fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D7321(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7321"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7321fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7473(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7473"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7473fr"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">D7472(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7472"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7472fr"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">D7280(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7280"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7280fr"/></span>
						 </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7282(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7282"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7282fr"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">D7283(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7283"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7283fr"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">D7285(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7285"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7285fr"/></span>
						 </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7952(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7952"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7285fr"/></span>
						 </td>
						 <td colspan="8">
						   
						 </td>
                </tr>
            </table>
			
            <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Sedation</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>

            <table>
		        <tr>
                        <td class="withds">
                           <span class="main-heading-1">D9230(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="sedations1"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="sedations1fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D9248(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="sedations3"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="sedations3fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D9239(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d9239"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d9239fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D9243(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="sedations2"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="sedations2fr"/></span>
						 </td>
                        <td colspan="6">
                           <span></span>
						 </td>
                </tr>
        				
            </table>
            <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Bone Grafts</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D4263(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d4263"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d4263fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D4264(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d4264"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d4264fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D6104(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d6104"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d6104fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D7953(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d7953"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d7953fr"/></span>
						 </td>
                        <td colspan="6">
                           <span></span>
						 </td>
                </tr>
				
            </table>
            
            <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Endo</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D3310(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d3310"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d3310fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D3320(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d3320"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d3320fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D3330(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d3330"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d3330Freq"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D3346(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d3346"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d3346fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D3347(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d3347"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d3347fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D3348(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d3348"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d3348fr"/></span>
                        </td>
                </tr>
				
				
            </table>
                        <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Implants</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D6010(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="implants1"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="implants5"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D6057(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="implants2"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="implants6"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D6058(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d6058"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d6058fr"/></span>
                        </td>
                </tr>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1">D6190(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="implants3"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="implants8"/></span>
						 </td>
                        <td class="withds">
                           <span class="main-heading-1">D6114/D6115(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d6114"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d6114fr"/></span>
						 </td>
                        <td colspan="2">
                           <span></span>
						 </td>
                </tr>
				
            </table>
			
			 <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Perio</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
                <tr>
                        <td class="withds">
                           <span class="main-heading-1">D4249(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="oral1"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="oral1fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D7951(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d7951"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d7951fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D9310(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="posterior8"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="posterior9"/></span>
                        </td>
                </tr>
                <tr>
                        <td class="withds">
                           <span class="main-heading-1">D4266(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d4266"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d4266fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D4267(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d4267"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d4267fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D4341(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="perio1"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="perio1fr"/></span>
                        </td>
                </tr>
                 <tr>
                        <td class="withds">
                           <span class="main-heading-1">D4273(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d4273"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d4273fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D6065(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="implants4"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="implants7"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D7251(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7251"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d7251fr"/></span>
                        </td>
                </tr> 
                <tr>
                        <td class="withds">
                           <span class="main-heading-1">D5110/5120(%):</span> 
						</td>
                        <td>						
						   <span class="sub-heading" ><xsl:value-of select="d5110"/> </span>
                        </td>
                        <td class="withds">
                           <span class="main-heading-1">Frequency:</span>
						</td>
						<td>
						   <span class="sub-heading"><xsl:value-of select="d5110fr"/></span>
						 </td>
                        <td>
                           <span class="main-heading-1">D5130/5140(%):</span>
						 </td>
                        <td> <span class="sub-heading"><xsl:value-of select="d5130"/></span>
                        </td>
                        <td>
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td>						 
						 <span class="sub-heading"><xsl:value-of select="d5130fr"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">D5860/D5865(%):</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d5860"/></span>
                        </td>
						<td class="withds">
                         <span class="main-heading-1">Frequency:</span>
                         </td>
                        <td class="withds">						 
						 <span class="sub-heading"><xsl:value-of select="d5860fr"/></span>
                        </td>
                </tr>				
            </table>
			
			 <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Guidelines for IV Sedation:</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1"><xsl:value-of select="ivSedation"/></span> 
						</td>
                </tr>				
            </table>
			<br/>
			 <table>
			<tr>
                     <td>
                         <span class="colourmagenta"><b>Assignment of Benefits:</b></span>
					 </td>
                      <td  colspan="9"></td>
            </tr>
            </table>
           <table>
				<tr>
                        <td class="withds">
                           <span class="main-heading-1"><xsl:value-of select="policy15"/></span> 
						</td>
                </tr>				
            </table>
 			
			<br/>
						<table  style="border:0px;width:100%" cellpadding="0" cellspacing="0">
				<tr>
					<td vertical-align="top" style="vertical-align:top;">
					<table  style="width:100%;border-right:1px solid #000; page-break:avoid" class="border-btn" cellspacing="0">
                <tbody>
			    <tr class="">
			    <th class="main-heading-11 br1px br_but colourfreq" style="width:20%">History</th>
				<th class="main-heading-11 br1px br_but" style="width:24%">ADA Code</th>
				<th class="main-heading-11 br1px br_but" style="width:28%">Tooth No.</th>
				<th class="main-heading-11 br1px br_but" style="width:28%">DOS</th>
				</tr>
				<xsl:for-each select="hdto1/hisall1">
				<tr class="">
				<td class="br1px"><span class="sub-heading1"></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyCode"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyTooth"/></span></td>
				<td class="br1px"><span class="sub-heading1"><xsl:value-of select="historyDos"/></span></td>
				</tr>
				</xsl:for-each>
             </tbody>
            </table></td>
					
			<td vertical-align="top" style="vertical-align:top;">
			<table style="width:100%;border-right:1px solid #000;  margin-left:-1px;"  class="border-btn" cellspacing="0" >
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
				<table style="width:100%;border-right:1px solid #000; margin-left:-1px;" class="border-btn last-r-border" cellspacing="0">
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
			
			<br/>
			<table cellpadding="0" cellspacing="0" style="width:100%; clear:both;">
                <tbody>
				<tr>
                        <td colspan="2" class="main-heading-11">
                            Comments:
							<div  style="border: 1px solid">
							<span class="sub-heading" >
							
							<xsl:value-of select="comments"/>&#160;
							
							
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