import { Component, ViewChild, ViewEncapsulation } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

import {
  ClaimRcmDataModel, ClaimEditModel, ServiceLevelCodeModel, SubmissionDetailModel,
  ClaimRuleModel, ClaimRuleRemarkModel, RuleEngineValModel, ServiceLevelCodeDataModel,
  ClaimRuleRemarkModelS, TLUser, TeamsM, OtherTeamRem, ClaimDetailModel, ClaimSettingDataModel, ClaimStep
} from '../models/claim-rcm-data-model';

import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { ApplicationServiceService } from '../service/application-service.service';
import { AppConstants } from '../constants/app.constants';
import { ClaimService } from '../service/claim.service';
import { ClaimAssignToTeamModel } from '../models/claim_assign_to_team';
import { ClaimUpdateStatusModel } from '../models/claim.status.model';
import { ClaimRulesPullDataModel } from '../models/claim-rules-pull-data-model';
import Utils from '../util/utils';
import { DownLoadService } from '../service/download.service';
import { DatePipe } from '@angular/common';
import { PdfViewerComponent } from 'ng2-pdf-viewer';
// import * as pdfjsLib from 'pdfjs-dist/build/pdf';

@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss'],
  encapsulation: ViewEncapsulation.None,
})



export class BillingClaimsComponent {

  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  alertAssign: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  alertArch: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  alertPreferrsub: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  finalerror: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  claimRcm: ClaimRcmDataModel;
  claimStatus: ClaimUpdateStatusModel;
  claimARulesPullDataModel: ClaimRulesPullDataModel = {};
  claimEditModel: ClaimEditModel;
  claimServiceLevelModel: ServiceLevelCodeModel;
  submissionDto: SubmissionDetailModel = {};
  claimRules: Array<ClaimRuleModel> = [];
  claimRuleRemarks: Array<ClaimRuleRemarkModel> = [];
  claimSteps: Array<ClaimStep> = [];
  reheading: string;
  inSave: boolean = false;
  ruleEngineReport: Array<RuleEngineValModel> = [];
  tlUsers: Array<TLUser>;
  teamsMs: Array<TeamsM>;
  assignModel: any = { "toLead": false, "toOtherTeam": false };
  otherTeamRemarks: Array<OtherTeamRem> = [];
  smilePoint: boolean = false;
  selectedTeam: number;
  claimAssignToTeamModel: ClaimAssignToTeamModel = {};
  claimUUid: string = "";
  infoMessage: string = "";
  ruleData: any = [];
  count: any = { 'pass': 0, 'fail': 0, 'alert': 0 };
  countA: any = { 'pass': 0, 'fail': 0, 'alert': 0 };
  countAS: any = { 'pass': 0, 'fail': 0, 'alert': 0 };
  mtype: string = '1';//Fail By Default.
  tlErrormsg = "";
  otherErrormsg = "";
  loader: any = { claimDetail: false, linkToRelatedDoc: false, remarksByOther: false, rebilledClaims: false, automatedValidation: false, manualValidation: false, ruleEngValid: false, serviceCode: false, claimSubmission: false }
  //ivfData:any=[];
  updatedIvfId: any;
  updatedTpId: any;
  reason: string = "";
  countM300: number = 1;
  relatedTo_300 = true;
  actionButtons = false;
  clientName: string = '';
  assignType: string = '';
  modelElement: any = { 'modal': '', 'span': '' }
  attachmentConfig: any = { 'showAttachment': false, 'attachmentCount': 0 };
  overidePreferredSubmission: boolean = false;
  selectedFilesMap: any = new Map();
  removedFilesMap: any = new Map();
  serialNoArray = new Map<string, number>();
  deleteType = "";
  deleteLoaderIVTP = false;
  noProviderNoteCodes: Array<string> = [];
  invalidClaim = "";
  claimSettingDataModel: ClaimSettingDataModel = null;
  displayPhaseSection: boolean = true;
  /*readonly noProviderNoteCodes: Array<string> = ["D0120", "D0145", "D0150", "D0140", "D0160", "D0170", "D0220", "D0230",
    "D0272", "D0274", "D0210", "D0350", "D1110", "D1120", "D1206", "D1208",
    "D0330", "D0601", "D0602", "D0603", "D1330", "D1351", "D1352", "D2330",
    "D2331", "D2332", "D2335", "D2391", "D2392", "D2393", "D2394", "D0431",
    "D2140", "D2150", "D2160", "D2161"];*/
  sectionIds: any = {
    'SECTION_CLAIM_DETAIL': {
      sectionId: 1,
      isNewSection: false
    },
    'LINKS_RELATED_DOCUMENTS': {
      sectionId: 2,
      isNewSection: false
    },
    'REMARKS_BY_OTHER': {
      sectionId: 3,
      isNewSection: false
    },
    'REBILLED_CLAIM': {
      sectionId: 4,
      isNewSection: false
    },
    'CLAIM_LEVEL_VALIDATION_AUTO': {
      sectionId: 5,
      isNewSection: false
    },
    'CLAIM_LEVEL_VALIDATION_MANUAL': {
      sectionId: 6,
      isNewSection: false
    },
    'SERVICE_LEVEL_VALIDATION_AUTO': {
      sectionId: 7,
      isNewSection: false
    },
    'SERVICE_LEVEL_VALIDATION_MANUAL': {
      sectionId: 8,
      isNewSection: false
    },
    'RULE_ENGINE_VALIDATION': {
      sectionId: 9,
      isNewSection: false
    }, 'CLAIM_SUBMISSION': {
      sectionId: 10,
      isNewSection: false
    },
    'SERVICE_LEVEL_INFORMATION': {
      sectionId: 11,
      isNewSection: true
    },
    'EOB': {
      sectionId: 12,
      isNewSection: true
    },
    'CLAIM_LEVEL_INFORMATION': {
      sectionId: 13,
      isNewSection: true
    },
    'INSURANCE_PAYMENT_INFORMATION': {
      sectionId: 14,
      isNewSection: true
    },
    'PATIENT_STATEMENT': {
      sectionId: 15,
      isNewSection: true
    },
    'ASSIGN_TO_OTHER': {
      sectionId: 16,
      isNewSection: false
    },
    'INSURANCE_FOLLOW_UP': {
      sectionId: 17,
      isNewSection: true
    },
    'RECREATE_CLAIM': {
      sectionId: 18,
      isNewSection: true
    },
    'APPEAL': {
      sectionId: 19,
      isNewSection: true
    },
    'PATIENT_PAYMENT': {
      sectionId: 20,
      isNewSection: true
    },
    'PATIENT_COMMUNICATION': {
      sectionId: 21,
      isNewSection: true
    },
    'COLLECTION_AGENCY': {
      sectionId: 22,
      isNewSection: true
    },
    'REQUEST_REBILLING': {
      sectionId: 23,
      isNewSection: true
    },
    'REBILLING': {
      sectionId: 24,
      isNewSection: true
    },
    'NEED_TO_CALL_INSURANCE': {
      sectionId: 25,
      isNewSection: true
    },
    'CURRENT_STATUS_AND_NEXT_ACTION': {
      sectionId: 26,
      isNewSection: true
    },
    'ATTACHMENT': {
      sectionId: 27,
      isNewSection: false
    }
  };

  toggleTab: any = {};
  toggleSideBar: boolean = false;

  claimSectionModal: any = {
    
 CLAIM_LEVEL_INFORMATION: {
  noOfEstPayment:'',
  paymentFrequency:'',
  noOfPaymentReceived:'',
},
    APPEAL: {
      modeOfAppeal: '',
      aiToolUsed: '',
      remarks: '',
      appealDocument: ''
    },
    INSURANCE_PAYMENT_INFORMATION: {
      "paymentIssueTo": "",
      "amountReceivedInBank": "",
      "amountPostedInEs": "",
      "checkDeliverTo": "",
      "checkNumber": "",
      "amountDateReceivedInBank": "",
      "checkCashDate": "",
      "paidAmount": "",
      "paymentMode": "",
    },
    EOB: {
      data: []
    },
    SERVICE_LEVEL_INFORMATION: {
      data: [],
      serviceLevelTotalAmount: {}
    },
    INSURANCE_FOLLOW_UP: {
      data: [],
      modal: {}
    },
    PATIENT_STATEMENT: {
      "modeOfStatement": "",
      "reason": "",
      "statementType": "",
      "status": "",
      "amountStatement": 0,
      "nextReviewDate": "",
      "nextStatementDate": "",
      "statementSendingDate": "",
      "remarks": "",
      "statementNotes": 0,
      "balanceSheetLink": "",
      "buttonType": 1
    },
    PATIENT_PAYMENT: {},
    PATIENT_COMMUNICATION: {
      data: [],
      modal: {}
    },
    CURRENT_STATUS_AND_NEXT_ACTION: {},

  };
  finalSaveClaimDataModel: any = {
    claimUuid: '',
    finalSubmit: true,
    moveToNextTeam: true
  };
  sectionLevelData: any = [];
  emptyFields: any = {};
  isSectionValidated: boolean = true;
  stringToSearch: any = '';
  zoom_to: any = 1.0;
  pdfUrlSrc: any = "";
  sectionLevelInfoTotalConfig: any = { allowedAmount: 0, paidAmount: 0, adjustmentAmount: 0, billToPatientAmount: 0, estPrimary: 0, fee: 0 };
  viewNotesConfig: any = { showNotes: false, viewNotes: [] };
  @ViewChild(PdfViewerComponent, { static: false }) private pdfViewer!: PdfViewerComponent;


  constructor(public appService: ApplicationServiceService, public appConstants: AppConstants,
    private claimService: ClaimService,
    private route: ActivatedRoute, private title: Title, private location: Location, private router: Router, private downloadService: DownLoadService,
    public datepipe: DatePipe) {
    this.claimRcm = { claimId: "" };
    title.setTitle(Utils.defaultTitle + "Claim Detail");
  }

  ngOnInit(): void {
    this.smilePoint = Utils.isSmilePoint();
    this.selectedTeam = Utils.selectedTeam();
    this.clientName = localStorage.getItem("selected_clientName");
    this.route.paramMap.subscribe(params => {
      this.claimUUid = params.get('uuid') || '';
      this.fetchClaimRights(this.claimUUid);
    });

  }

  fetchClaimRights(uuid: string) {
    let ths = this;
    ths.claimService.getClaimSectionRights(
      (res: any) => {
        if (res.status === 200) {
          console.log(res.data);
          this.sectionLevelData = res.data;
          ths.setClaimSectionRights(res);
          ths.fetchClaimSteps(uuid);
          ths.fetchClaimsByUuid(uuid);


        }
      });

  }

  setClaimSectionRights(res: any) {
    this.claimSettingDataModel = res.data[0];

  }

  checkForSectionAccess(sectionid: number, accessType: string): boolean {
    const result = this.claimSettingDataModel?.teamsWithSections[0].sectionData.filter(obj => obj.sectionId === sectionid);
    if (result != null && result.length == 1) {
      if (accessType === 'view') return result[0].viewAccess;
      else return result[0].editAccess;
    }
    return false;
  }

