import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimRcmDataModel } from '../../models/claim-rcm-data-model';
import {ClaimRulesPullDataModel} from '../../models/claim-rules-pull-data-model';

import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BillingClaimsComponent implements OnInit {



  claimRcm: ClaimRcmDataModel;
  claimARulesPullDataModel:ClaimRulesPullDataModel={};

  claimUUid:string="";
  ruleData:any=[];
  count:any={'pass':0,'fail':0,'alert':0};
  mtype:number=1;
  ivfData:any=[];

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants,
      private route: ActivatedRoute) {
   this.claimRcm ={claimId:""};

   }

  
  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      console.log(params.get('uuid'))
      this.claimUUid=params.get('uuid') || '';
       this.fetchClaimsByUuid(this.claimUUid);
      });
      this.getSubmissionData();
   
  }

  fetchClaimsByUuid(uuid:string){

    let ths=this;
    ths.appService.fetchBillingClaimsByUuid(uuid,(res:any)=>{
      if (res.status=== 200){
       ths.claimRcm= res.data;
      
      }
     
    });
  }

  getIVFData(){

    let ths=this;
   
    // ths.appService.fetchivfDataForClaim(ths.claimUUid,(res:any)=>{
    //   if (res.status=== 200){
      
    //   }
     
    // });
    ths.ivfData = 'dsf'
  }

  getRulesData(){
    
    let ths=this;
    ths.claimARulesPullDataModel.claimId=ths.claimRcm.claimId.split("_")[0];
    ths.claimARulesPullDataModel.patientId=ths.claimRcm.patientId;
    ths.claimARulesPullDataModel.officeId=ths.claimRcm.officeUuid;

    //For testing :
    ths.claimARulesPullDataModel.officeId= "f015515d-7df2-11e8-8432-8c16451459cd";
    ths.claimARulesPullDataModel.claimId="11878";
    ths.claimARulesPullDataModel.patientId="7431";
    this.ruleData=
    [
      {
          "message": "Patient is <b style=\"color:red\" class=\"error-message-api\">NOT</b> eligible due to Policy being not effective on 2019-12-19. The policy is effective from 2020-01-01.",
          "surface": null,
          "ruleName": "Eligibility of the patient",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "1",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Fee Schedule in Insurance Verification Form - UniCare 100_(01/21) - does NOT match with Fee Schedule in Eaglesoft - BCBS - DN0103_(05/22). <b style=\"color:red\" class=\"error-message-api\">Change the Fee Schedule in Eaglesoft and regenerate the Claim</b>.",
          "surface": null,
          "ruleName": "Coverage Book and Fee Schedule Names",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "4",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Employer Name in ES - Horizon Dental Option does <b style=\"color:red\" class=\"error-message-api\">NOT</b> match Employer Name in IV - Self.  Correct the Employer name.",
          "surface": null,
          "ruleName": " Percent Coverage Match",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "6",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Insurance Name in ES - Horizon Blue Cross Blue Shiled of NJ does <b style=\"color:red\" class=\"error-message-api\">NOT</b> match Insurance Name in IV - Horizon BCBS of NJ.  Correct the Insurance Name.",
          "surface": null,
          "ruleName": " Percent Coverage Match",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "6",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Downgrading",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "19",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "D0150 was done on tooth no. NA ( ) on 2021-08-31,2020-06-29,2019-11-20,2021-01-26 and does <b style=\"color:red\" class=\"error-message-api\">NOT</b> meet allowed frequency of 1( 1x36Mo). <b style=\"color:red\" class=\"error-message-api\">Recalculate the Insurance & Patient Portion.</b>",
          "surface": null,
          "ruleName": "Frequency Limitations",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "21",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "D0210 was done on tooth no. NA  on 2019-11-20 and does <b style=\"color:red\" class=\"error-message-api\">NOT</b> meet allowed frequency of 1(1x36Mo_1D). <b style=\"color:red\" class=\"error-message-api\">Recalculate the Insurance & Patient Portion.</b>",
          "surface": null,
          "ruleName": "Frequency Limitations",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "21",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Exam Frequency Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "57",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Age Limit",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "8",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Missing Tooth Clause",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "18",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Bundling - Fillings",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "17",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Bundling - X-Rays",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "16",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "SRP Quads Per Day",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "15",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Sealants",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "14",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Filling Codes & Tooth #",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "9",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Pre-Auth / Attachments / Narratives",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "10",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Waiting Period",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "11",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Build-Ups & Crown Same Day",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "13",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "CRA Not Required.",
          "surface": null,
          "ruleName": "CRA",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "22",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Insurance Type is PPO  <b style=\"color:green\" class=\"success-message-api\"> Pass </b>.",
          "surface": null,
          "ruleName": "Xray Bundling",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "23",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Plan Type not Medicaid -  <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Filling Bundling",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "24",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "DQ Fillings",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "53",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Plan Type not Medicaid -  <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Crown Bundling with Fillings",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "25",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Filling/Sealant not found with Crown - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Filling and Sealant Not Covered",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "26",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Sealant not found with Filling - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Sealant Not Covered",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "27",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Procedure not found with Extraction - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Restoration Not Covered",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "28",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Only one Exam Code found (D0150) - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Exams Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "29",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "No Only one Cleaning Code found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Cleaning Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "30",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Perio Maintainance Clause",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "31",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "None of D4341 and D4342 found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "SRP Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "32",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "None of Filling and Endo found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Root Canal Clause",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "33",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "None of D2950 and D2954 found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "D2954 Clause",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "34",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "None of Bone Graft/Alveoplasty code found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Bone Graft Rule",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "35",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "D5130 and D5140 not found - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Immediate Denture",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "36",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Plan type is PPO - <b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Medicaid Provider Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "38",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Prophy code not present. <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Age Limitation Prophylaxis",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "39",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Space Maintainer codes not Present - <b style=\"color:green\" class=\"success-message-api\">Pass</b>.",
          "surface": null,
          "ruleName": "Space Maintainer-Billateral",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "40",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Duplicate Treatment Codes (#[NA]),(#[NA, NA]) Present. <b style=\"color:red\" class=\"error-message-api\">Fail</b>",
          "surface": null,
          "ruleName": "Duplicate TP Codes",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "42",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "In Claim Signed Consent Requirements has <b style=\"color:red\" class=\"error-message-api\">NOT</b> been taken/Present.",
          "surface": null,
          "ruleName": "Signed Consent Requirements",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "44",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Pre-Authorization",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "46",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Provider Change",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "47",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Exam limitation for CHIP",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "48",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Sealant limitation in CHIP",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "49",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Major Service Form Requirements",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "50",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "D0210 has already been performed on Date 2019-11-20 hence cannot take D0210, Please <b style=\"color:red\" class=\"error-message-api\">Remove</b> it from the Treatment Plan. Plan and bill PA and Bitewings with checking frequency.",
          "surface": null,
          "ruleName": "FMX/Pano Rule",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "54",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Perio Depth Checker",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "55",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:#074200\" class=\"success-other-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Exception Rule",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "56",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Exam Frequency Limitation (D0145)",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "58",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "BWX Age Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "59",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Exams Age Limitation",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "60",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Humana Exception",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "61",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "FCL Dental Exception",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "62",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Service not Covered (Chip)",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "63",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "IntraOral Periapical",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "64",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Nitrous Oxide",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "65",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "COB Primary",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "66",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Bridge Clause",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "75",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Policy Holder name :Duncan Conner - <b style=\"color:green\" class=\"success-message-api\">Pass</b> ",
          "surface": null,
          "ruleName": "Policy Holder Match",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "83",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "Provider in IV - Geetika Rastogi  does <b style=\"color:red\" class=\"error-message-api\">NOT</b> match Provider in Eaglesoft - Le. <b style=\"color:red\" class=\"error-message-api\">Change the Provider in Eaglesoft and regenerate the Claim.</b>",
          "surface": null,
          "ruleName": "Provider Name",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "1",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "84",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Perio Maintenance with Prophy and Fluoride",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "85",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      },
      {
          "message": "<b style=\"color:green\" class=\"success-message-api\">Pass</b>",
          "surface": null,
          "ruleName": "Oral hygiene with Prophy and Fluoride",
          "ivDate": "11/15/2021",
          "tooth": null,
          "mtype": "2",
          "officeName": "Beaumont",
          "patientId": "7431",
          "claimId": "11878",
          "insuranceType": "Primary",
          "codes": null,
          "ruleId": "86",
          "ivId": "196041",
          "dos": "11/20/2019",
          "patientName": "Duncan Conner"
      }
  ]

    this.count.pass=this.count.fail=this.count.alert=0;
    this.ruleData.forEach((e:any) => {
      if(e.mtype==1){
        this.count.fail = this.count.fail + 1;
      }
      else if(e.mtype==2){
        this.count.pass= this.count.pass +1;
      }else if(e.mtype==3){
        this.count.alert=this.count.alert+1;
      }
    });

    // ths.appService.getClaimRuleData( ths.claimARulesPullDataModel,(res:any)=>{
    //   if (res.status=== 200){
       
    //     } else{
         
    //     }
          
     
    // });
  }

  switchType(type:any){
      if(type=='pass'){
         this.mtype=2;
      }else if(type=='fail'){
        this.mtype=1;
     }else if(type==='alert'){
        this.mtype=3
     }
  }

  getSubmissionData(){
    //   this.appService.fetchSubmissionData(this.claimUUid,(res:any)=>{
    //     console.log(res)
    //   })   

  }

  showHide(index:any){
    let el:any = document.querySelectorAll(".bold-b-text");
    for(let i =0 ; i<el.length ; i++){
        if(i == index){
            el[i].classList.toggle("collapsed");
            el[i].nextElementSibling.classList.toggle("show");
        }
    }
  }
}
