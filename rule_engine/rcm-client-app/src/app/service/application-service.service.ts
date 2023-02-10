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

  fetchLatesClaimLLogs(cName:any,callback:any){
    this.getData({},this.httpUrl['freshclaimlogs']+"/"+cName,callback);
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

  fetchCompanyNameData(callback:any){
    this.getData({},this.httpUrl['getCompany'],callback)
  }

  fetchTeamsNameData(params:any,callback:any){
    this.getData({},this.httpUrl['getTeams']+`/${params}`,callback)
  }

  fetchOfficeData(callback:any){
    this.getData({},this.httpUrl['getOffices'],callback)
  }

  fetchOfficeByCompany(params:any,callback:any){
    this.getData({},this.httpUrl['officeByCompany']+`/${params}`,callback)
  }

  fetchRolesByCompany(companyName:any,callback:any){
    this.getData({},this.httpUrl['rolesByCompanyName']+`/${companyName}`,callback)
  }

  fetchRolesByTeam(teamId:any,callback:any){
    this.getData({},this.httpUrl['rolesByTeam']+`/${teamId}`,callback)
  }

  fetchAllUser(page:any,companyName:any,callback:any){
     this.getData({},this.httpUrl['getAllUsers']+`/${companyName ? companyName : 'All'}/${page}`,callback)
  }

  fetchUserByTeamId(teamId:any,callback:any){
    this.getData({},this.httpUrl['userByTeamId']+`/${teamId}`,callback)
  }

  assignOffice(params:any,callback:any){
    this.postData(params,this.httpUrl['assignOffice'],callback)
  }

  registerUser(params:any,callback:any){
    this.postData(params,this.httpUrl['registerUser'],callback)
  }

  findUser(username:any,callback:any){
    this.postData(username,this.httpUrl['findUserByEmail'],callback)
  }

  changePassword(params:any,callback:any){
    this.postData(params,this.httpUrl['changePassword'],callback)
  }

  updateUserStatus(params:any,callback:any){
    this.postData(params,this.httpUrl['updateStatus'],callback)
  }

  editOfficeName(params:any,callback:any){
    this.postData(params,this.httpUrl['editOfficeName'],callback)
  }

  addNewOffice(params:any,callback:any){
    this.postData(params,this.httpUrl['addNewOffice'],callback)
  }

  sortData(data:any,sortingColm:any,order:any,sortingType:any){
    if(sortingType === 'name'){
      if(sortingColm == 'officeAssigned'){
        order === 'asc' ?  data.sort((p1:any, p2:any) => (p1.fname < p2.fname ||(p1.fname == null || p2.fname == null)) ? -1 : (p1.fname < p2.fname ||(p1.fname == null || p2.fname == null)) ? 1 : 0) : data.sort((p1:any, p2:any) => (p1.fname > p2.fname ||(p1.fname == null || p2.fname == null)) ? -1 : (p1.fname > p2.fname ||(p1.fname == null || p2.fname == null)) ? 1 : 0);
      }
      else if(sortingColm == 'officeName'){
        order === 'asc' ?  data.sort((p1:any, p2:any) => (p1.officeName < p2.officeName) ? -1 : (p1.officeName < p2.officeName) ? 1 : 0) : data.sort((p1:any, p2:any) => (p1.officeName > p2.officeName) ? -1 : (p1.officeName > p2.officeName) ? 1 : 0);
   }
  } else if(sortingType === 'number'){
    if(sortingColm == 'count'){
      order === 'asc' ?  data.sort((p1:any, p2:any) => (p1.count < p2.count) ? -1 : (p1.count < p2.count) ? 1 : 0) : data.sort((p1:any, p2:any) => (p1.count > p2.count) ? -1 : (p1.count > p2.count) ? 1 : 0);
   }
   else if(sortingColm === 'remoteLiteRejections'){
      order === 'asc' ?  data.sort((p1:any, p2:any) => (p1.remoteLiteRejections < p2.remoteLiteRejections) ? -1 : (p1.remoteLiteRejections < p2.remoteLiteRejections) ? 1 : 0) : data.sort((p1:any, p2:any) => (p1.remoteLiteRejections > p2.remoteLiteRejections) ? -1 : (p1.remoteLiteRejections > p2.remoteLiteRejections) ? 1 : 0);
   }
   else if(sortingColm === 'countAndRemLiteReject'){
      order === 'asc' ?  data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? 1 : 0) : data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? 1 : 0);
   }
  } else if(sortingType === 'date'){
    if(sortingColm === 'opdt'){
      order === 'asc' ?  data.sort((p1:any, p2:any) => {
        let date1:any = new Date(p1.opdt);
        let date2:any = new Date(p2.opdt);
        return date1-date2;
      }) : data.sort((p1:any, p2:any) =>{ 
        let date1:any = new Date(p1.opdt);
        let date2:any = new Date(p2.opdt);
        return date2 - date1;
      });
    }
    else if(sortingColm === 'opdos'){
      order === 'asc' ?  data.sort((p1:any, p2:any) => {
        let date1:any = new Date(p1.opdos);
        let date2:any = new Date(p2.opdos);
        return date1-date2;
      }) : data.sort((p1:any, p2:any) =>{ 
        let date1:any = new Date(p1.opdos);
        let date2:any = new Date(p2.opdos);
        return date2 - date1;
      });
    }
  }

  
}

getAllClients(callback:any){
  this.getData({},this.httpUrl['getAllClients'],callback)
}

}