  fetchClaimsByUuid(uuid: string) {
    let ths = this;
    ths.claimUUid = uuid;
    ths.updateUrl("/billing-claims/" + uuid);
    this.loader.claimDetail = this.loader.linkToRelatedDoc = true;
    ths.claimService.fetchBillingClaimsByUuid(uuid, (res: any) => {
      if (res.status === 200) {
        this.loader.claimDetail = this.loader.linkToRelatedDoc = false;
        if (res.data != null) {
          ths.claimRcm = res.data;
          if (ths.claimRcm.preferredModeOfSubmission != null) {
            if (ths.claimRcm.preferredModeOfSubmission != "") {
              let prfMode = ths.claimRcm.preferredModeOfSubmission;
              ths.submissionDto.channel = prfMode;

            }
          }
          //-O- Means its delete Intentionally
          if (ths.claimRcm.ivfId != null) {
            ths.claimRcm.ivfId = ths.claimRcm.ivfId.replace("-O-", "");
            if (ths.claimRcm.ivfId == '') {
              ths.claimRcm.assignmentOfBenefits = 'N/A';
            }
          } else {
            ths.claimRcm.assignmentOfBenefits = 'N/A';
          }
          if (ths.claimRcm.assignmentOfBenefits != null && ths.claimRcm.assignmentOfBenefits == '')
            ths.claimRcm.assignmentOfBenefits = "No";
          if (ths.claimRcm.tpId != null) {
            ths.claimRcm.tpId = ths.claimRcm.tpId.replace("-O-", "");
          }

          this.updatedIvfId = ths.claimRcm.ivfId;
          this.updatedTpId = ths.claimRcm.tpId;
          ths.infoMessage = (!ths.claimRcm.primary && ths.claimRcm.assoicatedClaimStatus) ? "Primary Claim is Open" : "";
          ths.fetchOtherTeamRemarks();
          ths.fetchClaimNotes();
          ths.getServiceLevelCodes();
          ths.getSubmissionDetails();
          //if (this.smilePoint) ths.getClaimRuleData();//Only In case of Smile point. other does not have it.
          //ths.runAutoRules(false);
          ths.fetchTLUsers();
          ths.fetchOtherTeams();
          ths.fetchAttachmentCount();
        } else {
          this.invalidClaim = "- Not authorized to view this claim";

        }
      }
      /**/

      this.fetchClaimLevelInfoSection();
      this.fetchAppealSection();
      this.fetchInsurancePaymentInfoSection();
      this.fetchEobSection();
      this.fetchServiceLevelInfoSection();
      this.fetchInsuranceFollowUpSection();
      this.fetchPatientStatementSection();
      this.fetchPatientPaymentSection();
      this.fetchNextActionRequiredSection();
      this.fetchPatientCommunicationSection();
    });
  }

