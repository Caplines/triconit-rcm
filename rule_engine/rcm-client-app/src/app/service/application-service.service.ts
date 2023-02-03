import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
import {FreshClaimPullModel} from '../models/fresh.claim.pull.model';
import {ClaimAssignmentPullModel} from '../models/claim-assignment-pull-model';

@Injectable({
  providedIn: 'root'
})
export class ApplicationServiceService extends BaseService {


  constructor(http: HttpClient) {
    super(http);
  }

  fetchClaimData(callback:any){

    this.getData({},this.httpUrl['fetchclaims'],callback);

  }

  fetchLatesClaimLLogs(callback:any){
    this.getData({},this.httpUrl['freshclaimlogs'],callback);
  }

  
  pullFreshClaims(model:FreshClaimPullModel,callback:any){
   
    this.postData(model, this.httpUrl['fetchclaimsFromSource'],callback);

  }

  fetchAssociateClaimBillLogs(type:number,callback:any){

      this.getData({}, this.httpUrl['fetchAssociateClaimLogs']+"/"+type,callback);
  }

 
  fetchClaimAssignments(model:ClaimAssignmentPullModel,callback:any){
    this.postData(model, this.httpUrl['fetchclaimsAssignmentData'],callback);
  }

  
  fetchAssociateClaimDet(teamId:number,subtype:string,callback:any){
    this.getData({}, this.httpUrl['fetchAssociateClaimDet']+"/"+teamId+"/"+subtype,callback);
  }

}
