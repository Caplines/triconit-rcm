<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"  version="1.0"  />
	<xsl:template match="/caplineIVFFormDto">
<html>

<head>
                <style>
				
				table {vertical-align:text-top;}
				.grid-container {
  display: grid;
  grid-template-columns: auto auto auto;
  background-color: #bbb;
 width:100%; 
 box-sizing:border-box;
}
.grid-item {
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 10px;
  
  text-align: center;
  width:32%;
  float:left;
  box-sizing:border-box;
}

.sub-heading {font-size:14px; color:#888888}
#page_1{font-size:14px; color:#333333}
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
                            Office Name: <span class="sub-heading"><xsl:value-of select="basicInfo1"/></span>
                        </td>
                        <td>
                            Patient Name: <span class="sub-heading"><xsl:value-of select="basicInfo2"/></span>
                        </td>
                        <td>
                           Insurance Name: 	<span class="sub-heading"><xsl:value-of select="basicInfo3"/></span>
                        </td>
                        <td>
                         Tax ID: 	<span class="sub-heading"><xsl:value-of select="basicInfo4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                           Policy Holder: <span class="sub-heading"><xsl:value-of select="basicInfo5"/></span>
                            
                        </td>
                        <td>
                            Patient DOB:  <span class="sub-heading"><xsl:value-of select="basicInfo6"/></span>
                         </td>
                        <td>
                         Insurance Contact: <span class="sub-heading"><xsl:value-of select="basicInfo7"/></span>
                         </td>
                        <td>
                           CSR Name: <span class="sub-heading"><xsl:value-of select="basicInfo8"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Policy Holder DOB: <span class="sub-heading"><xsl:value-of select="basicInfo9"/></span>
                           
                        </td>
                        <td>
                           Employer Name: <span class="sub-heading"><xsl:value-of select="basicInfo10"/></span>
                              
                        </td>
                        <td>
                            Continued/Recall/NP: <span class="sub-heading"><xsl:value-of select="basicInfo11"/></span>
                         </td>
                        <td>
                            REF #: <span class="sub-heading"><xsl:value-of select="basicInfo12"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Member SSN: <span class="sub-heading"><xsl:value-of select="basicInfo13"/></span>
                          
                        </td>
                        <td>
                           Group #: <span class="sub-heading"><xsl:value-of select="basicInfo14"/></span>
                            
                        </td>
                        <td>
                           COB Status: <span class="sub-heading"><xsl:value-of select="basicInfo15"/></span>
                         </td>
                        <td>
                           Patient ID: <span class="sub-heading"><xsl:value-of select="basicInfo21" /></span>
                               
                        </td>
                    </tr>
                    <tr>
                        <td>
                           Member ID: <span class="sub-heading"><xsl:value-of select="basicInfo16"/></span>
                        </td>
                        <td>
                           Appointment: <span class="sub-heading"><xsl:value-of select="basicInfo17"/></span>
                        </td>
                        <td>
                           Payer ID: <span class="sub-heading"><xsl:value-of select="basicInfo18"/></span>
                        </td>
                        <td>
                           Provider Last Name: <span class="sub-heading"><xsl:value-of select="basicInfo19"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                           Insurance Address: <span class="sub-heading"><xsl:value-of select="basicInfo20"/></span>
                            
                        </td>
                    </tr>
                    
                </tbody>
            </table>
            <p id="errors" style="color:red;font-weight:200;"></p>
            <p class="p3 ft1">Policy/Plan Information</p>
            <table cellpadding="5" cellspacing="2" class="t1" id="policy">
                <tbody>
				   <!--
                    <tr>
                        <td colspan="2">
                            <p class="p2 ft4"><b>Plan Type :</b>
                                <span class="sub-heading"><xsl:value-of select="policy1"/>
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
                        <td>
                            <p class="p2 ft4"><b>CRA Req.:</b>
                            <span class="sub-heading"><xsl:value-of select="policy17"/></span>
                            
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft4">
                                Termed Date:
                                <span class="sub-heading"><xsl:value-of select="policy2"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft4">
                                Network: <span class="sub-heading"><xsl:value-of select="policy3"/></span>
								
                               
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">Fee Schedule:
                                <span class="sub-heading"><xsl:value-of select="policy4"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p4 ft5">Effective Date:
                                <span class="sub-heading"><xsl:value-of select="policy5" /></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">Cal.Yr/Fiscal Yr/Plan Yr. :
                                <span class="sub-heading"><xsl:value-of select="policy6"/></span>
                                <!--______-->
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="p0 ft4">Annual Max:
                                <span class="sub-heading"><xsl:value-of select="policy7"/></span>
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft4">Ann. Max Rem:
                                <span class="sub-heading"><xsl:value-of select="policy8"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">Ind. Ded:
                                <span class="sub-heading"><xsl:value-of select="policy9"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">Ind. Ded Rem:
                                <span class="sub-heading"><xsl:value-of select="policy10"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p2 ft4">Dependents Covered to age:
                                <span class="sub-heading"><xsl:value-of select="policy11"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft4">Pre-D Mandatory:
                                <br/>
                                <span class="sub-heading"><xsl:value-of select="policy12"/></span>
                                <!--______-->
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="p2 ft4">Non-Duplicate clause?
                                <span class="sub-heading"><xsl:value-of select="policy13"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td colspan="2">
                            <p class="p0 ft4">Full Time Student Status Required?
                                <span class="sub-heading"><xsl:value-of select="policy14"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td colspan="2">
                            <p class="p0 ft5">Assignment of Benefits Accepted?
                                <span class="sub-heading"><xsl:value-of select="policy15"/></span>
                                <!--______-->
                            </p>
                        </td>
                        <td>
                            <p class="p0 ft5">
                                Coverage Book:
                                <span class="sub-heading"><xsl:value-of select="policy16"/></span>
                                <!--______-->
                            </p>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            <br/>

            <table style="width: 50%" id="rules">
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
                        <td>
                            <p class="ft17">Percentages Covered</p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft38">Basic(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages1"/></span>
                        </td>
                        <td>
                            <p class="ft38">Subject to ded</p>
                        </td>
                        <td>
                            <p class="ft38">
                                <span class="sub-heading"><xsl:value-of select="percentages2"/></span>
                            </p>
                        </td>
                        <td>
                            <p class="ft13">Major(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages3"/></span>
                        </td>
                        <td>
                            <p class="ft13">Subject to ded</p>
                        </td>
                        <td>
                            <p class="ft13">
                                <span class="sub-heading"><xsl:value-of select="percentages4"/></span>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft9">Endo(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages5"/></span>
                        </td>
                        <td>
                            <p class="ft9">Suject to ded</p>
                        </td>
                        <td>
                            <p class="ft9">
                                <span class="sub-heading"><xsl:value-of select="percentages6"/></span>
                            </p>
                        </td>
                        <td>
                            <p class="ft38">Perio Surgery(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages7"/></span>
                        </td>
                        <td>
                            <p class="ft38">Subject to ded</p>
                        </td>
                        <td>
                            <p class="ft38">
                                <span class="sub-heading"><xsl:value-of select="percentages8"/></span>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft18">Preventative(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages9"/></span>
                        </td>
                        <td>
                            <p class="ft9">Diagnostics(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages10"/></span>
                        </td>
                        <td>
                            <p class="ft31">PA X-rays(%)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages11"/></span>
                        </td>
                        <td>
                            <p class="ft18">Claims Timely Fillings</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="percentages12"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft27">Prosthetics</p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft22">Missing tooth clause</p>
                        </td>
                        <td>
                            <p class="ft22">
                                <span class="sub-heading"><xsl:value-of select="prosthetics1"/></span>
                            </p>
                        </td>
                        <td>
                            <p class="ft22">Replacement Clause</p>
                        </td>
                        <td>
                            <p class="ft22">
                                <span class="sub-heading"><xsl:value-of select="prosthetics2"/></span>
                            </p>
                        </td>
                        <td>
                            <p class="ft22">Paid Prep/Seat Date</p>
                        </td>
                        <td>
                            <p class="ft22">
                                <span class="sub-heading"><xsl:value-of select="prosthetics3"/></span>
                                
                            </p>
                        </td>
                        <td>
                            <p class="ft22">Night Gaurds (D9940) Frequency</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="prosthetics4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft30">Waiting Periods (In Months)</p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>
                            <p class="ft6">SSC</p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft31">Basic</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="waitingPeriod1"/></span>
                            
                        </td>
                        <td>
                            <p class="ft31">Major</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="waitingPeriod2"/></span>
                            
                        </td>
                        <td>
                            <p class="ft7">D2930:</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="ssc1"/></span>
                        </td>
                        <td>
                            <p class="ft7">D2931:</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="ssc2"/></span>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <p class="ft30">
                                Exams(Frequency):
                            </p>
                        </td>
                        <td>
                            
                        </td>
                        <td>
                           
                        </td>
                        <td>
                         
                        </td>
                        <td>
                           
                        </td>
                        <td>
                            
                        </td>
                        <td>
                           
                        </td>
                        <td>
                          
                        </td>
                    </tr>
                    <tr>
                        <!--Exams-->
                        <td>
                            <p class="ft31">D0120</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="exams1"/></span>
                        </td>
                        <td>
                            <p class="ft31">D0140</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="exams2"/></span>
                        </td>
                        <td>
                            <p class="ft31">D0145</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="exams3"/></span>
                        </td>
                        <td>
                            <p class="ft31">D0150</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="exams4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="ft6">
                                X-Rays(Frequency)
                            </p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <!--X-Rays-->
                        <td>
                            <p class="ft7">BWX(0274)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="xrays1"/></span>
                        </td>
                        <td>
                            <p class="ft7">PA(0220)</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="xrays2"/></span>
                        </td>
                        <td>
                            <p class="ft7">D0230</p>
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="xrays3"/></span>
                        </td>
                        <td class="ft7">FMX(0210)</td>
                        <td class="ft7">
                            <span class="sub-heading"><xsl:value-of select="xrays4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft7">Bundling</td>
                        <td>
                            <p class="ft7">
                                <span class="sub-heading"><xsl:value-of select="xrays5"/></span>
                            </p>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft17">
                            Fluroide(D1208)
                        </td>
                        <td>
                           
                        </td>
                        <td>
                           
                        </td>
                        <td>
                          
                        </td>
                        <td class="ft17">
                            Varnish(D1206)
                        </td>
                        <td>
                           
                        </td>
                        <td>
                            
                        </td>
                        <td>
                           
                        </td>
                    </tr>
                    <tr>
                        <!--Fluroide(D1208)-->
                        <td class="ft18">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="fluroide1"/></span>
                        </td>
                        <td class="ft18">
                            Age Limit
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="fluroide2"/></span>
                        </td>
                        <!--Varnish(D1206)-->
                        <td class="ft18">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="fluroide3"/></span>
                        </td>
                        <td class="ft18">
                            Age Limit
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="fluroide4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft8">
                            Sealants(D1351)
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealantsD"/></span>
                        </td>
                        <td>
                            
                        </td>
                        <td>
                           
                        </td>
                        <td class="ft8">
                            Coverage:
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <!--Sealants(D1351)-->
                        <td class="ft15">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealants1"/></span>
                        </td>
                        <td class="ft15">
                            Age Limit
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealants2"/></span>
                        </td>
                        <!--Coverage-->
                        <td class="ft15">
                            Primary Molars
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealants3"/></span>
                        </td>
                        <td class="ft15">
                            Pre-Molars
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealants4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft15">
                            Perm-Molars
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sealants5"/></span>
                        </td>
                        <td class="ft22">
                            Prophy (Frequency):
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="prophy1"/></span>
                        </td>
                        <td class="ft22">
                            D1120:
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="prophy2"/></span>
                        </td>
                        <td class="ft22">
                            1120/1110 Roll age
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="rollage"/></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="ft11">Perio</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <!--Perio-->
                        <td class="ft13">
                            SRP(D4341)%
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perio1"/></span>
                        </td>
                        <td class="ft13">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perio2"/></span>
                        </td>
                        <td class="ft13">
                            Quads Per Day
                        </td>
                        <td>
                            <p class="ft13">
                                <span class="sub-heading"><xsl:value-of select="perio3"/></span>
                            </p>
                        </td>
                        <td class="ft13">
                            Days b/w Quad
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perio4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="p8 ft11">Perio Mnt.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <!--PerioMNT-->
                        <td class="ft13">
                            (D4910) %:
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt1"/></span>
                        </td>
                        <td class="ft13">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt2"/></span>
                        </td>
                        <td class="ft13">
                            Alt. with Prophy(D1110)
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt3"/></span>
                        </td>
                        <td class="ft23">
                            Fillings(Bundle)
                        </td>
                        <td>
                            <p class="ft23">
                                <span class="sub-heading"><xsl:value-of select="fillings"/></span>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft13">
                            FMD(D4355)%
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt4"/></span>
                        </td>
                        <td class="ft13">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt5"/></span>
                        </td>
                        <td class="ft13">
                            Gingivitis(D4346)%
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt6"/></span>
                        </td>
                        <td class="ft13">
                            Frequency
                        </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="perioMnt7"/></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="ft39">Sedations</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft40">Nitrous (D9230)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sedations1"/></span>
                        </td>
                        <td class="ft40">IV Sedation (D9243)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sedations2"/></span>
                        </td>
                        <td class="ft40">IV Sedation (D9248)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="sedations3"/></span>
                        </td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft35">Extractions(%)</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft37">Minor(D7111,D7140)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="extractions1"/></span>
                        </td>
                        <td class="ft37">Major (D7210,D7220, D7230,D7240)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="extractions2"/></span>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft26">Oral Surgery</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft25">Crown Lengthening(D4249)%</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="oral1"/></span>
                        </td>
                        <td class="ft25" style="text-align: center">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="oral2"/></span>
                        </td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft26">Alveolplasty</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft25">1-3 Teeth/Qd(D7311): Covered w. Ext</td>
                        <td>
                            <p class="ft25">
                                <span class="sub-heading"><xsl:value-of select="oral3"/></span>
                            </p>
                        </td>
                        <td class="ft25" style="text-align: center">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="oral4"/></span>
                        </td>
                        <td class="ft25">4 Teeth/Qd (D7310): Covered w. Ext</td>
                        <td>
                            <p class="ft25">
                                <span class="sub-heading"><xsl:value-of select="oral5"/></span>
                            </p>
                        </td>
                        <td class="ft25" style="text-align: center">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="oral6"/></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="ft6">Dentures (Frequency)</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft7">Complete (D5110/D5120)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="dentures1"/></span>
                        </td>
                        <td class="ft7">Immediate (D5130/D5140</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="dentures2"/></span>
                        </td>
                        <td class="ft7" >Partial
  						 (D5211/D5212/<br/>D5213/D5214/<br/>D5225/D5226)
						 </td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="dentures3"/></span>
                        </td>
                        <td class="ft7">Interim Partial (D5820)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="dentures4"/></span>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft7">Bone Graft (D7953) Covered w. Ext</td>
                        <td>
                            <p class="ft7">
                                <span class="sub-heading"><xsl:value-of select="dentures5"/></span>
                            </p>
                        </td>
                        <td class="ft7" style="text-align: center">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="dentures6"/></span>
                        </td>
                        <td class="ft7" style="text-align: center"></td>
                        <td>
                            
                        </td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft42">Implants Coverage (%)</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft42">Implants D6010</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="implants1"/></span>
                        </td>
                        <td class="ft13">Implants D6057</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="implants2"/></span>
                        </td>
                        <td class="ft13">Implants D6190</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="implants3"/></span>
                        </td>
                        <td class="ft13">Implants Supported Porc./Ceramic (D6065)</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="implants4"/></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="ft43" style="font-size: 10pt;font-weight: bold;text-decoration: underline">Posterior</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft43">Composites (D2391) %</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior1"/></span>
                        </td>
                        <td class="ft43">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior2"/></span>
                        </td>
                        <td class="ft43" colspan="2">Downgraded to Amalgam (D2140)
                        </td>
                        <td colspan="2">
                            <p class="f43" style="font-size: 12px">
                                <span class="sub-heading"><xsl:value-of select="posterior3"/></span>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft43">Crowns (D2740 &amp; D2750)%</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior4"/></span>
                        </td>
                        <td class="ft43">Crowns(D2740 &amp; D2750) Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior5"/></span>
                        </td>
                        <td class="ft43" colspan="2">Are Crowns Downgraded (D2791)
                        </td>
                        <td colspan="2">
                            <p class="f43" style="font-size: 12px">
                                <span class="sub-heading"><xsl:value-of select="posterior6"/></span>
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td class="ft43">Night Gaurds (D9940) %</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior7"/></span>
                        </td>
                        <td class="ft31">Consult (D9310)%</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior8"/></span>
                        </td>
                        <td class="ft31" style="text-align: center">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior9"/></span>
                        </td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="ft22">Build-up (D2950) %</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior10"/></span>
                        </td>
                        <td class="ft22">Frequency</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="posterior11"/></span>
                        </td>
                        <td class="ft22">Same day as Crown</td>
                        <td>
                            <p class="ft22">
                                <span class="sub-heading"><xsl:value-of select="posterior12"/></span>
                            </p>
                        </td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft14">Ortho(%)</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>

                    <tr>
                        <td class="ft7">D8080, D8090, D8070</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="ortho1"/></span>
                        </td>
                        <td class="ft7">Ortho Max</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="ortho2"/></span>
                        </td>
                        <td class="ft7">Age Limit</td>
                        <td>
                            <span class="sub-heading"><xsl:value-of select="ortho3"/></span>
                        </td>
                        <td class="ft7">Subject to Deductible</td>
                        <td>
                            <p class="ft7">
                                <span class="sub-heading"><xsl:value-of select="ortho4"/></span>
                            </p>
                        </td>
                    </tr>
                </tbody>
            </table>
            
            <br/>

            <p class="p3 ft34">Complete 5 Year History</p>
			
			 <xsl:variable name="hh" select="count(history/his)"/>
             <xsl:variable name="hhi" select="num[1]"/>			 
			 <xsl:variable name="tra2" select="history/his"/> 
			 
			<div class="classname_main">  
			
			<div class='grid-container'>
				<div class="grid-item">ADA Code</div>
				<div class="grid-item">Tooth No.</div>
				<div class="grid-item">DOS</div>

				</div>
			<xsl:for-each select="hdto/hisall">
                 <xsl:variable name="cn" select="className"/> 
                <div class='grid-container {$cn}'>
				<div class="grid-item"><span class="sub-heading"><xsl:value-of select="historyCode"/></span>&#160;</div>
				<div class="grid-item"><span class="sub-heading"><xsl:value-of select="historyTooth"/></span>&#160;</div>
				<div class="grid-item"><span class="sub-heading"><xsl:value-of select="historyDos"/></span>&#160;</div>

				</div>
				
				
               </xsl:for-each>
	         </div>

           
            <table cellpadding="0" cellspacing="0" style="width:100%; clear:both;">
                <tbody>
				<tr>
                        <td colspan="2">
                            Comments:
							<span class="sub-heading"><xsl:value-of select="comments"/></span>
                             
                        </td>
                        
                    </tr>
                    <tr>
                        <td>
                            Benefits Verified by:
							<span class="sub-heading"><xsl:value-of select="benefits"/></span>
                             
                        </td>
                        <td style="text-align:right">
                          Date:
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