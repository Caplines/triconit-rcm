import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

import {
  ClaimRcmDataModel, ClaimEditModel, ServiceLevelCodeModel, SubmissionDetailModel,
  ClaimRuleModel, ClaimRuleRemarkModel, RuleEngineValModel, ServiceLevelCodeDataModel,
  ClaimRuleRemarkModelS, TLUser, TeamsM, OtherTeamRem
} from '../models/claim-rcm-data-model';

import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { ApplicationServiceService } from '../service/application-service.service';
import { AppConstants } from '../constants/app.constants';
import { ClaimService } from '../service/claim.service';
import { ClaimAssignToTeamModel } from '../models/claim_assign_to_team';
import { ClaimRulesPullDataModel } from '../models/claim-rules-pull-data-model';
import Utils from '../util/utils';
import { DownLoadService } from '../service/download.service';

@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss'],
  encapsulation: ViewEncapsulation.None,
})

export class BillingClaimsComponent {

  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  alertAssign: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  claimRcm: ClaimRcmDataModel;
  claimARulesPullDataModel: ClaimRulesPullDataModel = {};
  claimEditModel: ClaimEditModel;
  claimServiceLevelModel: ServiceLevelCodeModel;
  submissionDto: SubmissionDetailModel = {};
  claimRules: Array<ClaimRuleModel> = [];
  claimRuleRemarks: Array<ClaimRuleRemarkModel> = [];
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
  countM300: number = 1;
  relatedTo_300 = true;
  actionButtons = false;
  clientName: string = '';
  assignType: string = '';
  modelElement: any = { 'modal': '', 'span': '' }
  attachmentConfig:any={'showAttachment':false,'attachmentCount':0};

  selectedFilesMap:any= new Map();
  removedFilesMap:any= new Map();

  constructor(public appService: ApplicationServiceService, public appConstants: AppConstants,
    private claimService: ClaimService,
    private route: ActivatedRoute, private title: Title, private location: Location, private router: Router, private downloadService: DownLoadService) {
    this.claimRcm = { claimId: "" };
    title.setTitle(Utils.defaultTitle + "Claim Detail");
  }

  ngOnInit(): void {
    this.smilePoint = Utils.isSmilePoint();
    this.selectedTeam = Utils.selectedTeam();
    this.clientName = localStorage.getItem("selected_clientName");
    this.route.paramMap.subscribe(params => {
      this.claimUUid = params.get('uuid') || '';
      this.fetchClaimsByUuid(this.claimUUid);
    });

  }

