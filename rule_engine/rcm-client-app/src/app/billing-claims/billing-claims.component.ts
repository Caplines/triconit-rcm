import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';

import {
  ClaimRcmDataModel, ClaimEditModel, ServiceLevelCodeModel, SubmissionDetailModel,
  ClaimRuleModel, ClaimRuleRemarkModel, RuleEngineValModel,
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
  mtype: string = '1';//Fail By Default.
  tlErrormsg = "";
  otherErrormsg = "";
  loader: any = { claimDetail: false, linkToRelatedDoc: false, remarksByOther: false, rebilledClaims: false, automatedValidation: false, manualValidation: false, ruleEngValid: false, serviceCode: false, claimSubmission: false }
  //ivfData:any=[];
  updatedIvfId: any;

  modelElement: any = { 'modal': '', 'span': '' }
  constructor(public appService: ApplicationServiceService, public appConstants: AppConstants,
    private claimService: ClaimService,
    private route: ActivatedRoute, private title: Title, private location: Location, private router: Router) {
    this.claimRcm = { claimId: "" };
    title.setTitle(Utils.defaultTitle + "Claim Detail");
  }

  ngOnInit(): void {
    this.smilePoint = Utils.isSmilePoint();
    this.selectedTeam = Utils.selectedTeam();

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
        if (ths.smilePoint) ths.getServiceLevelCodes();//Only In case of Smile point. other does not have it.
        ths.getSubmissionDetails();
        //if (this.smilePoint) ths.getClaimRuleData();//Only In case of Smile point. other does not have it.
        //ths.runAutoRules(false);
        ths.fetchTLUsers();
        ths.fetchOtherTeams();

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
    ths.claimEditModel = {};
    ths.claimEditModel.claimUuid = ths.claimRcm.uuid;
    ths.claimEditModel.claimNoteDtoList = ths.claimRcm.claimNotes;
    ths.claimEditModel.claimRemark = ths.claimRcm.claimRemarks;
    ths.claimEditModel.serCVDto = ths.claimServiceLevelModel?.dto;
    ths.claimEditModel.submissionDto = ths.submissionDto;
    ths.claimEditModel.ruleRemarkDto = [];
    ths.claimRules.forEach(x => {
      x.sectionName = "ClaimLevelValidation"
      if (x.remark != null && x.remark.trim() != '') ths.claimEditModel.ruleRemarkDto.push(x);
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

      ths.assignModel.toOtherTeam = true;
      let valid = ths.validateData();
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
    } else if (type === 'assigntl') {
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

    ths.claimRcm.claimNotes.forEach(no => {
      if (no.value == null || no.value.trim() === '') {
        ths.addErrorDisplay(document.getElementById("CL_N_" + no.id));
        valid = false;

      }
    });


    ths.claimRules.forEach(x => {
      if ((x.remark == null || x.remark.trim() === '') && x.messageType === 1) {//on NO only
        ths.addErrorDisplay(document.getElementById("CL_RU" + x.ruleId));
        valid = false;
      }
      if (x.messageType === 0) {//mark the Yes or No
        ths.addErrorDisplay(document.getElementById("CL_P_F_" + x.ruleId));
        valid = false;
      }
    });

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
      ths.addErrorDisplay(document.getElementById("SUB_DET_TI"));
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

      let SUB_DET_TI: any = document.getElementById("SUB_DET_TI");
      if (SUB_DET_TI.value.trim() === '') {
        ths.addErrorDisplay(document.getElementById("SUB_DET_TI"));
        valid = false;
      }


    }

    if (this.smilePoint) {
      if (ths.claimServiceLevelModel.claimFound) {

        ths.claimServiceLevelModel.dto.forEach((x, i) => {
          debugger;
          if ((x.manualAuto == 'Automated' && (x.remark == null || x.remark.trim() === '')) && x.messageType === 1) {//on NO only
            ths.addErrorDisplay(document.getElementById("SERV_C_V_A" + i));
            valid = false;
          }

        });
        ths.claimServiceLevelModel.dto.forEach((x, i) => {
          if ((x.manualAuto == 'Manual' && x.answer === null || x.answer.trim() === '')) {//on NO only
            ths.addErrorDisplay(document.getElementById("serviceCodeValidationsM" + i));
            valid = false;
          } else if ((x.manualAuto == 'Manual' && (x.remark == null || x.remark.trim() === '')) && x.answer === 'Incorrect') {//on NO only
            ths.addErrorDisplay(document.getElementById("SERV_C_V_M" + i));
            valid = false; {

            }
          }
        });
      } else {
        ths.addErrorDisplay(document.getElementById("serviceCodeValidationsA"));
        ths.addErrorDisplay(document.getElementById("serviceCodeValidationsM"));
      }
    }
    if (this.smilePoint) {

      ths.ruleEngineReport.forEach(x => {

        if (x.mtype === '1' && (x.remark == null || x.remark.trim() === '')) {
          ths.addErrorDisplay(document.getElementById("ENG_REP_" + x.ruleId));
          valid = false;
        }
      });

      if (ths.ruleEngineReport.length == 0) {
        ths.addErrorDisplay(document.getElementById("claimValidationsRE"));

        valid = false;

      } else {
        ths.removeErrorDisplay(document.getElementById("claimValidationsRE"));
      }
    }
    console.log("valid", valid);

    return valid;

  }


  claimNoEnable(): boolean {
    if (this.submissionDto?.channel == 'Portal') {
      return false;
    } return true;
  }

  preAuthEnable(): boolean {
    if (this.submissionDto?.preauth == true) {
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

  getRulesClaimdata() {
    let ths = this;
    ths.loader.automatedValidation = ths.loader.manualValidation = true;
    ths.claimService.getRulesClaimdata(ths.claimRcm.uuid, (res: any) => {
      if (res.status === 200) {
        ths.loader.automatedValidation = ths.loader.manualValidation = false;
        ths.claimRules = res.data;
        this.countA.pass = this.countA.fail = this.countA.alert = 0;
        ths.claimRules.forEach((e: any) => {
          if (e.messageType == 1 && (e.ruleType === 'C' || e.ruleType === 'R,C')) {
            this.countA.fail = this.countA.fail + 1;
          }
          else if (e.messageType == 2 && (e.ruleType === 'C' || e.ruleType === 'R,C')) {
            this.countA.pass = this.countA.pass + 1;
          }
          else if (e.manualAuto == 'AUTO' && (e.ruleType === 'G')) {
            //add filter here
          }
        });

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
    ths.claimService.runAutoRules(ths.claimRcm.uuid, reReun, (res: any) => {
      if (res.status === 200) {
        ths.getRulesClaimdata();
      }
    })
  }

  getClaimRuleData() {
    let ths = this;
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

  makeReadOnly(): boolean {
    if (!this.isBilling) return true;
    else if (!this.claimRcm.pending) return true;
    else if (!this.claimRcm.allowEdit) return true;
    return false;
  }

  isSectionReadOnly(): boolean {
    if (!this.claimRcm.pending) return true;
    if (this.claimRcm.firstTeamId == this.claimRcm.currentTeamId
      && this.claimRcm.allowEdit) {
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
    debugger;
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

  updateIV(claimUuid: any, ivId: any) {
    let params: any = {
      'claimUuid': claimUuid,
      'ivfId': ivId
    };
    this.appService.updateIvId(params, (res: any) => {
      if (res.status) {
        if (res.data.success) {

          this.claimRcm.ivfId = res.data.ivfId;
          this.claimRcm.ivDos = res.data.ivDos;
          this.claimRcm.tpId = res.data.tpId;
          this.claimRcm.tpDos = res.data.tpDos;

        }
      }
      this.closeIvPopup();
    })
  }

  get isRoleLead() {
    return Utils.isRoleLead();
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

  get timeZone(): string {
    return Utils.getTimeZone();
  }

  updateUrl(url: string) {
    if ('/billing-claims/' + url != this.router.url) this.location.go(url);
  }

  goToListofClaimsPage() {
    window.location.href = "/list-of-claims";
  }
}
