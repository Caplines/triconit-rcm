import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
import { FreshClaimPullModel } from '../models/fresh.claim.pull.model';
import { ClaimRcmDataModel, ClaimEditModel } from '../models/claim-rcm-data-model';
import { ClaimRulesPullDataModel } from '../models/claim-rules-pull-data-model';
import { ClaimAssignToTeamModel } from '../models/claim_assign_to_team';
import { TokenStorageService } from '../service/token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ClaimService extends BaseService {
  setPaddingContainer: boolean = false;

  constructor(http: HttpClient, tokenStorage: TokenStorageService) {
    super(http, tokenStorage);
  }

  fetchBillingClaimsByUuid(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['fetchBillingClaimsByUuid'] + "/" + uuid, callback)
  }

  getClaimRuleData(params: ClaimRulesPullDataModel, callback: any) {
    this.postData(params, this.httpUrl['claimRuleData'], callback)
  }


  fetchClaimNotes(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['claimNotes'] + "/" + uuid, callback)
  }

  saveClaimData(claimEditModel: ClaimEditModel, callback: any) {
    this.postData(claimEditModel, this.httpUrl['saveClaim'], callback);
  }

  getServiceLevelCodes(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['servicelevelval'] + "/" + uuid, callback);
  }

  getSubmissionDetails(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['submissionDetails'] + "/" + uuid, callback);
  }


  getRulesClaimdata(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['ruleclaimdata'] + "/" + uuid, callback);
  }

  getRuleRemarks(uuid: string, callback: any) {
    this.getData({}, this.httpUrl['ruleRemark'] + "/" + uuid, callback);
  }

  runAutoRules(uuid: string, reReun: boolean, callback: any) {
    if (reReun) this.getData({}, this.httpUrl['autorules'] + "/" + uuid + "/true", callback);
    else this.getData({}, this.httpUrl['autorules'] + "/" + uuid + "/" + "false", callback);
  }



  fetchIvDetails(clamiduuid: string, ivId: string, callback: any) {
    //if needed we will put iv id in Request
    this.getData({}, this.httpUrl['ivdetails'] + "/" + clamiduuid, callback);
  }


  fetchOtherTeamRemarks(clamiduuid: string, callback: any) {
    this.getData({}, this.httpUrl['other_team_remark'] + "/" + clamiduuid, callback);
  }

  assignClaimToTL(params: ClaimAssignToTeamModel, callback: any) {
    this.postData(params, this.httpUrl['assigntotl'], callback)
  }
}