  fetchClaimNotes() {
    this.claimService.fetchClaimNotes(this.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        // ths.claimRcm= res.data;
        this.claimRcm.claimNotes = res.data;

      }
    })

  }
  fetchClaim(uuid: string, primary: boolean, type: string) {
    let ths = this;
    if (type === 'Primary' && primary) return;//NO Need to reclick
    if (type === 'Secondary' && !primary) return;////NO Need to reclick
    if (uuid) {
      ths.resetOldApiData();
      ths.fetchClaimsByUuid(uuid);

    }
  }

  resetOldApiData() {
    let ths = this;
    ths.claimRcm = { claimId: "" };
    ths.claimServiceLevelModel = { claimFound: false };
    ths.submissionDto = {};
    ths.claimRules = [];
    ths.claimRuleRemarks = [];
    ths.ruleEngineReport = [];
    ths.reheading = "";
    ths.tlUsers = [];
    ths.teamsMs = [];
    ths.otherTeamRemarks = [];
  }
  //getIVFData(){

  //let ths=this;

  // ths.appService.fetchivfDataForClaim(ths.claimUUid,(res:any)=>{
  //   if (res.status=== 200){

  //   }

  // });
  //ths.ivfData = 'dsf'
  // }

  saveClaim(type: string, byPassPendingCheck: boolean) {
    //Only for Pending claims
    let ths = this;
    ths.assignType = type;
    ths.claimEditModel = {};
    ths.claimEditModel.claimUuid = ths.claimRcm.uuid;
    ths.claimEditModel.ruleEngineRunRemark = ths.claimRcm.ruleEngineRunRemark;
    ths.claimEditModel.claimNoteDtoList = ths.claimRcm.claimNotes;
    ths.claimEditModel.claimRemark = ths.claimRcm.claimRemarks;
    debugger;
    ths.claimEditModel.serCVDto = ths.claimServiceLevelModel?.dto;
    ths.claimEditModel.submissionDto = ths.submissionDto;
    ths.claimEditModel.ruleRemarkDto = [];
    ths.claimEditModel.byPassPendingCheck = byPassPendingCheck;
    ths.claimRules.forEach(x => {
      x.sectionName = "ClaimLevelValidation"
      //if (x.remark != null && x.remark.trim() != '') 
      ths.claimEditModel.ruleRemarkDto.push(x);

    });
    ths.ruleEngineReport.forEach(x => {

      let c = new ClaimRuleRemarkModelS(x.remark, null, null, null, Number(x.ruleId), "RuleEngine");
      if (x.remark != null) ths.claimEditModel.ruleRemarkDto.push(c);
    });

    if (type === 'latter') {

      ths.inSave = true;
      ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
        ths.inSave = false;
        this.showAlertPopup(callback);
      })
    }
    else if (type === 'submit') {
      //do From Validation
      //debugger;
      let valid = ths.validateData();
      let validSec = true;
      if (valid && validSec) {

        let prefer = this.checkForValidPreferredSbmisssion();

        if (!prefer && !this.overidePreferredSubmission) {
          ths.openPrefferedModal();
          return;
        }
        ths.claimEditModel.submission = true;
        ths.inSave = true;
        ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
          ths.inSave = false;
          this.showAlertPopup(callback);
        });
      }
    }
    else if (type === 'assign') {
      ths.claimEditModel.assignTouuid = "";
      ths.claimEditModel.assignToTeam = -1;
      //debugger;
      ths.assignModel.toOtherTeam = true;
      let valid = true;
      //if (!this.isSuperAdmin) valid = ths.validateData(); //no need to check for validation
      if (ths.isInternalAudit && ths.assignType == 'reviewed') {
        //ths.claimEditModel.assignToTeam = ths.teamsMs[0].teamId;
      }

      if (valid) {
        ths.openAssignModal('other');

        //Open Modal
        /*
        ths.claimEditModel.submission=false;
        ths.claimService.saveClaimData(ths.claimEditModel,(callback: any)=>{
          ths.inSave=false;
          this.showAlertPopup(callback);
        });
       */
      }
    } else if (type === 'reviewed') {
      ths.claimEditModel.assignTouuid = "";
      ths.claimEditModel.assignToTeam = -1;
      //debugger;
      ths.assignModel.toOtherTeam = true;
      let valid = true;
      if (!this.isSuperAdmin) valid = ths.validateData();
      let validSec = true;
      /*if (!ths.claimRcm.pending) {//if Already submitted one Time
        ths.checkSectionFieldValidation(false, true, false);
        validSec = ths.isSectionValidated;
      }*/
      if (ths.isInternalAudit) {

        ths.claimEditModel.assignToTeam = 7;//ths.teamsMs[0].teamId;
      }

      if (valid && validSec) {
        ths.openAssignModal('other');

        //Open Modal
        /*
        ths.claimEditModel.submission=false;
        ths.claimService.saveClaimData(ths.claimEditModel,(callback: any)=>{
          ths.inSave=false;
          this.showAlertPopup(callback);
        });
       */
      }
    }
    else if (type === 'assigntl') {
      ths.claimEditModel.assignTouuid = "";

      ths.openAssignModal('tl');
    } else if (type === 'assign') {
      ths.claimEditModel.assignTouuid = "";
      ths.claimEditModel.assignToTeam = -1;
      //debugger;
      ths.assignModel.toOtherTeam = true;
      let valid = true;
      //if (!this.isSuperAdmin) valid = ths.validateData(); //no need to check for validation
      if (ths.isInternalAudit && ths.assignType == 'reviewed') {
        //ths.claimEditModel.assignToTeam = ths.teamsMs[0].teamId;
      }

      if (valid) {
        ths.openAssignModal('other');

        //Open Modal
        /*
        ths.claimEditModel.submission=false;
        ths.claimService.saveClaimData(ths.claimEditModel,(callback: any)=>{
          ths.inSave=false;
          this.showAlertPopup(callback);
        });
       */
      }
    } else if (type === 'assignafterpendingnot_bill_internal') {
      ths.claimEditModel.submission = false;
      ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
        ths.finalSaveSection(true, true);
      });
    } else if ("reviewedafterpendingbyinternalaudit") {
      let valid = true;
      ths.claimEditModel.submission = false;
      if (!this.isSuperAdmin) valid = ths.validateData();
      if (valid) {
        ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
          ths.finalSaveSection(true, true);
        });
      }
    } else if (type === 'submitafterpending') {
      //do From Validation
      //debugger;
      ths.claimEditModel.submission = true;
      let valid = ths.validateData();
      let validSec = true;
      if (valid && validSec) {

        let prefer = this.checkForValidPreferredSbmisssion();

        if (!prefer && !this.overidePreferredSubmission) {
          ths.openPrefferedModal();
          return;
        }
        ths.claimEditModel.submission = true;
        ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
          ths.finalSaveSection(true, true);
        });
      }
    }


  }


  addErrorDisplay(el: HTMLElement) {
    if (el != null) el.classList.add('bg-error');
  }

  removeErrorDisplay(el: HTMLElement) {
    if (el != null) el.classList.remove('bg-error');
  }

  removeErrorDisplayKey(event: any) {
    // console.log(event.target.value);
    if (event.target.value.trim() !== '') {
      event.target.classList.remove('bg-error');
    }

  }

  removeErrorDisplayKeyById(id: any, sub?: any) {
    this.removeErrorDisplay(document.getElementById(id + (sub != undefined ? sub : "")));

  }

  validateData(): boolean {
    let ths = this;
    let valid: boolean = true;
    debugger;
    ths.claimRules.forEach(x => {
      //console.log(x.ruleId);
      if ((x.remark == null || x.remark.trim() === '') && x.messageType === 1 && x.ruleType == 'C'
        && document.getElementById("CL_RU" + x.ruleId) != null) {//on NO only
        ths.addErrorDisplay(document.getElementById("CL_RU" + x.ruleId));
        valid = false;
      }
      if (x.messageType === 0 && x.ruleType == 'C' && document.getElementById("CL_P_F_" + x.ruleId) != null) {//mark the Yes or No
        ths.addErrorDisplay(document.getElementById("CL_P_F_" + x.ruleId));
        valid = false;
      }
      if (x.ruleId == 300 && !ths.isProviderNotesNeeded) {
        if (x.messageType === 2) {
          ths.claimRcm.claimNotes.forEach(no => {
            if (no.value == null || no.value.trim() === '') {
              ths.addErrorDisplay(document.getElementById("CL_N_" + no.id));
              valid = false;

            }
          });
        } else {
          ths.claimRcm.claimNotes.forEach(no => {
            //condition added for external clients
            if (document.getElementById("CL_N_" + no.id) != null) {
              ths.removeErrorDisplayKeyById("CL_N_" + no.id);
            }
          });
        }
      }
    });

    if (!ths.isInternalAudit && ths.checkForSectionAccess(ths.sectionIds['CLAIM_SUBMISSION']['sectionId'], 'edit')) {//Only Non Audit can Submit
      if (Object.keys(ths.submissionDto).length == 0) {
        ths.addErrorDisplay(document.getElementById("SUB_DET_CHA"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_ATT"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_PRE"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_REF"));
        if (!ths.claimNoEnable()) {

          ths.addErrorDisplay(document.getElementById("SUB_DET_CLA"));
        }
        if (!ths.preAuthEnable(ths.sectionIds['CLAIM_SUBMISSION']['sectionId'], 'edit')) {
          ths.addErrorDisplay(document.getElementById("SUB_DET_PRENO"));
        }
        ths.addErrorDisplay(document.getElementById("SUB_DET_DT"));
        //ths.addErrorDisplay(document.getElementById("SUB_DET_TI"));
        valid = false;
      } else {
        if (ths.submissionDto.channel === undefined || ths.submissionDto.channel === null) {
          ths.addErrorDisplay(document.getElementById("SUB_DET_CHA"));
          valid = false;
        }
        if (ths.submissionDto.attachmentSend === undefined || ths.submissionDto.attachmentSend === null) {
          ths.addErrorDisplay(document.getElementById("SUB_DET_ATT"));
          valid = false;
        }
        if (ths.submissionDto.preauth === undefined || ths.submissionDto.preauth === null) {
          ths.addErrorDisplay(document.getElementById("SUB_DET_PRE"));
          valid = false;
        }
        if (ths.submissionDto.refferalLetter === undefined || ths.submissionDto.refferalLetter === null) {
          ths.addErrorDisplay(document.getElementById("SUB_DET_REF"));
          valid = false;
        }
        if (!ths.claimNoEnable()) {


          let SUB_DET_CLA: any = document.getElementById("SUB_DET_CLA");
          if (SUB_DET_CLA.value.trim() === '') {
            ths.addErrorDisplay(document.getElementById("SUB_DET_CLA"));
            valid = false;
          }
        } else {
          ths.removeErrorDisplayKeyById('SUB_DET_CLA');
        }
        if (!ths.preAuthEnable(ths.sectionIds['CLAIM_SUBMISSION']['sectionId'], 'edit')) {
          let SUB_DET_PRENO: any = document.getElementById("SUB_DET_PRENO");
          if (SUB_DET_PRENO.value.trim() === '') {
            ths.addErrorDisplay(document.getElementById("SUB_DET_PRENO"));
            valid = false;
          }
        } else {
          ths.removeErrorDisplayKeyById('SUB_DET_PRENO');
        }
        let SUB_DET_DT: any = document.getElementById("SUB_DET_DT");
        if (SUB_DET_DT.value.trim() === '') {
          ths.addErrorDisplay(document.getElementById("SUB_DET_DT"));
          valid = false;
        }
        if (!ths.providerRefNoEnable()) {
          let SUB_DET_PRENO: any = document.getElementById("SUB_DET_PORVIDER_RENO");
          if (SUB_DET_PRENO.value.trim() === '') {
            ths.addErrorDisplay(document.getElementById("SUB_DET_PORVIDER_RENO"));
            valid = false;
          }
        } else {
          ths.removeErrorDisplayKeyById('SUB_DET_PORVIDER_RENO');
        }
      }
      /*let SUB_DET_TI: any = document.getElementById("SUB_DET_TI");
      if (SUB_DET_TI.value.trim() === '') {
        ths.addErrorDisplay(document.getElementById("SUB_DET_TI"));
        valid = false;
      }*/


    }

    if (ths.claimServiceLevelModel.claimFound) {

      ths.claimServiceLevelModel.dto.forEach((x, i) => {
        //debugger;
        if ((x.manualAuto == 'Automated' && (x.remark == null || x.remark.trim() === '')) && x.messageType === 1) {//on NO only
          ths.removeErrorDisplayKeyById("SERV_C_V_A" + x.remarkUuid);

        }

      });
    }
    if (this.smilePoint) {
      if (ths.claimServiceLevelModel.claimFound) {

        ths.claimServiceLevelModel.dto.forEach((x, i) => {
          //debugger;
          if ((x.manualAuto == 'Automated' && (x.remark == null || x.remark.trim() === '')) && x.messageType === 1) {//on NO only
            ths.addErrorDisplay(document.getElementById("SERV_C_V_A" + x.remarkUuid));
            valid = false;
          }

        });
        ths.claimServiceLevelModel.dto.forEach((x, i) => {
          if ((x.manualAuto == 'Manual') && (x.answer == null || x.answer.trim() === '')) {//on NO only
            ths.addErrorDisplay(document.getElementById("serviceCodeValidationsM" + x.remarkUuid));
            valid = false;
          } else if ((x.manualAuto == 'Manual' && (x.remark == null || x.remark.trim() === '')) && x.answer === 'Incorrect') {//on NO only
            ths.addErrorDisplay(document.getElementById("SERV_C_V_M" + x.remarkUuid));
            valid = false;
          }
        });
      } else {
        ths.addErrorDisplay(document.getElementById("serviceCodeValidationsA"));
        ths.addErrorDisplay(document.getElementById("serviceCodeValidationsM"));
      }
    }
    if (document.getElementById("claimValidationsRE") != null) {
      ths.removeErrorDisplay(document.getElementById("claimValidationsRE"));

    }
    if (document.getElementById("ruleEngineRunRemark") != null) {
      ths.removeErrorDisplay(document.getElementById("ruleEngineRunRemark"));

    }
    if (this.smilePoint && !this.isInternalAudit && ths.isRuleEnginevalidationNeeded()) {
      //debugger;
      let inRE: boolean = false;
      ths.ruleEngineReport.forEach(x => {
        inRE = true;
        if (x.mtype === '1' && (x.remark == null || x.remark.trim() === '')) {
          ths.addErrorDisplay(document.getElementById("ENG_REP_" + x.ruleId));
          valid = false;
        }
      });
      if (!inRE) {
        if (document.getElementById("claimValidationsRE") != null) {
          if (document.getElementById("ruleEngineRunRemark") != null && (ths.claimRcm.ruleEngineRunRemark == null || ths.claimRcm.ruleEngineRunRemark.trim() === '')) {
            ths.addErrorDisplay(document.getElementById("ruleEngineRunRemark"));
            valid = false;
          }
          //ths.addErrorDisplay(document.getElementById("claimValidationsRE"));
        }

      }


    }
    //valid = false;
    console.log("valid", valid);

    return valid;

  }


  claimNoEnable(): boolean {
    let el = document.getElementById("SUB_DET_CLA");
    if (el == null) return true;
    el.style.display = "";
    document.getElementById("SUB_DET_CLA_SUB").style.display = "";

    if (this.submissionDto?.channel == 'Portal') {

      return false;
    }
    document.getElementById("SUB_DET_CLA").style.display = "none";
    document.getElementById("SUB_DET_CLA_SUB").style.display = "none";
    return true;
  }

  preAuthEnable(sectionId: number, accessType: string): boolean {
    var right = this.checkForSectionAccess(sectionId, accessType);
    if (!right) return true;
    document.getElementById("SUB_DET_PRENO").style.display = "none";
    if (this.submissionDto?.preauth == true) {
      document.getElementById("SUB_DET_PRENO").style.display = "";
      return false;
    } return true;
  }

  /*Refferal Letter Changed to Provider Change Reference Number Needed*/
  providerRefNoEnable(): boolean {
    document.getElementById("SUB_DET_PORVIDER_RENO").style.display = "none";
    if (this.submissionDto?.refferalLetter == true) {
      document.getElementById("SUB_DET_PORVIDER_RENO").style.display = "";
      return false;
    } return true;
  }

  switchType(type: any) {
    if (type == 'pass') {
      this.mtype = '2';
    } else if (type == 'fail') {
      this.mtype = '1';
    } else if (type === 'alert') {
      this.mtype = '3'
    }
  }



  showHide(toggleTabName: any) {

    this.toggleTab[toggleTabName] = !this.toggleTab[toggleTabName];

    // let el: any = document.querySelectorAll(".bold-b-text");
    // for (let i = 0; i < el.length; i++) {
    //   if (i == index) {
    //     el[i].children[1].children[0].classList.toggle("close");
    //     el[i].classList.toggle("collapsed");
    //     el[i].nextElementSibling.classList.toggle("show");
    //   }
    // }
  }

  getServiceLevelCodes() {
    let ths = this;
    this.loader.serviceCode = true;
    ths.claimService.getServiceLevelCodes(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        this.loader.serviceCode = false;
        ths.claimServiceLevelModel = res.data;
        ths.ignoreSomeRules();
        if (ths.claimServiceLevelModel.esDate != null) {
          if (ths.claimServiceLevelModel.esDate != "") {
            //ths.claimRcm.dateLastUpdatedES = ths.claimServiceLevelModel.esDate;
          }
        }

      } else {
        this.loader.serviceCode = false;
        ths.claimServiceLevelModel;
      }
      ths.getClaimRuleData();
    })

  }

  getSubmissionDetails() {
    let ths = this;
    ths.claimService.getSubmissionDetails(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        ths.submissionDto = res.data;
        if (ths.submissionDto == null) {
          ths.submissionDto = {};
          if (ths.claimRcm.preferredModeOfSubmission != "") {
            let prfMode = ths.claimRcm.preferredModeOfSubmission;
            ths.submissionDto.channel = prfMode;
          }
        }
      }
    })
  }

  generateManualIdOfRule() {
    let ths = this;
    let ctr = 0;
    ths.claimRules.forEach((rule: any) => {
      if (rule.ruleId == 318 &&
        ((ths.claimRcm.primary && (ths.claimRcm.primaryInsCode == 'CMC' || ths.claimRcm.primaryInsCode == 'AMC' || ths.claimRcm.primaryInsCode == 'MCR' || ths.claimRcm.primaryInsCode == 'CHIP'))
          || (!ths.claimRcm.primary && (ths.claimRcm.secondaryInsType == 'CMC' || ths.claimRcm.secondaryInsType == 'AMC' || ths.claimRcm.secondaryInsType == 'MCR' || ths.claimRcm.secondaryInsType == 'CHIP')))) {
        rule.srNo = 1;
        ctr = 1;
      } else if (rule.ruleId == 319 &&
        ((ths.claimRcm.primary && (ths.claimRcm.primaryInsCode == 'CMC' || ths.claimRcm.primaryInsCode == 'AMC' || ths.claimRcm.primaryInsCode == 'MCR' || ths.claimRcm.primaryInsCode == 'CHIP'))
          || (!ths.claimRcm.primary && (ths.claimRcm.secondaryInsType == 'CMC' || ths.claimRcm.secondaryInsType == 'AMC' || ths.claimRcm.secondaryInsType == 'MCR' || ths.claimRcm.secondaryInsType == 'CHIP')))) {
        rule.srNo = 2;
        ctr = 2;
      } else if (rule.ruleId == 321 &&
        ((ths.claimRcm.primary && (ths.claimRcm.primaryInsCode == 'CMC' || ths.claimRcm.primaryInsCode == 'AMC' || ths.claimRcm.primaryInsCode == 'MCR' || ths.claimRcm.primaryInsCode == 'CHIP'))
          || (!ths.claimRcm.primary && (ths.claimRcm.secondaryInsType == 'CMC' || ths.claimRcm.secondaryInsType == 'AMC' || ths.claimRcm.secondaryInsType == 'MCR' || ths.claimRcm.secondaryInsType == 'CHIP')))) {
        rule.srNo = 3;
        ctr = 3;
      } else if (rule.ruleId == 322 &&
        ((ths.claimRcm.primary && (ths.claimRcm.primaryInsCode == 'CMC' || ths.claimRcm.primaryInsCode == 'AMC' || ths.claimRcm.primaryInsCode == 'MCR' || ths.claimRcm.primaryInsCode == 'CHIP'))
          || (!ths.claimRcm.primary && (ths.claimRcm.secondaryInsType == 'CMC' || ths.claimRcm.secondaryInsType == 'AMC' || ths.claimRcm.secondaryInsType == 'MCR' || ths.claimRcm.secondaryInsType == 'CHIP')))) {
        rule.srNo = 4;
        ctr = 4
      }
    });

    ths.claimRules.forEach((rule: any) => {
      if ((rule.ruleId == 320) &&
        ((ths.claimRcm.primary && (ths.claimRcm.primaryInsCode == 'CMC' || ths.claimRcm.primaryInsCode == 'CHIP'))
          || (!ths.claimRcm.primary && (ths.claimRcm.secondaryInsType == 'CMC' || ths.claimRcm.secondaryInsType == 'CHIP')))) {
        rule.srNo = ctr + 1;
        ctr = ctr + 1;
      }
    });

    ths.claimRules.forEach((rule: any) => {
      if (rule.ruleId == 300) {
        rule.srNo = ctr + 1;
        ctr = ctr + 1;
        ths.serialNoArray.set("300", rule.srNo);
      }
    });


  }
  getRulesClaimdata() {
    let ths = this;
    //debugger;
    ths.loader.automatedValidation = ths.loader.manualValidation = true;
    ths.claimService.getRulesClaimdata(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        ths.loader.automatedValidation = ths.loader.manualValidation = false;
        ths.claimRules = res.data;
        ths.generateManualIdOfRule();
        ths.countA.pass = ths.countA.fail = ths.countA.alert = 0;
        ths.countAS.pass = ths.countAS.fail = ths.countAS.alert = 0;
        //debugger;
        ths.claimRules.forEach((e: any) => {
          if (e.ruleId == 300 && (e.messageType != null && e.messageType == 3)) {
            ths.relatedTo_300 = false;
          }
          if (e.messageType == 1 && e.manualAuto === "AUTO" && (e.ruleType === 'C' || e.ruleType === 'R,C')) {
            this.countA.fail = this.countA.fail + 1;
          }
          else if (e.messageType == 2 && e.manualAuto === "AUTO" && (e.ruleType === 'C' || e.ruleType === 'R,C')) {
            this.countA.pass = this.countA.pass + 1;
          }
          else if (e.manualAuto == 'AUTO' && (e.ruleType === 'G')) {
            //add filter here and update .. claimServiceLevelModel
            //dec6e068-1977-4881-8893-26182b0368ec
            if (this.claimServiceLevelModel != undefined && this.claimServiceLevelModel.dto != undefined) {
              let v: ServiceLevelCodeDataModel = this.claimServiceLevelModel.dto.find(x => x.ruleId == e.ruleId);
              if (v != null) {

                v.message = e.message;
                v.messageType = e.messageType;
                if (e.messageType == 1) this.countAS.fail = this.countAS.fail + 1;
                if (e.messageType == 2) this.countAS.pass = this.countAS.pass + 1;

              }

            }
          }
        });
        ths.actionButtons = true;
        ths.getRuleRemarks();
        // if (ths.submissionDto==null) ths.submissionDto={};
      }
    })
  }

  getRuleRemarks() {

    let ths = this;
    ths.claimService.getRuleRemarks(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        ths.claimRuleRemarks = res.data;
        if (ths.claimRuleRemarks.length > 0) {
          //put in 
          ths.claimRules.forEach(x => {
            let filter: Array<ClaimRuleRemarkModel> = ths.claimRuleRemarks.filter(s => s.ruleId === x.ruleId);
            if (filter && filter.length == 1) {
              x.remark = filter[0].remark;
            }
          });

          ths.ruleEngineReport.forEach(x => {
            let filter: Array<ClaimRuleRemarkModel> = ths.claimRuleRemarks.filter(s => s.ruleId === Number(x.ruleId));
            if (filter && filter.length == 1) {
              x.remark = filter[0].remark;
            }
          });

        }

      }
    })

  }

  runAutoRules(reReun: boolean) {

    let ths = this;
    ths.actionButtons = false;
    ths.claimService.runAutoRules(ths.claimRcm.uuid, reReun, (res: any) => {
      if (res.status === 200) {
        if (res.data.message === "success") {
          ths.claimRcm.providerOnClaim = res.data.providerOnClaim;
          ths.claimRcm.assignmentOfBenefits = res.data.assignmentOfBenefits;
          ths.claimRcm.claimType = res.data.claimType;
          ths.claimServiceLevelModel = res.data.serviceValidationDataDto;
          ths.claimRcm.preferredModeOfSubmission = res.data.preferredModeOfSubmission;
          ths.overidePreferredSubmission = false;
        }
        ths.getRulesClaimdata();
        setTimeout(() => {
          ths.actionButtons = true;
        }, 2000);
      }
    })
  }

  getClaimRuleData() {
    let ths = this;
    if (this.smilePoint) {
      ths.loader.ruleEngValid = true;
      ths.claimARulesPullDataModel.claimId = ths.claimRcm.claimId.split("_")[0];//"15927";///
      ths.claimARulesPullDataModel.officeId = ths.claimRcm.officeUuid;//"cc450da8-aaae-11e8-8544-8c16451459cd";//
      ths.claimARulesPullDataModel.patientId = ths.claimRcm.patientId;//"6602";//TESTING

      ths.claimService.getClaimRuleData(ths.claimARulesPullDataModel, (res: any) => {
        if (res.status === 200) {
          ths.loader.ruleEngValid = false;
          ths.getRulesClaimdata();
          ths.ruleEngineReport = res.data;////Rule Engine Data
          if (ths.ruleEngineReport.length > 0)
            ths.generateRuleEngineReportHeading(ths.ruleEngineReport[0]);

          this.count.pass = this.count.fail = this.count.alert = 0;
          this.ruleEngineReport.forEach((e: any) => {
            if (e.mtype == 1) {
              this.count.fail = this.count.fail + 1;
            }
            else if (e.mtype == 2) {
              this.count.pass = this.count.pass + 1;
            } else if (e.mtype == 3) {
              this.count.alert = this.count.alert + 1;
            }
          });

        }
      })
    } else {
      ths.getRulesClaimdata();
    }
  }

  generateRuleEngineReportHeading(rule: RuleEngineValModel) {

    this.reheading = '<span>Claim ID: <span>' + rule.claimId + ' of ' + rule.officeName + ' office</span></span>' +
      '<span> | Pt.ID: <span>' + rule.patientId + '</span></span>' +
      '<span> | IV.ID: <span>' + rule.ivId + '</span></span>' +
      '<span> | Pt.Name: <span>' + rule.patientName + '</span></span>' +
      '<span> | DOS: <span>' + rule.dos + '</span></span>' +
      '<span> | Tx plan Date: <span>' + rule.dos + '</span></span>' +
      '<span> | Ins.Type : <span>' + rule.insuranceType + '</span></span>' +
      '<span> | Run Date : <span>' + this.datepipe.transform(rule.runDate, 'MMM dd, YYYY'); + '</span></span>';

  }

  passFail(t: any, type: number) {
    t = type;
  }

  /*
  The claims that will be parked in the Internal Audit Team's bucket first, only sections billing team will be filling will include:
  a) Rules Engine Validations
  b) Enter Claim Submission Details:
  */
  makeReadOnlyWithPermission(sectionId: any): boolean {
    var right = this.checkForSectionAccess(sectionId, 'edit');

    if (this.claimRcm == undefined) return true;
    //if (!this.isBilling) return true;
    else if (!right) return true;
    //else if (!this.claimRcm.pending) return true;
    else if (!this.claimRcm.allowEdit) return true;
    if (this.claimRcm.firstTeamId == 3 && this.isBilling
    ) { //use case claim->/e28dd916-4da7-45c7-9884-ee6fd3cac759
      return true;
    }
    return false;
  }

  makeReadOnly(): boolean {
    if (this.claimRcm == undefined) return true;
    //if (!this.isBilling) return true;
    //else if (!this.claimRcm.pending) return true;
    else if (!this.claimRcm.allowEdit) return true;
    if (this.claimRcm.firstTeamId == 3 && this.isBilling
    ) { //use case claim->/e28dd916-4da7-45c7-9884-ee6fd3cac759
      return true;
    }
    return false;
  }

  isSectionReadOnly(sectionId: number, accessType: string): boolean {
    //debugger;
    var right = this.checkForSectionAccess(sectionId, accessType);
    if (!right) return true;
    if (this.claimRcm == undefined) return true;
    if (!this.claimRcm.allowEdit) return true;
    //if (!this.claimRcm.pending) return true;
    if (this.claimRcm.firstTeamId == 3 && this.isBilling
    ) {
      return false;
    }
    if (this.claimRcm.firstTeamId == 7 && this.isBilling
    ) {
      return false;
    }
    if (this.claimRcm.firstTeamId == 3 && this.isInternalAudit
    ) {
      return false;
    }
    return true;
  }

  getTeamName(teamId: string): string {

    let team = Number(teamId);
    if (!isNaN(team)) return this.appConstants.TEAMS_CONFIG.get(Number(team))?.name;

    return "NA";
  }

  showAlertPopup(res: any) {
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 5000);
    if (res.status == 200) {
      this.alert.isError = false;

      this.alert.alertMsg = res.data.message;
      if (this.alert.alertMsg === 'Latter') {
        this.alert.alertMsg = "Saved for Latter Processing!!";
      } else if (this.alert.alertMsg === 'Submitted') {
        this.alert.alertMsg = "Claim Submitted Successfully!!";
        this.claimRcm.allowEdit = false;
        window.location.reload();

      } else if (this.alert.alertMsg === 'OtherTeam') {
        this.alert.alertMsg = "Claim Assigned To Other Team Successfully!!";
        this.claimRcm.allowEdit = false;
        window.location.reload();
      } else if (this.alert.alertMsg === 'TL') {
        this.alert.alertMsg = "Claim Assigned To TL For Preview!!";
        this.claimRcm.allowEdit = false;
      } else if (this.alert.alertMsg === 'TL') {
        this.alert.alertMsg = "Claim Assigned To TL For Preview!!";
        this.claimRcm.allowEdit = false;
      }

    } else {
      this.alert.isError = true;
      this.alert.alertMsg = "Error";
    }

  }

  showAlertAssignPopup(res: any) {
    this.alertAssign.showAlertPopup = true;
    setTimeout(() => { this.alertAssign.showAlertPopup = false; }, 2000);
    if (res.status == 200) {
      this.alertAssign.isError = false;
      this.alertAssign.alertMsg = res.message;
    } else {
      this.alertAssign.isError = true;
      this.alertAssign.alertMsg = "Error";
    }

  }


  closeModal() {
    this.modelElement.modal.style.display = "none";
  }

  openAssignModal(type: string) {
    this.assignModel.toOtherTeam = this.assignModel.toLead = false;
    if (type === 'tl') this.assignModel.toLead = true;
    if (type === 'other') this.assignModel.toOtherTeam = true;
    this.modelElement.modal = document.getElementById("assigment-modal");
    this.modelElement.span = document.getElementsByClassName("close")[0];
    this.modelElement.modal.style.display = "block";
  }

  fetchTLUsers() {
    let ths = this;
    ths.appService.fetchTLUsers((res: any) => {
      if (res.status === 200) {
        ths.tlUsers = res.data;

      }
    })
  }


  fetchOtherTeams() {
    let ths = this;
    ths.appService.fetchOtherTeams((res: any) => {
      if (res.status === 200) {
        ths.teamsMs = res.data;

      }
    })
  }

  fetchOtherTeamRemarks() {
    let ths = this;
    ths.loader.remarksByOther = true;
    ths.claimService.fetchOtherTeamRemarks(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        ths.loader.remarksByOther = false;
        ths.otherTeamRemarks = res.data;
      }
    })

  }

  reval() {

    this.runAutoRules(true);
  }

  assignToOtherTeam() {
    console.log(this.claimEditModel.assignToTeam);
    this.isOtherTLExist(this.alert, (res: any) => {
      if (res) {

        let ths = this;
        ths.otherErrormsg = "";
        ths.claimEditModel.assignToOtherTeam = true;
        ths.claimEditModel.assignToTL = false;
        ths.claimEditModel.assignTouuid = '';
        ths.removeErrorDisplay(document.getElementById("selectTeam"));
        ths.removeErrorDisplay(document.getElementById("assignToComment"));
        let valid = true;

        let rem: any = document.getElementById("assignToComment");
        if (rem.value.trim() === '') {
          ths.addErrorDisplay(document.getElementById("assignToComment"));
          valid = false;
        }
        //debugger;
        if (ths.claimEditModel.assignToTeam == -1) {
          ths.addErrorDisplay(document.getElementById("selectTeam"));//selectTeam
          valid = false;
        }

        if (valid) {
          //assign
          ths.inSave = true;
          ths.closeModal();
          console.log(ths.claimEditModel);
          debugger;
          ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
            ths.inSave = false;
            ths.showAlertPopup(callback);
            ths.claimRcm.allowEdit = false;
            ths.showAlertPopup(callback);
          });
        }
      }
    })
  }

  assignToLead() {
    let ths = this;
    ths.tlErrormsg = "";
    this.claimEditModel.assignToTL = true;
    ths.claimEditModel.assignToOtherTeam = false;
    ths.claimEditModel.assignToTeam = 0;
    let valid = true;

    ths.removeErrorDisplay(document.getElementById("selectLeadName"));
    ths.removeErrorDisplay(document.getElementById("tlRemark"));
    let rem: any = document.getElementById("tlRemark");
    if (rem.value.trim() === '') {
      ths.addErrorDisplay(document.getElementById("tlRemark"));
      valid = false;
    }
    if (ths.claimEditModel.assignTouuid == "") {
      ths.addErrorDisplay(document.getElementById("selectLeadName"));
      valid = false;
    }
    if (ths.claimRcm.assignedToUuid === ths.claimEditModel.assignTouuid) {
      valid = false;
      ths.tlErrormsg = "Claim Already Assigned to Same TL";

    }
    if (valid) {
      //Save Data
      ths.claimAssignToTeamModel.claimUuid = ths.claimUUid;
      ths.claimAssignToTeamModel.otherTeamId = -1;
      ths.claimAssignToTeamModel.remark = rem.value;
      ths.claimAssignToTeamModel.teamLeadUuid = ths.claimEditModel.assignTouuid;
      ths.claimAssignToTeamModel.toLead = true;
      ths.claimService.assignClaimToTL(ths.claimAssignToTeamModel, (res: any) => {
        if (res.status === 200) {
          window.location.reload();

        }
      })
    }
  }

  openUpdateIvPopup(event: any) {
    event.stopPropagation();
    let popup: any = document.getElementById("ivUpdate");
    this.updatedIvfId = this.claimRcm.ivfId;
    this.updatedTpId = this.claimRcm.tpId;
    popup.style.display = 'block';
  }

  openDeleteIvTPopup(event: any, type: string) {
    event.stopPropagation();
    this.deleteType = type;
    let popup: any = document.getElementById("iVTPDEL");
    popup.style.display = 'block';
  }

  closeIvPopup() {
    let popup: any = document.getElementById("ivUpdate");
    popup.style.display = 'none';
    this.updatedIvfId = '';
  }

  closeDelPopup() {
    let popup: any = document.getElementById("iVTPDEL");
    popup.style.display = 'none';
    this.deleteType = '';
  }

  updateIV(claimUuid: any, ivId: any, tpid: any) {
    let params: any = {
      'claimUuid': claimUuid,
      'ivfId': ivId,
      'tpId': tpid
    };

    this.appService.updateIvId(params, (res: any) => {
      if (res.status) {
        //debugger;
        if (res.data.success) {
          if (res.data.ivfId != null) this.claimRcm.ivfId = res.data.ivfId;
          if (res.data.ivDos != null) this.claimRcm.ivDos = res.data.ivDos;
          if (res.data.tpId != null) this.claimRcm.tpId = res.data.tpId;
          if (res.data.tpDos != null) this.claimRcm.tpDos = res.data.tpDos;
          if (res.data.ssn != null) this.claimRcm.ssn = res.data.ssn;
          this.reval();
        }
      }
      this.closeIvPopup();
    })
  }

  removeIVTP() {
    let params: any = null;
    if (this.deleteType === "IV") {
      params = {
        'claimUuid': this.claimUUid,
        'removeIv': true
      };
    }
    if (this.deleteType === "TP") {
      params = {
        'claimUuid': this.claimUUid,
        'removeTp': true
      };
    }
    this.deleteLoaderIVTP = true;
    this.appService.removeIVTP(params, (res: any) => {

      if (res.status) {
        this.deleteLoaderIVTP = false;
        if (res.data.success) {
          if (res.data.ivfId != null) this.claimRcm.ivfId = res.data.ivfId;
          if (res.data.ivDos != null) this.claimRcm.ivDos = res.data.ivDos;
          if (res.data.tpId != null) this.claimRcm.tpId = res.data.tpId;
          if (res.data.tpDos != null) this.claimRcm.tpDos = res.data.tpDos;
          if (res.data.ssn != null) this.claimRcm.ssn = res.data.ssn;
          this.reval();
        }
      }
      this.closeDelPopup();
    })
  }

  get isRoleLead() {
    return Utils.isRoleLead();
  }

  get isInternalAuditLogin() {
    return (Utils.selectedTeam() == 3);
  }
  get isRoleSuperAdmin() {
    return Utils.checkRoleSuperAdmin();
  }

  get defaultClient(): string {
    return Utils.getDefaultClient();
  }

  get isBilling(): boolean {
    return Utils.isBilling();
  }

  get isInternalAudit(): boolean {
    return Utils.isInternalAudit();
  }

  get timeZone(): string {
    return Utils.getTimeZone();
  }

  updateUrl(url: string) {
    if ('/billing-claims/' + url != this.router.url) this.location.go(url);
  }

  goToListofClaimsPage() {
    window.location.href = "/list-of-claims";
  }

  get isSuperAdmin() {
    return Utils.checkRoleSuperAdmin();
  }


  replaceValue(v: string, w: string): string {
    return v.replace(/,/g, w);
  }

  enableDisable300Sub(cond: boolean, srNo: number) {
    let subsection: any = document.querySelectorAll(".relatedTo_300");
    if (subsection.length > 0) {
      if (!cond) {
        this.relatedTo_300 = false;
        for (let el of subsection) el.style.display = 'none';
      } else {
        for (let el of subsection) el.style.display = '';
        this.relatedTo_300 = true;
      }
    }
  }
  downloadPdf() {
    this.loader.exportPDFLoader = true;
    let data = { "fileName": "Claim_Details", "data": [this.claimRcm], "teamId": this.selectedTeam, "clientName": this.clientName, "otherTeamsRemark": this.otherTeamRemarks, "claimRules": this.claimRules, "serviceLevelCodeManual": this.claimServiceLevelModel, "ruleEngineReport": this.ruleEngineReport, "claimSubmissionDto": this.submissionDto, "relatedTo_300": this.relatedTo_300, "countA": this.countA, "countAS": this.countAS, "count": this.count };
    this.appService.claimDetailsPdfDownload(data, "pdf", (res: any) => {
      if (res.status === 200) {
        console.log(res.body);
        this.downloadService.saveBolbData(res.body, "Claim_Details.pdf");
        this.loader.exportPDFLoader = false;
      } else {
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    })
  }

  manageProviderOnClaimFromSheet() {
    //Now Multiple values can come
    if (this.claimRcm.providerOnClaimFromSheet == null) return "";
    let ar = this.claimRcm.providerOnClaimFromSheet.split("<<<>>>");
    let temp = "";
    ar.forEach(element => {
      temp = temp + element + "<br/>";
    });

    return temp;
  }

  fetchAttachmentCount() {
    this.appService.fetchAttachmentCount(this.claimUUid, (res: any) => {
      if (res.status == 200 && res.data) {
        this.attachmentConfig['showAttachment'] = true;
        this.attachmentConfig['attachmentCount'] = res.data;
      }
    })
  }

  receiveChildEvent(event: any) {
    if (event['action'] === 'fileSelected') {
      this.setSelectedFileForComponent(event.claimUuid, event.value);
    } else if (event['action'] === 'filesSelectedToRemove') {
      this.setSelectedFileToRemove(event.claimUuid, event.value)
    } else if (event['action'] === 'clearAttachmentAndRemovedFiles') {
      this.clearAttachment();
    }
  }

  setSelectedFileForComponent(claimUuid: any, file: File) {
    this.selectedFilesMap.set(claimUuid, file);
  }

  setSelectedFileToRemove(claimUuid: any, fileParam: any) {
    this.removedFilesMap.set(claimUuid, fileParam);
  }

  getSelectedFileForComponent(claimUuid: any) {
    return this.selectedFilesMap.get(claimUuid);
  }

  getSelectedFilesToRemove(claimUuid: any) {
    return this.removedFilesMap.get(claimUuid);
  }

  // submitAttachment(callback: any) {
  //   let removedFiles = this.getSelectedFilesToRemove(this.claimUUid);
  //   if (!removedFiles) {
  //     removedFiles = [];
  //   }
  //   const selectedFiles = this.getSelectedFileForComponent(this.claimUUid);
  //   if (!Array.isArray(removedFiles)) {
  //     this.appService.removeAttachmentFile(removedFiles, (res: any) => {
  //       if (res.status == 200 && res.data.fileResponseStatus) {
  //         this.finalSubmitAttachment(selectedFiles, 0, callback);
  //       } else {
  //         this.showAlertPopup(res);
  //       }
  //     })
  //   } else {
  //     if (!selectedFiles) {
  //       return callback({ 'status': true });
  //     } else {
  //       this.finalSubmitAttachment(selectedFiles, 0, callback);
  //     }
  //   }
  // }


  // finalSubmitAttachment(dataArray: any[], currentIndex: number, callback: any) {
  //   if (currentIndex >= dataArray.length) {
  //     return callback({ 'status': true });
  //   }
  //   const currentData = dataArray[currentIndex];
  //   let formData: any = new FormData();
  //   formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.claimUUid);
  //   formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
  //   formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
  //   this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
  //     if (res.data.status) {
  //       this.finalSubmitAttachment(dataArray, currentIndex + 1, callback);
  //     } else {
  //       this.showAlertPopup(res);
  //     }
  //   })
  // }

  clearAttachment() {
    this.selectedFilesMap = new Map();
    this.removedFilesMap = new Map();
  }

  openArchivedModal() {
    this.reason = "";
    this.modelElement.modal = document.getElementById("archive-modal");
    this.modelElement.span = document.getElementsByClassName("close")[0];
    this.modelElement.modal.style.display = "block";
  }

  openUnArchivedModal() {
    this.reason = "";
    this.modelElement.modal = document.getElementById("unarchive-modal");
    this.modelElement.span = document.getElementsByClassName("close")[0];
    this.modelElement.modal.style.display = "block";
  }

  closeArchPopup(type: string) {
    let popup: any = document.getElementById(type);
    popup.style.display = 'none';

  }

  markArchived() {
    let ths = this;
    ths.actionButtons = false;
    ths.claimStatus = new ClaimUpdateStatusModel();
    ths.claimStatus.reason = ths.reason;
    ths.claimStatus.claimUuid = ths.claimUUid;
    ths.claimService.archiveclaim(ths.claimStatus, (res: any) => {
      ths.actionButtons = true;
      this.alertArch.showAlertPopup = true;
      if (res.status == 200) {

        this.alertArch.isError = false;
        if (res.data === 'Claim Archived') {
          this.alertArch.alertMsg = "Claim Archived!!";
          setTimeout(() => {
            this.alertArch.showAlertPopup = false;
            window.location.reload();
          }, 5000);

        } else {
          this.alertArch.alertMsg = res.data;
          this.alertArch.isError = true;
        }

      } else {
        this.alertArch.isError = true;
      }
    });
  }

  markUnArchived() {
    let ths = this;
    ths.actionButtons = false;
    ths.claimStatus = new ClaimUpdateStatusModel();
    ths.claimStatus.reason = ths.reason;
    ths.claimStatus.claimUuid = ths.claimUUid;
    ths.claimService.unArchiveclaim(ths.claimStatus, (res: any) => {
      ths.actionButtons = true;
      this.alertArch.showAlertPopup = true;
      if (res.status == 200) {

        this.alertArch.isError = false;
        if (res.data === 'Claim UnArchived') {
          this.alertArch.alertMsg = "Claim UnArchived!!";
          setTimeout(() => {
            this.alertArch.showAlertPopup = false;
            window.location.reload();
          }, 5000);

        } else {
          this.alertArch.alertMsg = res.data;
          this.alertArch.isError = true;
        }

      } else {
        this.alertArch.isError = true;
      }
    });
  }

  checkForValidPreferredSbmisssion(): boolean {
    let ths = this;
    if (this.overidePreferredSubmission) return true;
    //ths.claimRcm.preferredModeOfSubmission = "RemoteLite";//For testing only
    let preferredModeOfSubmission = ths.claimRcm.preferredModeOfSubmission;

    if (preferredModeOfSubmission === null) preferredModeOfSubmission = "";
    if (preferredModeOfSubmission === '') {
      return true;
    }
    if (preferredModeOfSubmission.toLowerCase() != ths.submissionDto.channel.toLowerCase())
      return false;
    return true
  }

  markValidPreffered(choice: string) {
    this.overidePreferredSubmission = false;
    if (choice === 'yes') {
      this.overidePreferredSubmission = true;
      this.saveClaim('submit', false);
    }
    this.closeArchPopup('check-preferredModeOfSubmission-modal');
  }

  openPrefferedModal() {
    this.modelElement.modal = document.getElementById("check-preferredModeOfSubmission-modal");
    this.modelElement.span = document.getElementsByClassName("close")[0];
    this.modelElement.modal.style.display = "block";
  }

  isProviderNotesNeeded(): boolean {
    //
    //this Provider Notes requirement is only for the cases that are sent to billing team and not internal audit team.
    let codedFound = false;
    /*if (this.claimRcm.firstTeamId == this.appConstants.INTERNAL_AUDIT_TEAM) {
      return true;
    }*/
    if (this.claimRcm.firstTeamId == this.appConstants.BILLING_TEAM) {
      return false;
    }
    if (this.claimServiceLevelModel == undefined) {
      return !codedFound;
    }
    let claimCodes: Array<ClaimDetailModel> = this.claimServiceLevelModel.details;
    if (claimCodes != null) {
      let rCode = [];
      claimCodes.forEach((c: ClaimDetailModel) => {
        //console.log(c.serviceCode);
        //We have to ask for Provider notes if there are one ore more service codes in the 
        //claim that are not in the list of excluded service codes that Capline sent us.
        const cd = this.noProviderNoteCodes.find(elem => elem === c.serviceCode);
        if (cd == undefined) rCode.push(c.serviceCode);

      });
      //console.log(rCode.length);
      if (rCode.length > 0) codedFound = true;
    }
    //console.log(codedFound);
    return codedFound;
  }

  isRuleEnginevalidationNeeded() {
    let req = true;
    let insType = "";
    let ths = this;
    let right = ths.checkForSectionAccess(ths.sectionIds['RULE_ENGINE_VALIDATION']['sectionId'], 'edit');

    //if (ths.ruleEngineReport.length == 0){
    //  req=false;
    //} else
    if (!right) {//this.isInternalAudit
      //For now Disable if length==0
      req = false;
    } else {
      if (ths.claimRcm.primary) {
        insType = ths.claimRcm.primaryInsType;
      } else {
        insType = ths.claimRcm.secondaryInsType;
      }
      if (insType === 'HMO') req = false;

    }
    return req;

  }


  isORMNotesNeeded(): boolean {
    let ths = this;
    if (ths.claimRcm.claimId === "") return false;
    let name: string = "";
    if (ths.claimRcm.primary) {
      name = ths.claimRcm.primInsurance;
    } else {
      name = ths.claimRcm.secInsurance;
    }
    return name.toLowerCase().includes("medicaid");
  }

  isOtherTLExist(ref: any, callback: any) {
    let params: any = {
      "claimUuid": this.claimUUid,
      "assignToTeamId": +this.claimEditModel.assignToTeam
    };
    this.appService.isOtherTeamTLExist(params, (res: any) => {
      if (res.data.responseStatus) {
        callback(res.data.responseStatus);
      }
      else {
        ref.showAlertPopup = true;
        ref.alertMsg = res.data.message;
        ref.isError = true;
        setTimeout(() => {
          ref = {};
        }, 6000);
      }
    })
  }

  ignoreSomeRules() {
    let ths = this;
    ths.noProviderNoteCodes = [];
    //We have a service level validation  named "FDH Certification" in the RCM Tool, we don't need that for below given offices because these offices are in a different state.
    //1. San Mateo
    //2. Rio Bravo
    if (ths.smilePoint && ths.claimServiceLevelModel.dto != undefined && (ths.claimRcm.officeName === 'San Mateo' || ths.claimRcm.officeName === 'Rio Bravo')) {
      //614c4beb-7df2-11e8-8432-8c16451459cd
      let tmp: Array<ServiceLevelCodeDataModel> = ths.claimServiceLevelModel.dto;
      tmp.forEach((x, i) => {
        //debugger;
        if (x.manualAuto == 'Automated' && x.ruleId == 308) {
          ths.claimServiceLevelModel.dto.splice(i, 1);
        }

      });
    }

    if (ths.smilePoint && ths.claimServiceLevelModel.dto) {
      //614c4beb-7df2-11e8-8432-8c16451459cd
      let tmp: Array<ServiceLevelCodeDataModel> = ths.claimServiceLevelModel.dto;
      tmp.forEach((x, i) => {
        if (x.manualAuto == 'Manual' && x.name == '-Provider Notes-' && x.value == 'Required') {
          ths.claimServiceLevelModel.dto.splice(i, 1);
          if (x.value == 'Required') {////NOT NEEDED NOW
            ths.noProviderNoteCodes.push(x.serviceCode);
          }
        }

      });
    }
  }



  fetchClaimLevelInfoSection() {
    this.appService.fetchClaimLevelInfoSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['CLAIM_LEVEL_INFORMATION'] = res.data;

      }
    })
  }

  fetchAppealSection() {
    this.appService.fetchAppealSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['APPEAL'] = res.data;

      }
    })
  }

  fetchInsurancePaymentInfoSection() {
    this.appService.fetchInsurancePaymentInfoSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION'] = res.data;

      }
    })
  }

  fetchEobSection() {
    if (this.checkForSectionAccess(this.sectionIds['EOB']['sectionId'], 'view')) {
      this.appService.fetchEobSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['EOB'].data = res.data;
        }
      })
    }

  }

  fetchServiceLevelInfoSection() {
    if (this.checkForSectionAccess(this.sectionIds['SERVICE_LEVEL_INFORMATION']['sectionId'], 'view')) {
      this.appService.fetchServiceLevelInfoSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data = res.data;
          if (!this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.some((e: any) => e.serviceCode == 'Undistributed')) {
            this.addUndistributedSectionLevelField();
          }
          this.getTotalServiceLevelInfo();
        }
      })
    }
  }

  saveClaimLevelinfo(isFinalSubmit: boolean) {
    this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['sectionId'] = this.sectionIds['CLAIM_LEVEL_INFORMATION']['sectionId'];
    if (!isFinalSubmit) {
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        claimInfoModel: this.claimSectionModal['CLAIM_LEVEL_INFORMATION']
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);

        }
      })
    }
    return this.claimSectionModal['CLAIM_LEVEL_INFORMATION'];

  }

  saveAppealLevelinfo(isFinalSubmit: boolean) {
    this.claimSectionModal['APPEAL']['sectionId'] = this.sectionIds['APPEAL']['sectionId'];

    if (!isFinalSubmit) {

      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        appealInfoModel: this.claimSectionModal['APPEAL']
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);

        }
      })
    }
    return this.claimSectionModal['APPEAL'];
  }

  checkSectionFieldValidation(moveToNextTeam: boolean, save: boolean, showResponseStatus: boolean) {
    let ths: any = this;
    ths.isSectionValidated = true;
    let data: any = [];
    ths.sectionLevelData[0].teamsWithSections.forEach((team: any) => {
      data = team.sectionData.filter((item: any) => item.editAccess);
    });
    for (const section of data) {
      if (section.isNewSection && section.sectionId == ths.sectionIds[section.sectionName]?.['sectionId']) {
        const methodName: any = `validate_${section.sectionName}`
        let isSectionVal: boolean = ths[methodName]();   //validation method will be called here
        //method names are creates using convention  validate_{sectioname}
        if (!isSectionVal) {
          ths.isSectionValidated = false;
        } else {
          ths.createSectionModal(section.sectionName);
        }
      }
    }
    this.claimEditModel.assignToTeam = this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeam'];
    if (this.claimEditModel.assignToTeam == -1) {
      this.alert('NO team Selected');
    }
    this.isOtherTLExist(this.finalerror, (res: any) => {
      if (res) {

        if (ths.isSectionValidated && ths.checkIfOldSectionAccessWhileFinalSave()) {
          //First Save OLD Data Then Save New data.
          let type = "";
          if (ths.claimRcm.currentTeamId === this.appConstants.BILLING_TEAM) type = "submitafterpending";
          else if (ths.claimRcm.currentTeamId === this.appConstants.INTERNAL_AUDIT_TEAM) type = "reviewedafterpendingbyinternalaudit";
          else type = "assignafterpendingnot_bill_internal";
          ths.saveClaim(type, true);

        } else {
          if (ths.isSectionValidated && save) {
            ths.finalSaveSection(moveToNextTeam, showResponseStatus);
          }
        }

      }
    });




  }


  createSectionModal(sectionName: any) {
    if (sectionName === 'CLAIM_LEVEL_INFORMATION') {
      this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['sectionId'] = this.sectionIds['CLAIM_LEVEL_INFORMATION']['sectionId'];
      this.finalSaveClaimDataModel.claimInfoModel = this.saveClaimLevelinfo(true);
    }
    else if (sectionName === 'APPEAL') {
      this.claimSectionModal['APPEAL']['sectionId'] = this.sectionIds['APPEAL']['sectionId'];
      this.finalSaveClaimDataModel.appealInfoModel = this.saveAppealLevelinfo(true);
    }
    else if (sectionName === 'INSURANCE_PAYMENT_INFORMATION') {
      this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['sectionId'] = this.sectionIds['INSURANCE_PAYMENT_INFORMATION']['sectionId'];
      this.finalSaveClaimDataModel.paymentInformationInfoModel = this.saveInsurancePaymentInfo(true);
    }
    else if (sectionName === 'EOB') {
      this.claimSectionModal['EOB']['sectionId'] = this.sectionIds['EOB']['sectionId'];
      this.finalSaveClaimDataModel.eobInfoModel = this.getPdfUrlAndSaveEOB(true);
    }
    else if (sectionName === 'SERVICE_LEVEL_INFORMATION') {
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['sectionId'] = this.sectionIds['SERVICE_LEVEL_INFORMATION']['sectionId'];
      this.finalSaveClaimDataModel.serviceLevelInformationInfoModel = this.saveServiceLevelInfo(true);
    }
    else if (sectionName === 'INSURANCE_FOLLOW_UP') {
      this.claimSectionModal['INSURANCE_FOLLOW_UP']['sectionId'] = this.sectionIds['INSURANCE_FOLLOW_UP']['sectionId'];
      this.finalSaveClaimDataModel.rcmFollowUpInsuranceInfoModel = this.saveInsuranceFollowUpInfo(true);
    }
    else if (sectionName === 'PATIENT_STATEMENT') {
      this.claimSectionModal['PATIENT_STATEMENT']['sectionId'] = this.sectionIds['PATIENT_STATEMENT']['sectionId'];
      this.finalSaveClaimDataModel.rcmPatientStatementInfoModel = this.savePatientStaementInfo(true);
    }
    else if (sectionName === 'PATIENT_PAYMENT') {
      this.claimSectionModal['PATIENT_PAYMENT']['sectionId'] = this.sectionIds['PATIENT_PAYMENT']['sectionId'];
      this.finalSaveClaimDataModel.patientPaymentInfoModel = this.savePatientPaymentInfo(true);
    }
    else if (sectionName === 'PATIENT_COMMUNICATION') {
      this.claimSectionModal['PATIENT_COMMUNICATION']['sectionId'] = this.sectionIds['PATIENT_COMMUNICATION']['sectionId'];
      this.finalSaveClaimDataModel.patientCommunicationInfoModel = this.savePatientCommunicationSection(true);
    }
    else if (sectionName === 'CURRENT_STATUS_AND_NEXT_ACTION') {
      this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'] = this.sectionIds['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'];
      this.finalSaveClaimDataModel.nextActionRequiredInfoModel = this.saveNextActionRequiredSection(true);
    }

  }

  validate_CLAIM_LEVEL_INFORMATION() {
    debugger;
    let isSectionValidated = true;
    this.emptyFields["CLAIM_LEVEL_INFORMATION"] = {};
    this.emptyFields["CLAIM_LEVEL_INFORMATION"].network = '';
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimId) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"]['claimId'] = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimProcessingDate) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimProcessingDate = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].network) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].network = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimPassFirstGo) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimPassFirstGo = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].initialDenial) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].initialDenial = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimStatusEs) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimStatusEs = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimStatusRcm) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimStatusRcm = true;
      isSectionValidated = false;
    }
    // if(this.isSectionValidated) {
    //   this.finalSaveClaimDataModel.claimInfoModel = this.claimSectionModal['CLAIM_LEVEL_INFORMATION'];
    //   return  this.isSectionValidated;
    // }

    return isSectionValidated;
  }

  validate_APPEAL() {
    let isSectionValidated = true;
    this.emptyFields["APPEAL"] = {};
    this.emptyFields["APPEAL"].network = '';
    if (!this.claimSectionModal["APPEAL"].modeOfAppeal) {
      this.emptyFields["APPEAL"]['modeOfAppeal'] = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["APPEAL"].aiToolUsed) {
      this.emptyFields["APPEAL"].aiToolUsed = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["APPEAL"].appealDocument) {
      this.emptyFields["APPEAL"].appealDocument = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["APPEAL"].remarks) {
      this.emptyFields["APPEAL"].remarks = true;
      isSectionValidated = false;
    }
    // else {
    //   this.finalSaveClaimDataModel.appealInfoModel = this.claimSectionModal['APPEAL'];
    //   return this.isSectionValidated;
    // }
    return isSectionValidated;
  }

  validate_LINKS_RELATED_DOCUMENTS() {
    return true;
  }
  validate_REMARKS_BY_OTHER() {
    return true;
  }
  validate_SECTION_CLAIM_DETAIL() {
    return true;
  }
  validate_REBILLED_CLAIM() {
    return true;
  }
  validate_CLAIM_LEVEL_VALIDATION_AUTO() {
    return true;
  }
  validate_CLAIM_LEVEL_VALIDATION_MANUAL() {
    return true;
  }
  validate_SERVICE_LEVEL_VALIDATION_AUTO() {
    return true;
  }
  validate_SERVICE_LEVEL_VALIDATION_MANUAL() {
    return true;
  }
  validate_RULE_ENGINE_VALIDATION() { return true; }
  validate_CLAIM_SUBMISSION() { return true; }
  validate_SERVICE_LEVEL_INFORMATION() { return true; }
  validate_EOB() { return true; }
  validate_INSURANCE_PAYMENT_INFORMATION() {
    return true;
  }
  validate_PATIENT_STATEMENT() {
    return true;
  }
  validate_ASSIGN_TO_OTHER() {
    return true;
  }
  validate_INSURANCE_FOLLOW_UP() {
    return true;
  }
  validate_RECREATE_CLAIM() {
    return true;
  }
  validate_PATIENT_PAYMENT() {
    return true;
  }
  validate_PATIENT_COMMUNICATION() {
    return true;
  }
  validate_COLLECTION_AGENCY() {
    return true;
  }
  validate_REQUEST_REBILLING() {
    return true;
  }
  validate_REBILLING() {
    return true;
  }
  validate_NEED_TO_CALL_INSURANCE() {
    return true;
  }
  validate_CURRENT_STATUS_AND_NEXT_ACTION() {
    return true;
  }
  validate_ATTACHMENT() {
    return true;
  }

  finalSaveSection(moveToNextTeam: boolean, showResponseStatus: boolean) {
    this.finalSaveClaimDataModel['claimUuid'] = this.claimUUid;
    this.finalSaveClaimDataModel['moveToNextTeam'] = moveToNextTeam;
    this.appService.saveClaimLevelInfoSection(this.finalSaveClaimDataModel, (res: any) => {
      if (res.status) {
        console.log(res);
        if (showResponseStatus) {

        }

      }
    })
  }

  copyUrl() {
    let inputEl = document.createElement("input");
    let browserLink = window.location.href;
    document.body.appendChild(inputEl);
    inputEl.value = browserLink;
    inputEl.select();
    document.execCommand("copy");
    document.body.removeChild(inputEl);

    let tooltip = document.getElementById("tooltip");
    tooltip.style.visibility = "visible";
    setTimeout(function () {
      tooltip.style.visibility = "hidden";
    }, 2000);
  }

  get newClaimTransferToTeam(): number {
    //TODO Write Logic- Ayush
    return 0;
  }

  saveInsurancePaymentInfo(isFinalSubmit: boolean) {
    this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['sectionId'] = this.sectionIds['INSURANCE_PAYMENT_INFORMATION']['sectionId'];
    this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['paidAmount'] = this.sectionLevelInfoTotalConfig.paidAmount > 0 ? +this.sectionLevelInfoTotalConfig.paidAmount : 0;
    this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['amountReceivedInBank'] = +this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['amountReceivedInBank'];  //converting into Number type using bitwise operator
    this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['amountPostedInEs'] = +this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['amountPostedInEs'];  //converting into Number type using bitwise operator
    this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['checkNumber'] = +this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']['checkNumber'];  //converting into Number type using bitwise operator
    if (!isFinalSubmit) {

      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        paymentInformationInfoModel: this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);

        }
      })
    }
    return this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION'];
  }


  zoom_in() {
    this.zoom_to = this.zoom_to + 0.25;
  }

  zoom_out() {
    if (this.zoom_to > 1) {
      this.zoom_to = this.zoom_to - 0.25;
    }
  }

  search() {
    this.pdfViewer.pdfViewer.eventBus.on('updatefindmatchescount', (data: any) => {
      this.stringToSearch = data.matchesCount.total;
      console.log(this.stringToSearch);

    });
  }

  getPdfUrlAndSaveEOB(isFinal: boolean) {

    this.claimSectionModal['EOB']['sectionId'] = this.sectionIds['EOB']['sectionId'];
    this.claimSectionModal['EOB']['extension'] = "pdf";
    if (!isFinal) {

      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinal,
        eobInfoModel: {
          'eobLink': this.claimSectionModal['EOB']['pdfLink'],
          'sectionId': this.claimSectionModal['EOB']['sectionId'],
          'extension': 'pdf'
        }
      };
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          this.claimSectionModal['EOB']['pdfLink'] = '';
          this.claimSectionModal['EOB'].data.push(res.data);
          this.pdfUrlSrc = res.data.eobPathLink;
        }
      })
    }
    return this.claimSectionModal['EOB']['pdfLink'] || null;

  }

  viewLink(link: any) {
    this.pdfUrlSrc = link;
  }


  fetchInsuranceFollowUpSection() {
    if (this.checkForSectionAccess(this.sectionIds['INSURANCE_FOLLOW_UP']['sectionId'], 'view')) {
      this.appService.fetchInsuranceFollowUpSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['INSURANCE_FOLLOW_UP'].data = res.data;
        }
      })
    }

  }

  fetchPatientStatementSection() {
    if (this.checkForSectionAccess(this.sectionIds['PATIENT_STATEMENT']['sectionId'], 'view')) {
      this.appService.fetchPatientStatementSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['PATIENT_STATEMENT'] = res.data;
        }
      })
    }

  }

  fetchPatientPaymentSection() {
    if (this.checkForSectionAccess(this.sectionIds['PATIENT_PAYMENT']['sectionId'], 'view')) {
      this.appService.fetchPatientPaymentSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['PATIENT_PAYMENT'] = res.data;
        }
      })
    }

  }

  fetchNextActionRequiredSection() {
    debugger;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusRcm'] = this.claimRcm.currentStatusName;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusEs'] = this.claimRcm.statusES;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['nextAction'] = this.claimRcm.nextActionName;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeam'] = -1;//this.claimRcm.assignedToTeam;

    let team: any = this.appConstants.TEAMS_CONFIG.get(this.claimRcm.assignedToTeam);

    //canWorkBeforeSubmssion ?: boolean;
    //this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeam'] = {
    // "teamId": 7, "teamName": "Billing"
    // };
    debugger;


    //if (this.checkForSectionAccess(this.sectionIds['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'], 'view')) {
    /*this.appService.fetchNextActionRequiredSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION'] = res.data;
        this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusRcm'] = this.claimRcm.currentStatusName;
        this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['CURRENT_STATUS_AND_NEXT_ACTION'] = this.claimRcm.nextActionName;
      } else {
        this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusRcm'] = this.claimRcm.currentStatusName;
        this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['CURRENT_STATUS_AND_NEXT_ACTION']= this.claimRcm.nextActionName;
      }
    })*/
    //}

  }

  fetchPatientCommunicationSection() {
    if (this.checkForSectionAccess(this.sectionIds['PATIENT_COMMUNICATION']['sectionId'], 'view')) {
      this.appService.fetchPatientCommunicationSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['PATIENT_COMMUNICATION'].data = res.data;
        }
      })
    }

  }

  removeEobById(id: any, indx: any) {
    let params: any = {
      "claimUuid": this.claimUUid,
      "ids": [id]
    };
    this.appService.removeEobData(params, (res: any) => {
      if (res) {
        console.log(res);
        this.claimSectionModal['EOB'].data.splice(indx, 1);
      }
    }
    )
  }

  deleteAllEobLink() {
    let removeIds: any = [];
    this.claimSectionModal.data.forEach((e: any) => {
      removeIds.push(e.id);
    });
    let params: any = {
      "claimUuid": this.claimUUid,
      "ids": removeIds
    };
    this.appService.removeEobData(params, (res: any) => {
      if (res) {
        console.log(res);
      }
    }
    )
  }

  saveServiceLevelInfo(isFinal: boolean) {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['sectionId'] = this.sectionIds['SERVICE_LEVEL_INFORMATION']['sectionId'];
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalBtpAmount'] = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalAdjustmentAmount'] = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['serviceLevelBody'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data;

    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalBtpAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalBtpAmount'] + +e.billToPatientAmount;
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalAdjustmentAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalAdjustmentAmount'] + +e.adjustmentAmount;

    });

    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinal,
        serviceLevelInformationInfoModel: {
          serviceLevelTotalAmount: this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount'],
          sectionId: this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['sectionId'],
        }
      };
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);
        }
      });
    }
    return this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount'];
  }

  addNewSectionLevelField() {
    let model: any = {
      "notes": "",
      "btpReason": "",
      "adjustmentReason": "",
      "billToPatientAmount": 0,
      "adjustmentAmount": 0,
      "paidAmount": 0,
      "allowedAmount": 0,
      "serviceCode": "",
      "action": '',
      "tooth": "",
      "surface": "",
      "estPrimary": "",
      "fee": "",
      "snum": this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].length
    };

    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].splice(this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].length - 1, 0, model);

  }

  addUndistributedSectionLevelField() {
    let model: any = {
      "notes": "",
      "btpReason": "",
      "adjustmentReason": "",
      "billToPatientAmount": 0,
      "adjustmentAmount": 0,
      "paidAmount": 0,
      "allowedAmount": 0,
      "serviceCode": "Undistributed",
      "action": '',
      "tooth": "",
      "surface": "",
      "estPrimary": 0,
      "fee": 0,
      "snum": this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.length + 1
    };
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.push(model);
  }

  removeSectionLevelRow(idx: any) {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.splice(idx, 1);
  }

  viewFullNotes(notes: any) {
    this.viewNotesConfig.showNotes = true;
    this.viewNotesConfig.viewNotes = notes;
  }

  getReconcialition() {
    return "true";
  }

  getTotalServiceLevelInfo() {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      this.sectionLevelInfoTotalConfig.allowedAmount = this.sectionLevelInfoTotalConfig.allowedAmount + +e.allowedAmount;
      this.sectionLevelInfoTotalConfig.adjustmentAmount = this.sectionLevelInfoTotalConfig.adjustmentAmount + +e.adjustmentAmount;
      this.sectionLevelInfoTotalConfig.paidAmount = this.sectionLevelInfoTotalConfig.paidAmount + +e.paidAmount;
      this.sectionLevelInfoTotalConfig.estPrimary = this.sectionLevelInfoTotalConfig.estPrimary + +e.estPrimary;
      this.sectionLevelInfoTotalConfig.fee = this.sectionLevelInfoTotalConfig.fee + +e.fee;
      this.sectionLevelInfoTotalConfig.billToPatientAmount = this.sectionLevelInfoTotalConfig.billToPatientAmount + +e.billToPatientAmount;
    })
  }

  closeNoteModal() {
    this.viewNotesConfig.showNotes = false;
  }

  fetchClaimSteps(uuid: string) {
    let ths = this;
    ths.claimService.fetchClaimSteps(uuid,
      (res: any) => {
        if (res.status === 200) {
          console.log(res.data);
          ths.claimSteps = res.data;
        }
      });
  }

  saveInsuranceFollowUpInfo(isFinal: boolean) {
    this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['sectionId'] = this.sectionIds['INSURANCE_FOLLOW_UP']['sectionId'];
    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        rcmFollowUpInsuranceInfoModel: this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']
      };
      console.log(params);
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          this.claimSectionModal['INSURANCE_FOLLOW_UP'].data.push(res.data);
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal'];
  }

  savePatientStaementInfo(isFinal: boolean) {
    this.claimSectionModal['PATIENT_STATEMENT']['sectionId'] = this.sectionIds['PATIENT_STATEMENT']['sectionId'];

    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        rcmPatientStatementInfoModel: this.claimSectionModal['PATIENT_STATEMENT']
      };
      console.log(params);

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['PATIENT_STATEMENT'];
  }

  savePatientPaymentInfo(isFinal: boolean) {
    this.claimSectionModal['PATIENT_PAYMENT']['sectionId'] = this.sectionIds['PATIENT_PAYMENT']['sectionId'];

    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        patientPaymentInfoModel: this.claimSectionModal['PATIENT_PAYMENT']
      };

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['PATIENT_PAYMENT'];

  }

  saveNextActionRequiredSection(isFinal: boolean) {
    this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'] = this.sectionIds['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'];
    if (!isFinal) {

      let params: any = {
        claimUuid: this.claimUUid,
        nextActionRequiredInfoModel: this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']
      };

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION'];
  }

  savePatientCommunicationSection(isFinal: boolean) {
    this.claimSectionModal['PATIENT_COMMUNICATION']['modal']['sectionId'] = this.sectionIds['PATIENT_COMMUNICATION']['sectionId'];
    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        patientCommunicationInfoModel: this.claimSectionModal['PATIENT_COMMUNICATION']['modal']
      };
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          this.claimSectionModal['PATIENT_COMMUNICATION'].data.push(res.data);
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['PATIENT_COMMUNICATION'];
  }

  selectStatementBox(buttonType: any) {
    this.claimSectionModal.PATIENT_STATEMENT['buttonType'] = buttonType;
    this.clearExisitingPatientStatmentValues(buttonType);
  }

  clearExisitingPatientStatmentValues(buttonType: any) {
    this.claimSectionModal.PATIENT_STATEMENT = {
      "modeOfStatement": "",
      "reason": "",
      "statementType": "",
      "status": "",
      "amountStatement": 0,
      "nextReviewDate": "",
      "nextStatementDate": "",
      "statementSendingDate": "",
      "remarks": "",
      "statementNotes": 0,
      "balanceSheetLink": "",
      "buttonType": buttonType
    }
  }

  clearCheckDeliverOtherThanModeCheck(){
    this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['paymentMode'] != 'Check' ? this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkDeliverTo'] = '' : '';
  }

  checkIfOldSectionAccessWhileFinalSave(): boolean {

    let ths: any = this;
    let isOldSectionNeeded = false;
    let data: any = [];
    ths.sectionLevelData[0].teamsWithSections.forEach((team: any) => {
      data = team.sectionData.filter((item: any) => item.editAccess);
    });

    for (const section of data) {
      if (section.isNewSection == false && section.sectionId == ths.sectionIds[section.sectionName]?.['sectionId']) {
        isOldSectionNeeded = true;
      }
    }
    return isOldSectionNeeded;
  }

}