  fetchClaimsByUuid(uuid: string) {

    let ths = this;
    ths.claimUUid = uuid;
    ths.updateUrl("/billing-claims/" + uuid);
    this.loader.claimDetail = this.loader.linkToRelatedDoc = true;
    ths.claimService.fetchBillingClaimsByUuid(uuid, (res: any) => {
      if (res.status === 200) {
        this.loader.claimDetail = this.loader.linkToRelatedDoc = false;
        ths.claimRcm = res.data;

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
      }

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

  saveClaim(type: string) {
    let ths = this;
    ths.assignType = type;
    ths.claimEditModel = {};
    ths.claimEditModel.claimUuid = ths.claimRcm.uuid;
    ths.claimEditModel.claimNoteDtoList = ths.claimRcm.claimNotes;
    ths.claimEditModel.claimRemark = ths.claimRcm.claimRemarks;
    ths.claimEditModel.serCVDto = ths.claimServiceLevelModel?.dto;
    ths.claimEditModel.submissionDto = ths.submissionDto;
    ths.claimEditModel.ruleRemarkDto = [];
    ths.claimRules.forEach(x => {
      x.sectionName = "ClaimLevelValidation"
      //if (x.remark != null && x.remark.trim() != '') 
      ths.claimEditModel.ruleRemarkDto.push(x);

    });
    ths.ruleEngineReport.forEach(x => {

      let c = new ClaimRuleRemarkModelS(x.remark, null, null, null, Number(x.ruleId), "RuleEngine");
      if (x.remark != null && x.remark.trim() != '') ths.claimEditModel.ruleRemarkDto.push(c);
    });

    if (type === 'latter') {
      ths.inSave = true;
      ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
        ths.inSave = false;
        this.showAlertPopup(callback);
      });
    }
    else if (type === 'submit') {
      //do From Validation

      let valid = ths.validateData();
      if (valid) {
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
      if (ths.isInternalAudit) {

        ths.claimEditModel.assignToTeam = 7;//ths.teamsMs[0].teamId;
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
    }
    else if (type === 'assigntl') {
      ths.claimEditModel.assignTouuid = "";

      ths.openAssignModal('tl');

    }
  }


  addErrorDisplay(el: HTMLElement) {
    el.classList.add('bg-error');
  }

  removeErrorDisplay(el: HTMLElement) {
    el.classList.remove('bg-error');
  }

  removeErrorDisplayKey(event: any) {
    console.log(event.target.value);
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

      if (x.ruleId == 300) {
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

    if (!ths.isInternalAudit) {//Only Non Audit can Submit
      if (Object.keys(ths.submissionDto).length == 0) {
        ths.addErrorDisplay(document.getElementById("SUB_DET_CHA"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_ATT"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_PRE"));
        ths.addErrorDisplay(document.getElementById("SUB_DET_REF"));
        if (!ths.claimNoEnable()) {

          ths.addErrorDisplay(document.getElementById("SUB_DET_CLA"));
        }
        if (!ths.preAuthEnable()) {
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
        if (!ths.preAuthEnable()) {
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
    if (this.smilePoint && !this.isInternalAudit) {

      ths.ruleEngineReport.forEach(x => {

        if (x.mtype === '1' && (x.remark == null || x.remark.trim() === '')) {
          ths.addErrorDisplay(document.getElementById("ENG_REP_" + x.ruleId));
          valid = false;
        }
      });

      if (ths.ruleEngineReport.length == 0) {
        if (!this.isInternalAudit) {
          //For now Disable if length==0
          //ths.addErrorDisplay(document.getElementById("claimValidationsRE"));

          //valid = false;//Deepak
        }

      } else {
        //ths.removeErrorDisplay(document.getElementById("claimValidationsRE"));
      }
    }
    //valid = false;
    console.log("valid", valid);

    return valid;

  }


  claimNoEnable(): boolean {
    document.getElementById("SUB_DET_CLA").style.display = "";
    document.getElementById("SUB_DET_CLA_SUB").style.display = "";

    if (this.submissionDto?.channel == 'Portal') {

      return false;
    }
    document.getElementById("SUB_DET_CLA").style.display = "none";
    document.getElementById("SUB_DET_CLA_SUB").style.display = "none";
    return true;
  }

  preAuthEnable(): boolean {
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



  showHide(index: any) {
    let el: any = document.querySelectorAll(".bold-b-text");
    for (let i = 0; i < el.length; i++) {
      if (i == index) {
        el[i].children[1].children[0].classList.toggle("close");
        el[i].classList.toggle("collapsed");
        el[i].nextElementSibling.classList.toggle("show");
      }
    }
  }

  getServiceLevelCodes() {
    let ths = this;
    this.loader.serviceCode = true;
    ths.claimService.getServiceLevelCodes(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        this.loader.serviceCode = false;
        ths.claimServiceLevelModel = res.data;
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
        if (ths.submissionDto == null) ths.submissionDto = {};
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
      '<span> | Ins.Type : <span>' + rule.insuranceType + '</span></span>';

  }

  passFail(t: any, type: number) {
    t = type;
  }

  /*
  The claims that will be parked in the Internal Audit Team's bucket first, only sections billing team will be filling will include:
  a) Rules Engine Validations
  b) Enter Claim Submission Details:
  */
  makeReadOnly(): boolean {
    if (this.claimRcm == undefined) return true;
    //if (!this.isBilling) return true;
    else if (!this.claimRcm.pending) return true;
    else if (!this.claimRcm.allowEdit) return true;
    if (this.claimRcm.firstTeamId == 3 && this.isBilling
    ) {
      return true;
    }
    return false;
  }

  isSectionReadOnly(): boolean {
    //debugger;
    if (this.claimRcm == undefined) return true;
    if (!this.claimRcm.allowEdit) return true;
    if (!this.claimRcm.pending) return true;
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

      ths.claimService.saveClaimData(ths.claimEditModel, (callback: any) => {
        ths.inSave = false;
        ths.showAlertPopup(callback);
        ths.claimRcm.allowEdit = false;
        ths.showAlertPopup(callback);
      });
    }



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
    popup.style.display = 'block';
  }

  closeIvPopup() {
    let popup: any = document.getElementById("ivUpdate");
    popup.style.display = 'none';
    this.updatedIvfId = '';
  }

  updateIV(claimUuid: any, ivId: any, tpid: any) {
    let params: any = {
      'claimUuid': claimUuid,
      'ivfId': ivId,
      'tpId': tpid
    };
    this.appService.updateIvId(params, (res: any) => {
      if (res.status) {
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

  enableDisable300Sub(cond: boolean) {
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
    let data = { "fileName": "Claim_Details", "data": [this.claimRcm], "teamId": this.selectedTeam, "clientName": this.clientName, "otherTeamsRemark": this.otherTeamRemarks, "claimRules": this.claimRules, "serviceLevelCodeManual": this.claimServiceLevelModel, "ruleEngineReport": this.ruleEngineReport, "claimSubmissionDto": this.submissionDto, "relatedTo_300": this.relatedTo_300, "countA": this.countA, "countAS": this.countAS, "count": this.count };
    this.appService.claimDetailsPdfDownload(data, "pdf", (res: any) => {
      if (res.status === 200) {
        console.log(res.body);
        this.downloadService.saveBolbData(res.body, "Claim_Details.pdf");
      } else {
        console.log("something went wrong");
      }
    })
  }

  manageProviderOnClaimFromSheet() {
    //Now Multiple values can come
    let ar = this.claimRcm.providerOnClaimFromSheet.split("<<<>>>");
    let temp = "";
    ar.forEach(element => {
      temp = temp + element + "<br/>";
    });

    return temp;
  }

  fetchAttachmentCount(){
    this.appService.fetchAttachmentCount(this.claimUUid,(res:any)=>{
          if(res.status == 200 && res.data){
            this.attachmentConfig['showAttachment'] = true;
            this.attachmentConfig['attachmentCount'] = res.data;
          }
    })
  }

  receiveChildEvent(event:any){
    if(event['action'] === 'fileSelected'){
      this.setSelectedFileForComponent(event.claimUuid, event.value);
    } else if(event['action']==='filesSelectedToRemove'){
        this.setSelectedFileToRemove(event.claimUuid,event.value)
    }
  }

  setSelectedFileForComponent(claimUuid: any, file: File) {
    this.selectedFilesMap.set(claimUuid, file);
  }

  setSelectedFileToRemove(claimUuid: any, fileParam:any){
    this.removedFilesMap.set(claimUuid,fileParam);
  }

  getSelectedFileForComponent(claimUuid: any) {
    return this.selectedFilesMap.get(claimUuid);
  }
  
  getSelectedFilesToRemove(claimUuid: any) {
    return this.removedFilesMap.get(claimUuid);
  }

  submitAttachment(){
      const removedFiles = this.getSelectedFilesToRemove(this.claimUUid);
      const selectedFiles = this.getSelectedFileForComponent(this.claimUUid);

      if(removedFiles){
          this.appService.removeAttachmentFile(removedFiles,(res:any)=>{
                if(res.status == 200 && res.data.fileResponseStatus){
                    this.finalSubmitAttachment(selectedFiles);
                } else{
                  this.showAlertPopup(res);
                }
          })
      } else {
        this.finalSubmitAttachment(selectedFiles);
      }
  }

  finalSubmitAttachment(selectedFile:any[]){
      this.loopThroughData(selectedFile,0);
  }

  loopThroughData(dataArray: any[], currentIndex: number) {
    if (currentIndex >= dataArray.length) {
      return;
    }
    const currentData = dataArray[currentIndex];
    let formData: any = new FormData();
    formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.claimUUid);
    formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
    formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
    this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
      if (res.data.fileResponseStatus) {
        this.loopThroughData(dataArray, currentIndex + 1);
      } else {
       this.showAlertPopup(res);
      }
    })
}
}
