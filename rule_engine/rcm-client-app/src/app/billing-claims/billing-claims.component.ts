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
import { PmlDatePicker } from '../shared/date-picker/datepicker-options';

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
  loader: any = { claimDetail: false, linkToRelatedDoc: false, remarksByOther: false, rebilledClaims: false, automatedValidation: false, manualValidation: false, ruleEngValid: false, serviceCode: false, claimSubmission: false, EOB: false, claimLevelInfo: false, insuranceFollowUpInfo: false, patientCommunicationSection: false, patientStaementInfo: false, patientPaymentInfo: false, collectionAgencyInfo: false, appealLevelinfo: false, serviceLevelInfo: false, insurancePaymentInfo: false }
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
  displayPhaseSection: boolean = false;
  finalsubmitcurrentstat: boolean = true;
  activeServiceCodeCount: number = 0;
  rebilledServiceCodeCount: number = 0;
  isPdfError: boolean = false;
  isNextFollowUpRequired: boolean = false;
  checkDeliveredTo = false;
  /*readonly noProviderNoteCodes: Array<string> = ["D0120", "D0145", "D0150", "D0140", "D0160", "D0170", "D0220", "D0230",
    "D0272", "D0274", "D0210", "D0350", "D1110", "D1120", "D1206", "D1208",
    "D0330", "D0601", "D0602", "D0603", "D1330", "D1351", "D1352", "D2330",
    "D2331", "D2332", "D2335", "D2391", "D2392", "D2393", "D2394", "D0431",
    "D2140", "D2150", "D2160", "D2161"];*/
  sectionIds: any = {
    'SECTION_CLAIM_DETAIL': {
      sectionId: 1,
      isNewSection: false,
      isWorkDone: true
    },
    'LINKS_RELATED_DOCUMENTS': {
      sectionId: 2,
      isNewSection: false,
      isWorkDone: true
    },
    'REMARKS_BY_OTHER': {
      sectionId: 3,
      isNewSection: false,
      isWorkDone: true
    },
    'REBILLED_CLAIM': {
      sectionId: 4,
      isNewSection: false,
      isWorkDone: true
    },
    'CLAIM_LEVEL_VALIDATION_AUTO': {
      sectionId: 5,
      isNewSection: false,
      isWorkDone: true
    },
    'CLAIM_LEVEL_VALIDATION_MANUAL': {
      sectionId: 6,
      isNewSection: false,
      isWorkDone: true
    },
    'SERVICE_LEVEL_VALIDATION_AUTO': {
      sectionId: 7,
      isNewSection: false,
      isWorkDone: true
    },
    'SERVICE_LEVEL_VALIDATION_MANUAL': {
      sectionId: 8,
      isNewSection: false,
      isWorkDone: true
    },
    'RULE_ENGINE_VALIDATION': {
      sectionId: 9,
      isNewSection: false,
      isWorkDone: true
    }, 'CLAIM_SUBMISSION': {
      sectionId: 10,
      isNewSection: false,
      isWorkDone: true
    },
    'SERVICE_LEVEL_INFORMATION': {
      sectionId: 11,
      isNewSection: true,
      isWorkDone: true
    },
    'EOB': {
      sectionId: 12,
      isNewSection: true,
      isWorkDone: true
    },
    'CLAIM_LEVEL_INFORMATION': {
      sectionId: 13,
      isNewSection: true,
      isWorkDone: true
    },
    'INSURANCE_PAYMENT_INFORMATION': {
      sectionId: 14,
      isNewSection: true,
      isWorkDone: true
    },
    'PATIENT_STATEMENT': {
      sectionId: 15,
      isNewSection: true,
      isWorkDone: true
    },
    'ASSIGN_TO_OTHER': {
      sectionId: 16,
      isNewSection: false,
      isWorkDone: true
    },
    'INSURANCE_FOLLOW_UP': {
      sectionId: 17,
      isNewSection: true,
      isWorkDone: true
    },
    'RECREATE_CLAIM': {
      sectionId: 18,
      isNewSection: true,
      isWorkDone: false
    },
    'APPEAL': {
      sectionId: 19,
      isNewSection: true,
      isWorkDone: true
    },
    'PATIENT_PAYMENT': {
      sectionId: 20,
      isNewSection: true,
      isWorkDone: true
    },
    'PATIENT_COMMUNICATION': {
      sectionId: 21,
      isNewSection: true,
      isWorkDone: true
    },
    'COLLECTION_AGENCY': {
      sectionId: 22,
      isNewSection: true,
      isWorkDone: true
    },
    'REQUEST_REBILLING': {
      sectionId: 23,
      isNewSection: true,
      isWorkDone: true
    },
    'REBILLING': {
      sectionId: 24,
      isNewSection: true,
      isWorkDone: true
    },
    'NEED_TO_CALL_INSURANCE': {
      sectionId: 25,
      isNewSection: true,
      isWorkDone: false
    },
    'CURRENT_STATUS_AND_NEXT_ACTION': {
      sectionId: 26,
      isNewSection: true,
      isWorkDone: true
    },
    'ATTACHMENT': {
      sectionId: 27,
      isNewSection: false,
      isWorkDone: true
    }
  };

  toggleTab: any = {};
  toggleSideBar: boolean = false;

  claimSectionModal: any = {

    CLAIM_LEVEL_INFORMATION: {
      noOfEstPayment: '',
      paymentFrequency: '',
      noOfPaymentReceived: '',
      claimPassFirstGo: '',
      initialDenial: '',
      network: '',
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
      modal: { "nextFollowUpRequired": "", 'currentClaimStatus': "", 'modeOfFollowUp': "" }
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
    PATIENT_PAYMENT: {
      modeOfPayment: '',
      postedInPMS: ''
    },
    PATIENT_COMMUNICATION: {
      data: [],
      modal: {
        modeOfFollowUp: '',
        desposition: ''
      }
    },
    CURRENT_STATUS_AND_NEXT_ACTION: {
      "currentClaimStatusEs": "",

    },
    REQUEST_REBILLING: {
      "nextAction": "Re-billing",
      "currentAction": "Re-billing",
      "teamId": 7,
      "reasonForRebilling": ''
    },
    REBILLING: {
      data: [],
      modal: {
        rebillingStatus: true,
        usedAI: ''
      },
      dataModal: {}
    },
    RECREATE_CLAIM: {
      validationData: [],
      attachSecondaryCreationValidationData: [],
      modal: {
        currentClaimUuid: '',
        newClaimId: null,
        buttonType: null,
        selectedServiceCodes: [],
        serviceCodesServiceLevel: [],
        secondaryValid: false,
        reasonRecreation: null
      },
      dataModal: {},
      claimFromSheet: {
        secondaryBlledAmount: '',
        secondaryClaimSubmissionDate: '',
        primaryPaid: '',
        claimTypeS: 'Billing',
        providerIdReport: '',
        secondaryEstAmount: '',
        secondaryInsuranceCompany: '',
        secondaryInsuranceContactNo: '',
        secondaryInsuranceName: '',
        secondaryMemberId: '',
        secondaryInsuranceAddress: '',
        secondaryGroupNumber: '',
        secondaryPolicyHolder: '',
        secondaryPolicyHolderDob: ''//,
        //claimId: '',
      },
      emptyClaimFromSheet: {}
    },
    COLLECTION_AGENCY: {
      buttonType: 1,
      modeOfPayment: '',
      collectionType: '',
      reason: '',
    }

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
  sectionLevelInfoTotalConfig: any = { allowedAmount: 0, paidAmount: 0, adjustmentAmount: 0, billToPatientAmount: 0, estPrimary: 0, fee: 0, balanceFromEsBeforePosting: 0, balanceFromEsAfterPosting: 0, isCorrectTotalPaidAmt: true, isCorrectTotalAllowedAmt: true, reconcile: false, creditAdjustmentAmount: 0, debitAdjustmentAmount: 0 };
  viewNotesConfig: any = { showNotes: false, viewNotes: [] };
  isBtpFlagTrue: boolean = false;
  serviceLevelSectionMultiSelectConfig: any = { serviceCodesList: [], rebillingRequirements: [], showModal: false };
  hideRecreateButton: boolean = false;
  selectedServiceCodesExist: boolean = false;
  emailUrl: any = '';
  hideSideBarDom: boolean = false;
  @ViewChild(PdfViewerComponent, { static: false }) private pdfViewer!: PdfViewerComponent;
  isLoggedInAdmin: boolean = false;
  isLoggedInReporting: boolean = false;
  isValidInput: boolean = false;
  validateRecreateClaimErrMsg = '';


  constructor(public appService: ApplicationServiceService, public appConstants: AppConstants,
    private claimService: ClaimService,
    private route: ActivatedRoute, private title: Title, private location: Location, private router: Router, private downloadService: DownLoadService,
    public datepipe: DatePipe, public pmlDatePicker: PmlDatePicker) {
    this.claimRcm = { claimId: "" };
    title.setTitle(Utils.defaultTitle + "Claim Detail");
  }

  ngOnInit(): void {
    this.smilePoint = Utils.isSmilePoint();
    this.selectedTeam = Utils.selectedTeam();
    this.isLoggedInAdmin = Utils.checkAdminLoginRole();
    this.isLoggedInReporting = Utils.checkReportingRole();
    this.clientName = localStorage.getItem("selected_clientName");
    this.route.paramMap.subscribe(params => {
      this.claimUUid = params.get('uuid') || '';
      if (!this.isLoggedInAdmin && !this.isLoggedInReporting) this.fetchClaimRights(this.claimUUid);
    });
    this.emailUrl = window.location.href;
    this.hideSideBarDom = true;
    this.toggleSideBar = true;

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
          // this.claimRcm.patientContactNo=this.claimRcm.patientContactNo+',-NO-DATA-'+',2356786545'
          if (this.claimRcm.patientContactNo) {
            this.claimRcm.patientContactNo = this.claimRcm.patientContactNo.split(",").join(", ");
          }
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
      this.fetchRebillingInfoSection();
      this.fetchRequestRebillingSection();
      this.fetchCollectionAgencySection();
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
    ths.claimEditModel.actionName = "";
    ths.claimEditModel.ruleEngineRunRemark = ths.claimRcm.ruleEngineRunRemark;
    ths.claimEditModel.claimNoteDtoList = ths.claimRcm.claimNotes;
    ths.claimEditModel.claimRemark = ths.claimRcm.claimRemarks;
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
      ths.claimEditModel.actionName = "Submitted";
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
      ths.claimEditModel.actionName = "Assign To Team";
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
      ths.claimEditModel.actionName = "Reviewed";
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
      ths.claimEditModel.actionName = "Assign To TL";
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
      } else {
        ths.finalsubmitcurrentstat = true;
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
      } else {
        ths.finalsubmitcurrentstat = true;
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
    //debugger;
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
      if (x.ruleId == 300 && !ths.isProviderNotesNeededManual) {
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
        let SUB_DET_DT_1: any = document.getElementById("SUB_DET_DT").querySelectorAll(".pml_dp")[0];
        ths.addErrorDisplay(SUB_DET_DT_1);
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
        // let SUB_DET_DT: any = document.getElementById("SUB_DET_DT");
        // debugger;
        // if (SUB_DET_DT.value.trim() === '') {
        //   ths.addErrorDisplay(document.getElementById("SUB_DET_DT"));
        //   valid = false;
        // }
        let SUB_DET_DT_1: any = document.getElementById("SUB_DET_DT").querySelectorAll(".pml_dp")[0];

        let SUB_DET_DT: any = SUB_DET_DT_1.value;
        if (SUB_DET_DT.trim() === '') {
          ths.addErrorDisplay(SUB_DET_DT_1)
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
        } else {
          ths.submissionDto.esDate = this.convertStringToDateForDatePicker(ths.submissionDto.esDate);
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
          //debugger;
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
          if (res.data.insuranceContact) {
            this.claimRcm.insuranceContactNo = res.data.insuranceContact.split(",").join(", ");
          }
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

  get isCDP(): boolean {
    return Utils.isCDP();
  }

  get timeZone(): string {
    return Utils.getTimeZone();
  }

  updateUrl(url: string) {
    if ('/billing-claims/' + url != this.router.url) this.location.go(url);
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

  isProviderNotesNeededManual(): boolean {
    //
    //this Provider Notes requirement is only for the cases that are sent to billing team and not internal audit team.
    let codedFound = false;
    /*if (this.claimRcm.firstTeamId == this.appConstants.INTERNAL_AUDIT_TEAM) {
      return true;
    }*/
    if (this.claimRcm.firstTeamId == AppConstants.BILLING_TEAM) {
      return true;
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
    //codedFound = true;
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
        // if (this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate'] !== null) {
        //   console.log(this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate'])
        //   this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate']);
        // }
        if (this.claimSectionModal['CLAIM_LEVEL_INFORMATION'] !== null) {
          this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['claimProcessingDate']);
        }
      }
    })
  }

  // convertStringToDateForDatePicker(dateString: string) {
  //   if (dateString !== null || (dateString !== null && dateString !== '') ){
  //     return new Date(dateString);
  //   }
  // }

  convertStringToDateForDatePicker(dateString: string) {
    if (dateString !== null && dateString !== '') {
      return new Date(dateString);
    }
  }

  // convertStringToDateForApiCall(dateString: string) {
  //   if (dateString !== null || (dateString !== null && dateString !== '')) {
  //     return this.datepipe.transform(dateString, 'yyyy-MM-dd');
  //   }
  // }

  convertStringToDateForApiCall(dateString: string) {
    if (dateString !== null && dateString !== '') {
      return this.datepipe.transform(dateString, 'yyyy-MM-dd');
    }
  }



  fetchAppealSection() {
    this.appService.fetchAppealSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['APPEAL'] = res.data;
        if (!res.data.modeOfAppeal) {
          this.claimSectionModal['APPEAL']['modeOfAppeal'] = '';
        } if (!res.data.aiToolUsed) {
          this.claimSectionModal['APPEAL']['aiToolUsed'] = '';
        }

      }
    })
  }

  fetchInsurancePaymentInfoSection() {
    this.appService.fetchInsurancePaymentInfoSection(this.claimUUid, (res: any) => {
      if (res && res.data) {
        this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION'] = res.data;
        // console.log(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkCashDate'])
        if (this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION'] !== null) {
          this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkCashDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkCashDate']);
          this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['amountDateReceivedInBank'] = this.convertStringToDateForDatePicker(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['amountDateReceivedInBank']);
        }
        // console.log(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkCashDate'])
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


          if (res.data != null && res.data.length > 0) {
            this.sectionLevelInfoTotalConfig.balanceFromEsBeforePosting = res.data[0].balanceFromEsBeforePosting;
            this.sectionLevelInfoTotalConfig.balanceFromEsAfterPosting = Number(res.data[0].balanceFromEsAfterPosting);
          }
          if (!this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.some((e: any) => e.serviceCode == 'Undistributed')) {
            this.addUndistributedSectionLevelField();
          }

          this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
            if (e.surface == null || e.surface == '' || e.surface == 'NA') {
              e.surface = 'N/A';
            }
            if (e.tooth == null || e.tooth == '' || e.tooth == 'NA') {
              e.tooth = 'N/A';
            }
            if (e.fee == null || e.fee == '' || e.fee == '-1') {
              e.fee = '0';
            }
            if (e.estPrimary == null || e.estPrimary == '' || e.estPrimary == '-1') {
              e.estPrimary = '0';
            }
            if (e.serviceCode != 'Undistributed') {
              this.activeServiceCodeCount++;
              if (e.rebilledCodeStatus) {
                this.rebilledServiceCodeCount++;
              }
            }
            this.sectionLevelInfoTotalConfig.paidAmount = this.sectionLevelInfoTotalConfig.paidAmount + e.paidAmount;
            this.sectionLevelInfoTotalConfig.allowedAmount = this.sectionLevelInfoTotalConfig.allowedAmount + e.allowedAmount;
          })

          this.getTotalServiceLevelInfo();
          this.getAllServiceCodes();
        }
      })
    }
  }

  saveClaimLevelinfo(isFinalSubmit: boolean) {
    this.claimSectionModal['CLAIM_LEVEL_INFORMATION']['sectionId'] = this.sectionIds['CLAIM_LEVEL_INFORMATION']['sectionId'];
    if (!isFinalSubmit) {
      this.loader.claimLevelInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        //claimInfoModel: this.claimSectionModal['CLAIM_LEVEL_INFORMATION']
        claimInfoModel: {
          ...this.claimSectionModal['CLAIM_LEVEL_INFORMATION'],
          claimProcessingDate: this.convertStringToDateForApiCall(this.claimSectionModal.CLAIM_LEVEL_INFORMATION['claimProcessingDate'])
        }
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'CLAIM_LEVEL_INFORMATION', '1');
        this.loader.claimLevelInfo = false;
      })
    }
    return this.claimSectionModal['CLAIM_LEVEL_INFORMATION'];

  }

  saveAppealLevelinfo(isFinalSubmit: boolean) {
    this.claimSectionModal['APPEAL']['sectionId'] = this.sectionIds['APPEAL']['sectionId'];

    if (!isFinalSubmit && this.validate_APPEAL()) {
      this.loader.appealLevelinfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        appealInfoModel: this.claimSectionModal['APPEAL']
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'APPEAL', '1');
        this.loader.appealLevelinfo = false;
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
      if (section.sectionName === 'REQUEST_REBILLING') continue; //no need to add Request Rebilling in Final save
      if (ths.sectionIds[section.sectionName]?.isWorkDone && ths.sectionIds[section.sectionName]?.isNewSection && section.sectionId == ths.sectionIds[section.sectionName]?.['sectionId']) {          //replace section.isNewSection with ===> ths.sectionIds[section.sectionName]?.isNewSection
        const methodName: any = `validate_${section.sectionName}`
        let isSectionVal: boolean = ths[methodName]();   //validation method will be called here
        //method names are creates using convention  validate_{sectioname}
        console.log(!isSectionVal);
        if (methodName == 'validate_APPEAL' || methodName == 'validate_SERVICE_LEVEL_INFORMATION' || methodName == 'validate_CLAIM_LEVEL_INFORMATION'
          || methodName == 'validate_INSURANCE_PAYMENT_INFORMATION' ||
          methodName == 'validate_INSURANCE_FOLLOW_UP' || methodName == 'validate_PATIENT_STATEMENT'
          || methodName == 'validate_PATIENT_COMMUNICATION'
          || methodName == 'validate_PATIENT_PAYMENT'
          || methodName == 'validate_COLLECTION_AGENCY'
          || methodName == 'validate_REBILLING') {
          isSectionVal = true;
        }
        if (!isSectionVal) {
          ths.isSectionValidated = false;
        } else {
          ths.createSectionModal(section.sectionName);
        }
      }
    }
    if (!ths.isSectionValidated) {
      return;
    }
    ths.claimEditModel = {};
    ths.claimEditModel.assignToTeam = ths.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeamId'];
    if (this.claimEditModel.assignToTeam == -1) {
      alert('NO team Selected');
      return;
    }
    this.isOtherTLExist(this.finalerror, (res: any) => {
      if (res) {

        if (ths.isSectionValidated && ths.checkIfOldSectionAccessWhileFinalSave()) {
          //First Save OLD Data Then Save New data.
          let type = "";
          if (ths.claimRcm.currentTeamId === AppConstants.BILLING_TEAM) type = "submitafterpending";
          else if (ths.claimRcm.currentTeamId === AppConstants.INTERNAL_AUDIT_TEAM) type = "reviewedafterpendingbyinternalaudit";
          else type = "assignafterpendingnot_bill_internal";
          ths.finalsubmitcurrentstat = false;
          ths.saveClaim(type, true);

        } else {
          if (ths.isSectionValidated && save) {
            ths.finalsubmitcurrentstat = false;
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
      if (this.sectionLevelInfoTotalConfig.paidAmount == 0) {
        this.finalSaveClaimDataModel.paymentInformationInfoModel = null;
      }

    }
    else if (sectionName === 'EOB') {
      this.claimSectionModal['EOB']['sectionId'] = this.sectionIds['EOB']['sectionId'];
      this.finalSaveClaimDataModel.eobInfoModel = this.getPdfUrlAndSaveEOB(true);
    }
    else if (sectionName === 'SERVICE_LEVEL_INFORMATION') {
      this.finalSaveClaimDataModel.serviceLevelInformationInfoModel = this.saveServiceLevelInfo(true);
      this.finalSaveClaimDataModel.serviceLevelInformationInfoModel['sectionId'] = this.sectionIds['SERVICE_LEVEL_INFORMATION']['sectionId'];

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
    else if (sectionName === 'REBILLING' && this.claimRcm.rebilledStatus) {
      this.claimSectionModal['REBILLING']['sectionId'] = this.sectionIds['REBILLING']['sectionId'];
      this.finalSaveClaimDataModel.rebillingInfoModel = this.saveRebillingSection(true);
    }
    else if (sectionName === 'COLLECTION_AGENCY') {
      this.claimSectionModal['COLLECTION_AGENCY']['sectionId'] = this.sectionIds['COLLECTION_AGENCY']['sectionId'];
      this.finalSaveClaimDataModel.collectionAgencyInfoModel = this.saveCollectionAgencyInfo(true);
    }
    else if (sectionName === 'CURRENT_STATUS_AND_NEXT_ACTION') {
      this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'] = this.sectionIds['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'];
      this.finalSaveClaimDataModel.nextActionRequiredInfoModel = this.saveNextActionRequiredSection(true);
    }

  }

  validate_CLAIM_LEVEL_INFORMATION() {
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
    /*if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimStatusEs) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimStatusEs = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CLAIM_LEVEL_INFORMATION"].claimStatusRcm) {
      this.emptyFields["CLAIM_LEVEL_INFORMATION"].claimStatusRcm = true;
      isSectionValidated = false;
    }*/
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

  validate_SERVICE_LEVEL_INFORMATION() {
    let isSectionValidated = true;
    for (let i = 0; i < 1000; i++) {
      this.emptyFields["SERVICE_LEVEL_INFORMATION" + i] = {};
      this.emptyFields["SERVICE_LEVEL_INFORMATION" + i]['btpReason'] = false;
      this.emptyFields["SERVICE_LEVEL_INFORMATION" + i]['adjustmentReason'] = false;
    }

    this.claimSectionModal["SERVICE_LEVEL_INFORMATION"].data.forEach((obj: { creditAdjustmentAmount: any, debitAdjustmentAmount: any, billToPatientAmount: any, adjustmentReason: string, btpReason: string }, index: number) => {
      let heading = "SERVICE_LEVEL_INFORMATION" + index;
      console.log('obj.creditAdjustmentAmount');
      console.log(obj.creditAdjustmentAmount);
      if ((obj.creditAdjustmentAmount===null || obj.creditAdjustmentAmount === 0) && (obj.debitAdjustmentAmount ===null ||obj.debitAdjustmentAmount === 0)) {
        obj.adjustmentReason = '';
      }

      if (obj.billToPatientAmount === null || obj.billToPatientAmount === 0) {
        obj.btpReason = '';
      }

      if (((obj.creditAdjustmentAmount!=null && obj.creditAdjustmentAmount > 0) || (obj.debitAdjustmentAmount!=null && obj.debitAdjustmentAmount > 0)) &&
        (obj.adjustmentReason === null || obj.adjustmentReason ==='')) {
        this.emptyFields[heading]['adjustmentReason'] = true;
        isSectionValidated = false;
      }

      if ((obj.billToPatientAmount!=null && obj.billToPatientAmount > 0 )&& (obj.btpReason === null || obj.btpReason === '')) {
        this.emptyFields[heading]['btpReason'] = true;
        isSectionValidated = false;
      }

    });

    if (!this.sectionLevelInfoTotalConfig.isCorrectTotalPaidAmt || !this.sectionLevelInfoTotalConfig.isCorrectTotalAllowedAmt) {
      isSectionValidated = false;
    }
    if (!this.sectionLevelInfoTotalConfig.reconcile) {
      isSectionValidated = false;
    }
    return isSectionValidated;

  }
  validate_EOB() { return true; }
  validate_INSURANCE_PAYMENT_INFORMATION() {
    return true;
  }

  validate_PATIENT_STATEMENT() {
    this.emptyFields["PATIENT_STATEMENT"] = {};
    let isSectionValidated = true;
    //status is mandatory for all buttons
    if (this.claimSectionModal['PATIENT_STATEMENT']['status'] === "") {
      this.emptyFields['PATIENT_STATEMENT']['status'] = true;
      isSectionValidated = false;
    }

    //The patient statemnt cycle will stop and be highlighted in some way when the patient payment becomes equal to the BTP Amount
    if (this.claimSectionModal.PATIENT_STATEMENT['buttonType'] == 1) {
      if (this.claimSectionModal['PATIENT_STATEMENT']['reason'] === "") {
        this.emptyFields['PATIENT_STATEMENT']['reason'] = true;
        isSectionValidated = false;
      }
    } else {
      if (this.claimSectionModal['PATIENT_STATEMENT']['modeOfStatement'] === "") {
        this.emptyFields['PATIENT_STATEMENT']['modeOfStatement'] = true;
        isSectionValidated = false;
      }
    }
    return isSectionValidated;
  }

  validate_ASSIGN_TO_OTHER() {
    return true;
  }
  validate_INSURANCE_FOLLOW_UP() {
    this.emptyFields["INSURANCE_FOLLOW_UP"] = {};
    let isSectionValidated = true;
    if (this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['modeOfFollowUp'] === "") {
      this.emptyFields['INSURANCE_FOLLOW_UP']['modeOfFollowUp'] = true;
      isSectionValidated = false;
    }

    if (this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['modeOfFollowUp'] == "Call") {
      if (!this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal'].refNumber) {
        this.emptyFields['INSURANCE_FOLLOW_UP'].refNumber = true;
        isSectionValidated = false;
      }
    }
    return isSectionValidated;
  }

  validate_RECREATE_CLAIM() {
    return true;
  }
  validate_PATIENT_PAYMENT() {
    this.emptyFields["PATIENT_PAYMENT"] = {};
    let isSectionValidated = true;
    if (!this.claimSectionModal['PATIENT_PAYMENT']['modeOfPayment']) {
      this.emptyFields["PATIENT_PAYMENT"]['modeOfPayment'] = true;
      isSectionValidated = false;
    }

    return isSectionValidated;
  }
  validate_PATIENT_COMMUNICATION() {
    let isSectionValidated = true;
    this.emptyFields["PATIENT_COMMUNICATION"] = {};
    if (!this.claimSectionModal["PATIENT_COMMUNICATION"]['modal'].modeOfFollowUp) {
      this.emptyFields["PATIENT_COMMUNICATION"]['modeOfFollowUp'] = true;
      isSectionValidated = false;
    }
    return isSectionValidated;
  }
  validate_COLLECTION_AGENCY() {

    let isSectionValidated = true;
    this.emptyFields["COLLECTION_AGENCY"] = {};
    if (this.claimSectionModal["COLLECTION_AGENCY"]['buttonType'] == 1 && !this.claimSectionModal["COLLECTION_AGENCY"]['collectionType']) {
      this.emptyFields["COLLECTION_AGENCY"]['collectionType'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal["COLLECTION_AGENCY"]['buttonType'] == 2 && !this.claimSectionModal["COLLECTION_AGENCY"]['modeOfPayment']) {
      this.emptyFields["COLLECTION_AGENCY"]['modeOfPayment'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal["COLLECTION_AGENCY"]['buttonType'] == 3 && !this.claimSectionModal["COLLECTION_AGENCY"]['reason']) {
      this.emptyFields["COLLECTION_AGENCY"]['reason'] = true;
      isSectionValidated = false;
    }

    return isSectionValidated;
  }
  validate_REQUEST_REBILLING() {
    let isSectionValidated = true;
    this.emptyFields["REQUEST_REBILLING"] = {};

    if (!this.claimRcm.rebilledStatus) {


      if (!this.claimSectionModal.REQUEST_REBILLING['reasonForRebilling'] || this.claimSectionModal.REQUEST_REBILLING['reasonForRebilling'] == '-1') {
        this.emptyFields["REQUEST_REBILLING"]['reasonForRebilling'] = true;
        isSectionValidated = false;
      }
      if (!this.claimSectionModal.REQUEST_REBILLING['remarks']) {
        this.emptyFields["REQUEST_REBILLING"]['remarks'] = true;
        isSectionValidated = false;
      }
      //not mandatory for now
      // if (!this.claimSectionModal.REQUEST_REBILLING['rebillingRequirements'] || this.claimSectionModal.REQUEST_REBILLING['rebillingRequirements']?.length == 0) {
      //   this.emptyFields["REQUEST_REBILLING"]['rebillingRequirements'] = true;
      //   isSectionValidated = false;
      // }

      if (this.claimSectionModal.REQUEST_REBILLING['rebillingType'] == 'partialClaim' && (!this.claimSectionModal.REQUEST_REBILLING['rebillingServiceCodes'] || this.claimSectionModal.REQUEST_REBILLING['rebillingServiceCodes']?.length == 0)) {
        this.emptyFields["REQUEST_REBILLING"]['rebillingServiceCodes'] = true;
        isSectionValidated = false;
      }

      if (!this.claimSectionModal.REQUEST_REBILLING['rebillingType']) {
        this.emptyFields["REQUEST_REBILLING"]['rebillingType'] = true;
        isSectionValidated = false;
      }
    }


    return isSectionValidated;
  }
  validate_REBILLING() {
    let isSectionValidated = true;
    this.emptyFields["REBILLING"] = {};
    if (this.claimRcm.rebilledStatus) {
      if (!this.claimSectionModal["REBILLING"]['modal'].rebillingRemarks) {
        this.emptyFields["REBILLING"]['rebillingRemarks'] = true;
        isSectionValidated = false;
      }
    }
    return isSectionValidated;
  }
  validate_NEED_TO_CALL_INSURANCE() {
    return true;
  }
  validate_CURRENT_STATUS_AND_NEXT_ACTION() {
    //debugger;
    let isSectionValidated = true;
    this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"] = {};
    if (this.claimSectionModal["CURRENT_STATUS_AND_NEXT_ACTION"].assignToTeamId == -1) {
      this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"]['assignToTeamId'] = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CURRENT_STATUS_AND_NEXT_ACTION"].nextAction) {
      this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"].nextAction = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CURRENT_STATUS_AND_NEXT_ACTION"].remarks) {
      this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"].remarks = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal["CURRENT_STATUS_AND_NEXT_ACTION"].currentClaimStatusRcm) {
      this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"].currentClaimStatusRcm = true;
      isSectionValidated = false;
    }

    if (!this.claimSectionModal["CURRENT_STATUS_AND_NEXT_ACTION"].currentClaimStatusEs) {
      this.emptyFields["CURRENT_STATUS_AND_NEXT_ACTION"].currentClaimStatusEs = true;
      isSectionValidated = false;
    }

    return isSectionValidated;
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
        this.finalsubmitcurrentstat = true;
        if (showResponseStatus) {
          window.location.reload();
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
      this.loader.insurancePaymentInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinalSubmit,
        // paymentInformationInfoModel: this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION']
        paymentInformationInfoModel: {
          ...this.claimSectionModal['INSURANCE_PAYMENT_INFORMATION'],
          checkCashDate: this.convertStringToDateForApiCall(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['checkCashDate']),
          amountDateReceivedInBank: this.convertStringToDateForApiCall(this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['amountDateReceivedInBank'])
        }
      }
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'INSURANCE_PAYMENT_INFORMATION', '1');
        this.loader.insurancePaymentInfo = false;
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
    this.claimSectionModal['EOB']['errorMessage'] = '';
    this.claimSectionModal['EOB']['sectionId'] = this.sectionIds['EOB']['sectionId'];
    this.claimSectionModal['EOB']['extension'] = "pdf";
    if (!isFinal) {

      this.loader.EOB = true;
      let params: any = {
        claimUuid: this.claimUUid,
        finalSubmit: isFinal,
        eobInfoModel: {
          'eobLink': this.claimSectionModal['EOB']['pdfLink'],
          'sectionId': this.claimSectionModal['EOB']['sectionId'],
          'extension': 'pdf'
        }
      };
      let matchFound = false;
      for (const e of this.claimSectionModal['EOB'].data) {
        if (e.eobLink == params.eobInfoModel.eobLink) {
          matchFound = true;
        }
      }

      if (!matchFound) {
        this.appService.saveClaimLevelInfoSection(params, (res: any) => {
          this.showAlert(res, 'EOB', '2');
          this.loader.EOB = false;
        })
      } else {
        this.claimSectionModal['EOB']['errorMessage'] = 'Duplicate Link.';
        this.claimSectionModal['EOB']['rsType'] = '2';
        setTimeout(() => {
          this.claimSectionModal['EOB']['errorMessage'] = '';
        }, 2000);

        this.loader.EOB = false;
      }
    }
    //return this.claimSectionModal['EOB']['pdfLink'] || null;

  }

  viewLink(link: any) {
    this.pdfUrlSrc = link;
    // this.isPdfError=true;
    window.open(this.pdfUrlSrc, '_blank');
  }


  fetchInsuranceFollowUpSection() {
    if (this.checkForSectionAccess(this.sectionIds['INSURANCE_FOLLOW_UP']['sectionId'], 'view')) {
      this.appService.fetchInsuranceFollowUpSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['INSURANCE_FOLLOW_UP'].data = res.data;
          this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['nextFollowUpDate'] = this.claimRcm.nextFollowUpDate;
          if (this.claimSectionModal['INSURANCE_FOLLOW_UP'] !== null) {
            this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['nextFollowUpDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['nextFollowUpDate']);
          }
        }
      })
    }

  }

  fetchPatientStatementSection() {
    if (this.checkForSectionAccess(this.sectionIds['PATIENT_STATEMENT']['sectionId'], 'view')) {
      this.appService.fetchPatientStatementSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['PATIENT_STATEMENT'] = res.data;
          if (this.claimSectionModal['PATIENT_STATEMENT'] !== null) {
            this.claimSectionModal['PATIENT_STATEMENT']['nextReviewDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['PATIENT_STATEMENT']['nextReviewDate']);
            this.claimSectionModal['PATIENT_STATEMENT']['statementSendingDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['PATIENT_STATEMENT']['statementSendingDate']);
            this.claimSectionModal['PATIENT_STATEMENT']['nextStatementDate'] = this.convertStringToDateForDatePicker(this.claimSectionModal['PATIENT_STATEMENT']['nextStatementDate']);
          }
          if (!res.data.modeOfStatement) {
            this.claimSectionModal['PATIENT_STATEMENT']['modeOfStatement'] = '';
          } if (!res.data.reason) {
            this.claimSectionModal['PATIENT_STATEMENT']['reason'] = '';
          }
        }
      })
    }

  }

  fetchPatientPaymentSection() {
    if (this.checkForSectionAccess(this.sectionIds['PATIENT_PAYMENT']['sectionId'], 'view')) {
      this.appService.fetchPatientPaymentSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['PATIENT_PAYMENT'] = res.data;
          if (this.claimSectionModal['PATIENT_PAYMENT'] !== null) {
            this.claimSectionModal['PATIENT_PAYMENT']['dateOfPayment'] = this.convertStringToDateForDatePicker(this.claimSectionModal['PATIENT_PAYMENT']['dateOfPayment']);
          }
          if (!res.data.modeOfPayment) {
            this.claimSectionModal['PATIENT_PAYMENT']['modeOfPayment'] = '';
          } if (!res.data.postedInPMS) {
            this.claimSectionModal['PATIENT_PAYMENT']['postedInPMS'] = '';
          }
        }
      })
    }

  }

  fetchNextActionRequiredSection() {
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusRcm'] = ""; //this.claimRcm.currentStatusName;
    if (this.claimRcm.primary && !(this.claimRcm.statusESUpdated === 'Closed' || this.claimRcm.statusESUpdated === 'Unbilled' || this.claimRcm.statusESUpdated === 'Billed' || this.claimRcm.statusESUpdated === 'Open')) {
      this.claimRcm.statusESUpdated = "";
    }
    if (!this.claimRcm.primary && !(this.claimRcm.statusESUpdated === 'Unbilled' || this.claimRcm.statusESUpdated === 'Billed')) {
      this.claimRcm.statusESUpdated = "";
    }
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['currentClaimStatusEs'] = this.claimRcm.statusESUpdated;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['nextAction'] = "";//this.claimRcm.nextActionName;
    this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeamId'] = -1;//this.claimRcm.assignedToTeam;

    let team: any = this.appConstants.TEAMS_CONFIG.get(this.claimRcm.assignedToTeam);

    //canWorkBeforeSubmssion ?: boolean;
    //this.claimSectionModal.CURRENT_STATUS_AND_NEXT_ACTION['assignToTeam'] = {
    // "teamId": 7, "teamName": "Billing"
    // };


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
          if (!res.data.modeOfFollowUp) {
            this.claimSectionModal['PATIENT_COMMUNICATION']['modeOfFollowUp'] = '';
          }
        }
      })
    }

  }

  fetchRequestRebillingSection() {
    if (this.checkForSectionAccess(this.sectionIds['REQUEST_REBILLING']['sectionId'], 'view')) {
      this.appService.fetchRequestRebillingSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          let serviceCodesForMultiSelect: any = [];
          let requirementsForMultiSelect: any = [];
          this.claimSectionModal['REBILLING']['modal'] = res.data;
          this.claimSectionModal['REBILLING']['modal']['rebillingStatus'] = this.claimRcm.rebilledStatus;
          res.data.originalServiceCodes.forEach((e: any) => {
            serviceCodesForMultiSelect.push({ name: e, checked: false })
          });
          res.data.originalRequirements.forEach((e: any) => {
            requirementsForMultiSelect.push({ name: e, checked: false })
          });
          this.claimSectionModal['REBILLING']['modal']['serviceCodesForMultiSelect'] = serviceCodesForMultiSelect;
          this.claimSectionModal['REBILLING']['modal']['requirementsForMultiSelect'] = requirementsForMultiSelect;
        }
      })
    }

  }

  fetchRebillingInfoSection() {
    if (this.checkForSectionAccess(this.sectionIds['REBILLING']['sectionId'], 'view') && this.claimRcm.rebilledStatus) {
      this.appService.fetchRebillingInfoSection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['REBILLING']['data'] = res.data;
        }
      })
    }
  }

  fetchCollectionAgencySection() {
    if (this.checkForSectionAccess(this.sectionIds['COLLECTION_AGENCY']['sectionId'], 'view')) {
      this.appService.fetchCollectionAgencySection(this.claimUUid, (res: any) => {
        if (res && res.data) {
          this.claimSectionModal['COLLECTION_AGENCY'] = res.data;
          if (!res.data.modeOfPayment) {
            this.claimSectionModal['COLLECTION_AGENCY']['modeOfPayment'] = '';
          } if (!res.data.collectionType) {
            this.claimSectionModal['COLLECTION_AGENCY']['collectionType'] = '';
          } if (!res.data.reason) {
            this.claimSectionModal['COLLECTION_AGENCY']['reason'] = '';
          }
          console.log(res.data);

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
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalPaidAmount'] = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalCreditAdjustmentAmount'] = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalDebitAdjustmentAmount'] = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['balanceFromEsBeforePosting'] = Number(this.sectionLevelInfoTotalConfig.balanceFromEsBeforePosting);
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['balanceFromEsAfterPosting'] = Number(this.sectionLevelInfoTotalConfig.balanceFromEsAfterPosting);
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['serviceLevelBody'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data;

    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalBtpAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalBtpAmount'] + +e.billToPatientAmount;
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalAdjustmentAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalAdjustmentAmount'] + +e.adjustmentAmount;
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalPaidAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalPaidAmount'] + +e.paidAmount;
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalCreditAdjustmentAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalCreditAdjustmentAmount'] + +e.creditAdjustmentAmount;
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalDebitAdjustmentAmount'] = this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount']['totalDebitAdjustmentAmount'] + +e.debitAdjustmentAmount;

    });
    this.updatBalanceFromESBeforePosting();

    if (!isFinal) {
      if (this.validate_SERVICE_LEVEL_INFORMATION()) {
        this.loader.serviceLevelInfo = true;
        let params: any = {
          claimUuid: this.claimUUid,
          finalSubmit: isFinal,
          serviceLevelInformationInfoModel: {
            serviceLevelTotalAmount: this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount'],
            sectionId: this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['sectionId'],
          }
        };
        this.appService.saveClaimLevelInfoSection(params, (res: any) => {
          this.showAlert(res, 'SERVICE_LEVEL_INFORMATION', '1');
          this.loader.serviceLevelInfo = false;
        });
      }
    }
    return { serviceLevelTotalAmount: this.claimSectionModal['SERVICE_LEVEL_INFORMATION']['serviceLevelTotalAmount'] };
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
      "snum": this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.length
    };

    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.splice(this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.length - 1, 0, model);

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
      "creditAdjustmentAmount": 0,
      "debitAdjustmentAmount": 0,
      "snum": this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.length + 1
    };
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.push(model);
  }

  removeSectionLevelRow(idx: any) {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.splice(idx, 1);
    this.getTotalServiceLevelInfo();
  }

  viewFullNotes(notes: any) {
    this.viewNotesConfig.showNotes = true;
    this.viewNotesConfig.viewNotes = notes;
  }

  updatBalanceFromESBeforePosting() {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      e.balanceFromEsBeforePosting = Number(this.sectionLevelInfoTotalConfig.balanceFromEsBeforePosting);
      e.balanceFromEsAfterPosting = Number(this.sectionLevelInfoTotalConfig.balanceFromEsAfterPosting);
    });
    this.checkReconcileLogic();
  }

  getTotalServiceLevelInfo() {
    this.clearTotalValues();
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      // this.sectionLevelInfoTotalConfig.allowedAmount = this.sectionLevelInfoTotalConfig.allowedAmount + +e.allowedAmount;
      this.sectionLevelInfoTotalConfig.adjustmentAmount = this.sectionLevelInfoTotalConfig.adjustmentAmount + +e.adjustmentAmount;
      // this.sectionLevelInfoTotalConfig.paidAmount = this.sectionLevelInfoTotalConfig.paidAmount + +e.paidAmount;
      this.sectionLevelInfoTotalConfig.estPrimary = this.sectionLevelInfoTotalConfig.estPrimary + +e.estPrimary;
      this.sectionLevelInfoTotalConfig.fee = this.sectionLevelInfoTotalConfig.fee + +e.fee;
      this.sectionLevelInfoTotalConfig.billToPatientAmount = this.sectionLevelInfoTotalConfig.billToPatientAmount + +e.billToPatientAmount;
      this.sectionLevelInfoTotalConfig.creditAdjustmentAmount = this.sectionLevelInfoTotalConfig.creditAdjustmentAmount + +e.creditAdjustmentAmount;
      this.sectionLevelInfoTotalConfig.debitAdjustmentAmount = this.sectionLevelInfoTotalConfig.debitAdjustmentAmount + +e.debitAdjustmentAmount;
    });
    this.sectionLevelInfoTotalConfig.billToPatientAmount > 0 ? this.isBtpFlagTrue = true : this.isBtpFlagTrue = false;
    this.addDecimalInTotalServiceValue();
    this.getTotalPaidAmt();
    this.getTotalAllowedAmt();
    this.checkReconcileLogic();
    // this.checkReconcileLogic();
  }

  getTotalPaidAmt() {
    let totalpaid = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      totalpaid = totalpaid + e.paidAmount;
    });
    this.sectionLevelInfoTotalConfig.isCorrectTotalPaidAmt = totalpaid == this.sectionLevelInfoTotalConfig.paidAmount ? true : false;

  }

  getTotalAllowedAmt() {
    let totalpaid = 0;
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
      totalpaid = totalpaid + e.allowedAmount;
    });
    totalpaid == this.sectionLevelInfoTotalConfig.allowedAmount ? this.sectionLevelInfoTotalConfig.isCorrectTotalAllowedAmt = true : this.sectionLevelInfoTotalConfig.isCorrectTotalAllowedAmt = false;

  }



  checkReconcileLogic() {
    if (this.sectionLevelInfoTotalConfig.balanceFromEsAfterPosting == (this.sectionLevelInfoTotalConfig.billToPatientAmount + this.sectionLevelInfoTotalConfig.balanceFromEsBeforePosting)) {
      this.sectionLevelInfoTotalConfig.reconcile = true;
    } else {
      this.sectionLevelInfoTotalConfig.reconcile = false;
    }
  }


  clearTotalValues() {
    // this.sectionLevelInfoTotalConfig.allowedAmount = 0;
    this.sectionLevelInfoTotalConfig.adjustmentAmount = 0;
    // this.sectionLevelInfoTotalConfig.paidAmount = 0;
    this.sectionLevelInfoTotalConfig.estPrimary = 0;
    this.sectionLevelInfoTotalConfig.fee = 0;
    this.sectionLevelInfoTotalConfig.billToPatientAmount = 0;
    this.sectionLevelInfoTotalConfig.creditAdjustmentAmount = 0;
    this.sectionLevelInfoTotalConfig.debitAdjustmentAmount = 0;
  }

  addDecimalInTotalServiceValue() {
    // this.sectionLevelInfoTotalConfig.allowedAmount = +this.sectionLevelInfoTotalConfig.allowedAmount?.toFixed(2);
    this.sectionLevelInfoTotalConfig.adjustmentAmount = +this.sectionLevelInfoTotalConfig.adjustmentAmount?.toFixed(2);
    // this.sectionLevelInfoTotalConfig.paidAmount = +this.sectionLevelInfoTotalConfig.paidAmount?.toFixed(2);
    this.sectionLevelInfoTotalConfig.estPrimary = +this.sectionLevelInfoTotalConfig.estPrimary?.toFixed(2);
    this.sectionLevelInfoTotalConfig.fee = +this.sectionLevelInfoTotalConfig.fee?.toFixed(2);
    this.sectionLevelInfoTotalConfig.billToPatientAmount = +this.sectionLevelInfoTotalConfig.billToPatientAmount?.toFixed(2);
    this.sectionLevelInfoTotalConfig.creditAdjustmentAmount = +this.sectionLevelInfoTotalConfig.creditAdjustmentAmount?.toFixed(2);
    this.sectionLevelInfoTotalConfig.debitAdjustmentAmount = +this.sectionLevelInfoTotalConfig.debitAdjustmentAmount?.toFixed(2);

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
          ths.claimSteps = this.filterConsecutiveDuplicates(res.data);
        }
      });
  }

  filterConsecutiveDuplicates(data: any) {
    if (data.length === 0) return data;
    const filteredData = [data[0]];
    for (let i = 1; i < data.length; i++) {
      const prev = filteredData[filteredData.length - 1];
      const current = data[i];
      if (prev.status === current.status && prev.name === current.name) {
        continue;
      } else {
        filteredData.push(current);
      }
    }

    return filteredData;
  }

  saveInsuranceFollowUpInfo(isFinal: boolean) {
    this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal']['sectionId'] = this.sectionIds['INSURANCE_FOLLOW_UP']['sectionId'];
    if (!isFinal && this.validate_INSURANCE_FOLLOW_UP()) {
      this.loader.insuranceFollowUpInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        rcmFollowUpInsuranceInfoModel: {
          ...this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal'],
          nextFollowUpDate: this.convertStringToDateForApiCall(this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['nextFollowUpDate'])
        }

      };
      console.log(params);
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'INSURANCE_FOLLOW_UP', '2');
        this.loader.insuranceFollowUpInfo = false;
      })
    }
    this.isNextFollowUpRequired = false;
    return this.claimSectionModal['INSURANCE_FOLLOW_UP']['modal'];
  }

  savePatientStaementInfo(isFinal: boolean) {
    this.claimSectionModal['PATIENT_STATEMENT']['sectionId'] = this.sectionIds['PATIENT_STATEMENT']['sectionId'];

    if (!isFinal && this.validate_PATIENT_STATEMENT()) {
      this.loader.patientStaementInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        rcmPatientStatementInfoModel: {
          ...this.claimSectionModal['PATIENT_STATEMENT'],
          statementSendingDate: this.convertStringToDateForApiCall(this.claimSectionModal['PATIENT_STATEMENT']['statementSendingDate']),
          nextReviewDate: this.convertStringToDateForApiCall(this.claimSectionModal['PATIENT_STATEMENT']['nextReviewDate']),
          nextStatementDate: this.convertStringToDateForApiCall(this.claimSectionModal['PATIENT_STATEMENT']['nextStatementDate'])
        }
      };
      console.log(params);

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'PATIENT_STATEMENT', '1');
        this.loader.patientStaementInfo = false;
      })
    }
    return this.claimSectionModal['PATIENT_STATEMENT'];
  }

  savePatientPaymentInfo(isFinal: boolean) {
    this.claimSectionModal['PATIENT_PAYMENT']['sectionId'] = this.sectionIds['PATIENT_PAYMENT']['sectionId'];

    if (!isFinal && this.validate_PATIENT_PAYMENT()) {
      this.loader.patientPaymentInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        patientPaymentInfoModel: {
          ...this.claimSectionModal['PATIENT_PAYMENT'],
          dateOfPayment: this.convertStringToDateForApiCall(this.claimSectionModal['PATIENT_PAYMENT']['dateOfPayment'])
        }
      };

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'PATIENT_PAYMENT', '1');
        this.loader.patientPaymentInfo = false;
      })
    }
    return this.claimSectionModal['PATIENT_PAYMENT'];

  }

  saveNextActionRequiredSection(isFinal: boolean) {
    this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'] = this.sectionIds['CURRENT_STATUS_AND_NEXT_ACTION']['sectionId'];
    this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['assignToTeamId'] = +this.claimSectionModal['CURRENT_STATUS_AND_NEXT_ACTION']['assignToTeamId'];
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
    if (!isFinal && this.validate_PATIENT_COMMUNICATION()) {
      this.loader.patientCommunicationSection = true;
      let params: any = {
        claimUuid: this.claimUUid,
        patientCommunicationInfoModel: this.claimSectionModal['PATIENT_COMMUNICATION']['modal']
      };
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'PATIENT_COMMUNICATION', '2');
        this.loader.patientCommunicationSection = false;
      })
    }
    return this.claimSectionModal['PATIENT_COMMUNICATION']['modal'];
  }

  selectStatementBox(buttonType: any) {
    this.claimSectionModal.PATIENT_STATEMENT['buttonType'] = buttonType;
    //this.clearExisitingPatientStatmentValues(buttonType);
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

  clearCheckDeliverOtherThanModeCheck() {
    this.claimSectionModal.INSURANCE_PAYMENT_INFORMATION['paymentMode'] == 'Check' ? this.checkDeliveredTo = true : this.checkDeliveredTo = false;
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
      if (ths.sectionIds[section.sectionName]?.['isNewSection'] == false && section.sectionId == ths.sectionIds[section.sectionName]?.['sectionId']) {
        isOldSectionNeeded = true;
      }
    }
    console.log("isOldSectionNeeded->" + isOldSectionNeeded);
    return isOldSectionNeeded;
  }

  updateBtpFlagValue(event: any) {

    if (event.target.checked) {
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
        e.flag = true;
        this.isBtpFlagTrue = true;
      });
    } else {
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
        e.flag = false;
        this.isBtpFlagTrue = false;
      });
    }
  }

  checkValidationReqRebilling() {
    if (this.validate_REQUEST_REBILLING()) {
      this.serviceLevelSectionMultiSelectConfig.showModal = true;
    }


  }


  saveRequestBillingInfo(isFinal: boolean) {
    this.claimSectionModal['REQUEST_REBILLING']['sectionId'] = this.sectionIds['REQUEST_REBILLING']['sectionId'];
    this.claimSectionModal['REQUEST_REBILLING']['billingUserUuid'] = this.claimRcm.billingUserUuid;
    if (!isFinal) {
      let params: any = {
        claimUuid: this.claimUUid,
        requestRebillingInfoModel: this.claimSectionModal['REQUEST_REBILLING']
      };

      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        if (res.status) {
          location.reload();
          console.log(res);
        }
      })
    }
    return this.claimSectionModal['REQUEST_REBILLING'];
  }

  concatenateRequiredRebilling() {
    let concatRequiredRequestBilling: any;
    concatRequiredRequestBilling = this.claimSectionModal['REQUEST_REBILLING']['rebillingRequirements'].reduce((accumulator: any, currentValue: any) => {
      return accumulator + "," + currentValue.name;
    }, '');
    concatRequiredRequestBilling = concatRequiredRequestBilling.replace(",", '');
    this.claimSectionModal['REQUEST_REBILLING']['rebillingRequirements'] = concatRequiredRequestBilling;
  }

  concatenateRebillingServiceCode() {
    let concatServiceCode: any = this.claimSectionModal['REQUEST_REBILLING']['rebillingServiceCodes'].reduce((accumulator: any, currentValue: any) => {
      return accumulator + "," + currentValue.name;
    }, '');
    concatServiceCode = concatServiceCode.replace(",", '');
    this.claimSectionModal['REQUEST_REBILLING']['rebillingServiceCodes'] = concatServiceCode;
  }

  concatenateAllRebillingServiceCode() {
    let concatServiceCode: any = this.serviceLevelSectionMultiSelectConfig.serviceCodesList.reduce((accumulator: any, currentValue: any) => {
      return accumulator + "," + currentValue.name;
    }, '');
    concatServiceCode = concatServiceCode.replace(",", '');
    this.claimSectionModal['REQUEST_REBILLING']['rebillingServiceCodes'] = concatServiceCode;
  }

  selectRebillingType(type: any) {
    this.claimSectionModal.REQUEST_REBILLING['rebillingType'] = type;
    if (type == 'fullClaim') {
      this.serviceLevelSectionMultiSelectConfig.serviceCodesList = [];
      this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any) => {
        if (e.serviceCode.toLowerCase() !== 'undistributed') {
          this.serviceLevelSectionMultiSelectConfig.serviceCodesList.push({ name: e.serviceCode });
        }
      })
      this.concatenateAllRebillingServiceCode();
    } else {
      this.claimSectionModal.REQUEST_REBILLING['rebillingServiceCodes'] = [];
    }
  }

  receiveChildrenEvent(event: any) {
    if (event['action'] === 'updateServiceCode') {
      this.claimSectionModal.REQUEST_REBILLING['rebillingServiceCodes'] = event.value;
      if (event.value.length == this.serviceLevelSectionMultiSelectConfig.serviceCodesList.length) {
        this.claimSectionModal.REQUEST_REBILLING['rebillingType'] = 'fullClaim';
        this.serviceLevelSectionMultiSelectConfig.serviceCodesList.forEach((e: any) => e.checked = false);
      } else {
        this.claimSectionModal.REQUEST_REBILLING['rebillingType'] = 'partialClaim';
      }
      this.concatenateRebillingServiceCode();
    } else if (event['action'] === 'updateRequiredReBilling') {
      this.claimSectionModal.REQUEST_REBILLING['rebillingRequirements'] = event.value;
      this.concatenateRequiredRebilling();
    } else if (event['action'] === 'updateReBillingServiceCode') {
      this.removeObjectFromArray(event.value, 'rebillingServiceCode');
    } else if (event['action'] === 'updateReBilling') {
      this.removeObjectFromArray(event.value, 'rebillingRequirement');
    } else if (event['action'] === 'updateRecreateServiceCodes') {
      this.removeObjectFromArray(event.value, 'reCreateServiceCodes');
    }
    console.log(event.value);

  }

  removeObjectFromArray(array: any, type: any) {
    if (type === 'rebillingServiceCode') {
      this.claimSectionModal.REBILLING['dataModal']['selectedRebillingServiceCodes'] = array.map((obj: any) => obj.name);
    } else if (type === 'rebillingRequirement') {
      this.claimSectionModal.REBILLING['dataModal']['selectedRebillingRequirements'] = array.map((obj: any) => obj.name);
    } else if (type === 'reCreateServiceCodes') {
      this.claimSectionModal.RECREATE_CLAIM['modal']['selectedServiceCodes'] = array.map((obj: any) => obj.name);

      if (this.claimSectionModal.RECREATE_CLAIM['modal']['selectedServiceCodes'].length === this.claimSectionModal.RECREATE_CLAIM['modal']['serviceCodesServiceLevel'].length) {
        this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] = 2;
        this.claimSectionModal.RECREATE_CLAIM['modal']['serviceCodesServiceLevel'].forEach((e: any) => e.checked = false);
      } else {
        this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] = 3;
      }

    }
  }

  getAllServiceCodes() {
    this.claimSectionModal['SERVICE_LEVEL_INFORMATION'].data.forEach((e: any, idx: any) => {
      if (e.serviceCode.toLowerCase() !== 'undistributed') {
        this.serviceLevelSectionMultiSelectConfig.serviceCodesList.push({ name: e.serviceCode, checked: false });
      }
      // this.serviceLevelSectionMultiSelectConfig.rebillingRequirements.push({ name: `option${idx}`, checked: false });
    })
    this.claimSectionModal.RECREATE_CLAIM['modal']['serviceCodesServiceLevel'] = this.serviceLevelSectionMultiSelectConfig.serviceCodesList;   //for recreation section service codes
    this.serviceLevelSectionMultiSelectConfig.rebillingRequirements = this.appConstants.requestRebillingRequirement;
  }

  closeReqRebillingModal() {
    this.serviceLevelSectionMultiSelectConfig.showModal = false;
  }

  selectRebillingStatus(rebillingStatus: boolean) {
    this.claimSectionModal.REBILLING['modal']['rebillingStatus'] = rebillingStatus;
  }

  selectRecreationRebillingClaim(status: boolean) {
    this.claimSectionModal.REBILLING['modal']['reCeationOptionChoosen'] = status;
  }

  saveRebillingSection(isFinal: boolean) {
    let reBillingModal: any = this.claimSectionModal['REBILLING'];
    reBillingModal['dataModal']['sectionId'] = this.sectionIds['REBILLING']['sectionId'];
    reBillingModal['dataModal']['reasonForRebilling'] = reBillingModal['modal']['reasonForRebilling'];
    reBillingModal['dataModal']['requestedRemarks'] = reBillingModal['modal']['requestedRemarks'];
    reBillingModal['dataModal']['rebillingRemarks'] = reBillingModal['modal']['rebillingRemarks'];
    reBillingModal['dataModal']['rebillingStatus'] = reBillingModal['modal']['rebillingStatus'];
    reBillingModal['dataModal']['requestedByUuid'] = reBillingModal['modal']['requestedByUuid'];
    reBillingModal['dataModal']['originalServiceCodes'] = reBillingModal['modal']['originalServiceCodes'];
    reBillingModal['dataModal']['originalRequirements'] = reBillingModal['modal']['originalRequirements'];
    reBillingModal['dataModal']['claimTransferNextTeamId'] = reBillingModal['modal']['claimTransferNextTeamId'];
    reBillingModal['dataModal']['reCeationOptionChoosen'] = reBillingModal['modal']['reCeationOptionChoosen'];
    reBillingModal['dataModal']['usedAI'] = reBillingModal['modal']['usedAI'];


    if (!isFinal) {
      if (this.checkValidationForRebilling()) {

        let params: any = {
          claimUuid: this.claimUUid,
          rebillingInfoModel: this.claimSectionModal['REBILLING']['dataModal']
        };
        this.appService.saveClaimLevelInfoSection(params, (res: any) => {
          if (res.status) {
            location.reload();
            console.log(res);
          }
        })
      }

    }
    return this.claimSectionModal['REBILLING']['dataModal'];
  }

  checkValidationForRebilling() {
    let isSectionValidated = true;
    this.emptyFields["REBILLING"] = {};
    if (!this.claimSectionModal["REBILLING"]['dataModal'].rebillingRemarks) {
      this.emptyFields["REBILLING"]['rebillingRemarks'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal['REBILLING']['dataModal']['rebillingStatus'] && !this.claimSectionModal.REBILLING['dataModal']['selectedRebillingServiceCodes'] || this.claimSectionModal.REBILLING['dataModal']['selectedRebillingServiceCodes'].length == 0) {
      this.emptyFields["REBILLING"]['selectedRebillingServiceCodes'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal['REBILLING']['dataModal']['rebillingStatus'] && !this.claimSectionModal.REBILLING['dataModal']['selectedRebillingRequirements'] || this.claimSectionModal.REBILLING['dataModal']['selectedRebillingRequirements'].length == 0) {
      this.emptyFields["REBILLING"]['selectedRebillingRequirements'] = true;
      isSectionValidated = false;
    }
    return isSectionValidated;

  }

  selectActionToPerformRecreate(action: any) {
    this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] = action;
    this.claimSectionModal.RECREATE_CLAIM['modal']['secondaryValid'] = false;
    this.hideRecreateButton = false;
    this.emptyFields.RECREATE_CLAIM = {};
    if (action == 'attachSecondary') {
      this.claimSectionModal.RECREATE_CLAIM['modal']['newClaimId'] = '';

      this.validateNewClaimId(action);
    } else if (action === '3' || action === '2') {
      this.claimSectionModal.RECREATE_CLAIM['validationData'] = [];
    }

  }

  validateNewClaimId(action?: any) {
    const claimId = this.claimRcm.claimId.split('_')[0];
    this.validateRecreateClaimErrMsg = "";
    if (this.claimSectionModal.RECREATE_CLAIM['modal']['newClaimId'] === claimId) {
      this.validateRecreateClaimErrMsg = 'The claim ID entered is the same as the current claim ID. Please enter a different one.';
      this.loader['validationData'] = false;
      return;
    }
    this.loader['validationData'] = true;
    this.validateRecreateClaimErrMsg = "";
    this.claimSectionModal.RECREATE_CLAIM.validationData = [];
    this.emptyFields.RECREATE_CLAIM = {};
    let params: any = {
      currentClaimUuid: this.claimUUid,
      newClaimId: this.claimSectionModal.RECREATE_CLAIM['modal']['newClaimId'],
      buttonType: this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] == 2 || this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] == 3 ? null : this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'],
    }

    this.appService.validateNewClaimId(params, (res: any) => {
      if (res.data) {
        console.log(res);
        this.loader['validationData'] = false;
        this.claimSectionModal.RECREATE_CLAIM['validationData'] = res.data.validationResponse;
        this.claimSectionModal.RECREATE_CLAIM['newServiceCodes'] = res.data.serviceCodesNewClaim;
        if (action && action === 'attachSecondary') this.claimSectionModal.RECREATE_CLAIM['modal']['secondaryValid'] = res.data.secondaryValid;
        //this.claimSectionModal.RECREATE_CLAIM['claimFromSheet']['claimTypeS'] = 'Billing';
        if (action && action === '3') this.showOrHideRecreateButton();
        this.otherErrormsg = '';
        if (res.data?.serviceCodesNewClaim?.length == 0) {
          this.otherErrormsg = "No Service Codes Found";
        }
      }
    })
  }

  showOrHideRecreateButton() {
    this.hideRecreateButton = false;
    this.claimSectionModal.RECREATE_CLAIM['validationData'].forEach((e: any) => {
      if ((e.ruleId == 325 || e.ruleId == 326) && e.resultType == 'FAIL') {
        this.hideRecreateButton = true;
      } else if ((e.ruleId == 324 || e.ruleId == 330 || e.ruleId == 329) && e.resultType == 'FAIL') {
        this.hideRecreateButton = true;
      }
    })
  }

  saveRecreateNewClaim(isFinal: boolean) {
    this.otherErrormsg = "";
    this.createModalForRecreateFullAndPartialClaim();

    //debugger;
    if (!isFinal) {
      if (this.checkValidationForRecreate()) {
        let params: any = {
          claimUuid: this.claimUUid,
          recreateClaimRequestInfoModel: this.claimSectionModal['RECREATE_CLAIM']['dataModal']
        };
        console.log(params);
        //debugger;
        let recreateModal: any = this.claimSectionModal.RECREATE_CLAIM;
        this.loader['attachSecondaryCreationValidationData'] = true;
        this.claimSectionModal.RECREATE_CLAIM.attachSecondaryCreationValidationData = [];

        if (recreateModal['modal']['buttonType'] == 'attachSecondary') {
          this.appService.validateSecondaryClaimData(this.claimUUid, params.recreateClaimRequestInfoModel.claimFromSheet[0], (res: any) => {
            if (res.status) {
              this.loader['attachSecondaryCreationValidationData'] = false;
              if (res.data.length > 0) {
                this.claimSectionModal.RECREATE_CLAIM.attachSecondaryCreationValidationData = res.data;
              } else {
                this.appService.saveClaimLevelInfoSection(params, (res1: any) => {
                  if (res1.status) {
                    location.reload();
                    console.log(res1);
                  }
                })
              }
              //location.reload();

            }
          })
        } else {
          this.appService.saveClaimLevelInfoSection(params, (res: any) => {
            if (res.status) {
              location.reload();
              console.log(res);
            }
          })
        }

      }
      else {
        this.otherErrormsg = "Fill all mandatory fields";
        if (this.emptyFields.RECREATE_CLAIM['selectedServiceCodes'] = true) {
          this.otherErrormsg += ". Service Codes not matched.";
        }
      }
    }

    return this.claimSectionModal['RECREATE_CLAIM'];
  }

  checkValidationForRecreate() {
    let isSectionValidated = true;
    this.emptyFields["RECREATE_CLAIM"] = {};
    if (!this.claimSectionModal.RECREATE_CLAIM['dataModal']['newClaimId'] && this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] != 'attachSecondary') {
      this.emptyFields.RECREATE_CLAIM['newClaimId'] = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal.RECREATE_CLAIM['dataModal']['reasonRecreation'] && this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] != 'attachSecondary') {
      this.emptyFields.RECREATE_CLAIM['reasonRecreation'] = true;
      isSectionValidated = false;
    }
    if (!this.claimSectionModal.RECREATE_CLAIM['dataModal']['recreationRemarks'] && this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] != 'attachSecondary') {
      this.emptyFields.RECREATE_CLAIM['recreationRemarks'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] == 3 && this.claimSectionModal.RECREATE_CLAIM['dataModal']['selectedServiceCodes'].length == 0) {
      this.emptyFields.RECREATE_CLAIM['selectedServiceCodes'] = true;
      isSectionValidated = false;
    }
    if (this.claimSectionModal.RECREATE_CLAIM.validationData.length > 0) {
      this.claimSectionModal.RECREATE_CLAIM.validationData.forEach((e: any) => {
        if (e.resultType == 'FAIL' && !e.remarks) {
          e.emptyRemarks = true;
          isSectionValidated = false;
        } else {
          e.emptyRemarks = false;
        }
      })
    }
    if (this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] == 3) {
      this.selectedServiceCodesExist = this.claimSectionModal.RECREATE_CLAIM['modal']['selectedServiceCodes'].some((serviceCodes: any) => this.claimSectionModal.RECREATE_CLAIM['newServiceCodes'].includes(serviceCodes));
      isSectionValidated = this.selectedServiceCodesExist;
    }
    if (this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] == 'attachSecondary' && this.claimSectionModal.RECREATE_CLAIM['modal']['secondaryValid']) {
      this.isSecondaryFieldsNotEmpty((secondaryFieldsValid: any) => {
        isSectionValidated = secondaryFieldsValid;
      });
    }
    return isSectionValidated;

  }

  createModalForRecreateFullAndPartialClaim() {
    let recreateModal: any = this.claimSectionModal.RECREATE_CLAIM;
    recreateModal['dataModal']['sectionId'] = this.sectionIds['RECREATE_CLAIM']['sectionId'];
    recreateModal['dataModal']['actionButtonType'] = recreateModal['modal']['buttonType'] == 'attachSecondary' ? 1 : +recreateModal['modal']['buttonType'];
    recreateModal['dataModal']['existingNewClaimServiceCodes'] = recreateModal['newServiceCodes'];
    recreateModal['dataModal']['newClaimId'] = recreateModal['modal']['newClaimId'];
    recreateModal['dataModal']['reasonRecreation'] = recreateModal['modal']['reasonRecreation'];
    recreateModal['dataModal']['recreationRemarks'] = recreateModal['modal']['recreationRemarks'];
    recreateModal['dataModal']['selectedServiceCodes'] = recreateModal['modal']['selectedServiceCodes'];
    this.getValdationFailRemark((failRemarks: any) => recreateModal['dataModal']['validationRuleRemarks'] = failRemarks);
    recreateModal['dataModal']['reCeationOptionChoosen'] = this.claimSectionModal['REBILLING']['modal']['reCeationOptionChoosen'];
    recreateModal['dataModal']['rebillingResponseDto'] = {
      "rebillingRemarks": this.claimSectionModal['REBILLING']['modal']['rebillingRemarks'],
      "reasonForRebilling": this.claimSectionModal['REBILLING']['modal']['reasonForRebilling'],
      "requestedByUuid": this.claimSectionModal['REBILLING']['modal']['requestedByUuid']
    };

    recreateModal['dataModal']['claimFromSheet'] = [recreateModal['claimFromSheet']];

  }

  isSecondaryFieldsNotEmpty(callback: any) {
    let secondaryFieldsValid = true;
    this.claimSectionModal.RECREATE_CLAIM['emptyClaimFromSheet'] = {};
    for (const key in this.claimSectionModal.RECREATE_CLAIM['claimFromSheet']) {
      if (!this.claimSectionModal.RECREATE_CLAIM['claimFromSheet'][key]) {
        this.claimSectionModal.RECREATE_CLAIM['emptyClaimFromSheet'][key] = true;
        secondaryFieldsValid = false;
      }
    }
    callback(secondaryFieldsValid);
  }

  getValdationFailRemark(callback: any) {
    let failRemarks: any = [];
    this.claimSectionModal.RECREATE_CLAIM.validationData.forEach((e: any) => {
      failRemarks.push(
        {
          ruleId: e.ruleId,
          remarks: e.remarks,
          message: e.message,
          messageType: e.resultType == "FAIL" ? 1 : e.resultType == "PASS" ? 2 : 3
        }
      )
    })
    callback(failRemarks);
  }

  clearSelectedRadioButtons() {
    const input = this.claimSectionModal.RECREATE_CLAIM['modal']['newClaimId'];
    this.isValidInput = input.trim() !== '' ? true : false;

    if (this.claimSectionModal.RECREATE_CLAIM['modal']['newClaimId'] === '') {
      this.claimSectionModal.RECREATE_CLAIM['modal']['buttonType'] = null;
    }
    this.claimSectionModal.RECREATE_CLAIM['validationData'] = [];
  }

  selectCollectionTypeButton(type: any) {
    console.log(type);

    this.claimSectionModal.COLLECTION_AGENCY['buttonType'] = type;
  }

  calculateNetAmtReceived() {
    this.claimSectionModal.COLLECTION_AGENCY['netAmountReceived'] = this.claimSectionModal.COLLECTION_AGENCY['amountReceived'] - this.claimSectionModal.COLLECTION_AGENCY['commisionCharged'];
  }



  saveCollectionAgencyInfo(isFinal: boolean) {
    this.claimSectionModal['COLLECTION_AGENCY']['sectionId'] = this.sectionIds['COLLECTION_AGENCY']['sectionId'];
    if (!isFinal && this.validate_COLLECTION_AGENCY()) {
      this.loader.collectionAgencyInfo = true;
      let params: any = {
        claimUuid: this.claimUUid,
        collectionAgencyInfoModel: this.claimSectionModal['COLLECTION_AGENCY']
      };
      this.appService.saveClaimLevelInfoSection(params, (res: any) => {
        this.showAlert(res, 'COLLECTION_AGENCY', '1');
        this.loader.collectionAgencyInfo = false;
      })
    }
    return this.claimSectionModal['COLLECTION_AGENCY'];
  }

  clearInsuranceFollowUpSection() {

    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['nextFollowUpRequired'] = "";
    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['currentClaimStatus'] = "";
    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['modeOfFollowUp'] = "";
    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['refNumber'] = "";
    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['insuranceRepName'] = "";
    //this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['nextFollowUpDate'] = "";
    this.claimSectionModal.INSURANCE_FOLLOW_UP['modal']['followUpRemarks'] = "";
  }


  allowOtherSaveWhileReqRebilling() {
    return this.claimRcm.rebilledStatus;
  }

  nextActionRequire(e: any) {
    this.validate_CURRENT_STATUS_AND_NEXT_ACTION();
    console.log(e.target.value);
    if (this.isCDP) {
      if (e.target.value === this.appConstants.Need_to_call_Insurance) {
        //check if Current Logged i team is CDP then add CDP to Assign to team 
        if (this.teamsMs.filter(x => { x.teamName === 'CDP' }).length == 0) {
          let m: TeamsM = {};
          m.teamId = AppConstants.CDP_TEAM;
          m.teamName = 'CDP';
          this.teamsMs.push(m);
        }
      }
      else {
        this.teamsMs = this.teamsMs.filter(obj => { return obj.teamId !== AppConstants.CDP_TEAM });
      }
    }
  }

  isPrimaryClaimClosed() {
    if (this.claimRcm.primary && this.claimRcm.currentStatus === this.appConstants.CLOSED_CLAIM_STATUS) {
      return false;
    }
    if (!this.claimRcm.primary && this.claimRcm.currentStatus === this.appConstants.CLOSED_CLAIM_STATUS) {
      return false;
    }
    if (!this.claimRcm.primary && this.claimRcm.assoicatedClaimCurrentStatus != this.appConstants.CLOSED_CLAIM_STATUS)
      return false;
    else return true;
  }

  hideSideBar() {
    this.toggleSideBar = !this.toggleSideBar;
    if (!this.toggleSideBar) {
      this.hideSideBarDom = false;
    }
    setTimeout(() => {
      if (this.toggleSideBar) {
        this.hideSideBarDom = true;
      }
    }, 100);
  }

  onError(error: any) {
    if (error != null) {
      window.open(this.pdfUrlSrc, '_blank');
      this.isPdfError = true;
    } else
      this.isPdfError = false;
  }

  showAlert(response: any, sectionName: string, responseType: string) {
    this.claimSectionModal[sectionName]['errorMessage'] = '';
    if (response.status === 200) {
      if (responseType === "2") {
        if (sectionName === 'EOB') {
          this.claimSectionModal['EOB']['pdfLink'] = '';
          this.claimSectionModal['EOB'].data.push(response.data);
          this.pdfUrlSrc = response.data.eobLink;
        }
        if (sectionName === 'INSURANCE_FOLLOW_UP') {
          this.clearInsuranceFollowUpSection();
          this.claimSectionModal['INSURANCE_FOLLOW_UP'].data = [response.data, ... this.claimSectionModal['INSURANCE_FOLLOW_UP'].data];
        }
        if (sectionName === 'PATIENT_COMMUNICATION') {
          this.claimSectionModal['PATIENT_COMMUNICATION'].data.push(response.data);
        }
        this.claimSectionModal[sectionName]['errorMessage'] = 'Saved Successfully.';
        this.claimSectionModal[sectionName]['rsType'] = '1';
      }
      if (responseType === "1") {
        if (response.data === true) {
          this.claimSectionModal[sectionName]['errorMessage'] = 'Saved Successfully.';
          this.claimSectionModal[sectionName]['rsType'] = '1';
        } else {
          this.claimSectionModal[sectionName]['errorMessage'] = 'Failed to save.';
          this.claimSectionModal[sectionName]['rsType'] = '2';
        }
      }
      console.log(response);
    } else {
      this.claimSectionModal[sectionName]['errorMessage'] = 'Failed to save.';
      this.claimSectionModal[sectionName]['rsType'] = '3';
    }
    setTimeout(() => {
      this.claimSectionModal[sectionName]['errorMessage'] = '';
    }, 2000);
  };

  onFollowUpChange(event: any) {
    if (event == 'YES') {
      this.isNextFollowUpRequired = true;
    } else {
      this.isNextFollowUpRequired = false;
    }
  }

}
