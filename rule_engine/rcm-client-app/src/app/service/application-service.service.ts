import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
import {FreshClaimPullModel} from '../models/fresh.claim.pull.model';
import {ClaimAssignmentPullModel} from '../models/claim-assignment-pull-model';
import { TokenStorageService } from '../service/token-storage.service';
@Injectable({
  providedIn: 'root'
})
export class ApplicationServiceService extends BaseService {


  constructor(http: HttpClient, tokenStorage: TokenStorageService) {
    super(http,tokenStorage);
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

  fetchClient(callback:any){
    this.getData({},this.httpUrl['getAllClients'],callback)
  }

  addNewClients(params:any,callback:any){
    this.postData(params,this.httpUrl['addClients'],callback)
  }

  editClient(params:any,callback:any){
    this.postData(params,this.httpUrl['editClient'],callback)
  }

  sortData(data:any,sortBy:any,sortingColm:any,order:any,sortingType:any,sortType:string){
    if(sortType === 'string'){
        order === 'asc' ? data.sort((a:any, b:any) => {
         if (a[sortBy] === null || a[sortBy] === "null") {
           return 1;
         } else if (b[sortBy] === null || b[sortBy] === "null") {
           return -1;
         } else {
           return a[sortBy].localeCompare(b[sortBy]);
         }
         }) : data.sort((a:any, b:any) => { 
           if (a[sortBy] === null || a[sortBy] === "null") {
             return -1;
           } else if (b.fname === null || b[sortBy] === "null") {
             return 1;
           } else {
             return b[sortBy].localeCompare(a[sortBy]);
           }
         })
       
    }else if(sortType === 'number'){

      order === 'asc' ?  data.sort((a:any, b:any) => {
        return  a[sortBy] - b[sortBy];
       }) :  data.sort((a:any, b:any) => {
         return  b[sortBy] - a[sortBy];
       });
     
  }else if(sortType === 'date'){

    order === 'asc' ?  data.sort((a:any, b:any) => {
      if (a[sortBy] === null && b[sortBy] === null) return 0;
      else if (a[sortBy] === null) return 1;
      else if (b[sortBy] === null) return -1;
      else return <any>new Date(a[sortBy]).getTime() - <any>new Date(b[sortBy]).getTime();
    }) : data.sort((a:any, b:any) =>{ 
      if (a[sortBy] === null && b[sortBy] === null) return 0;
      else if (a[sortBy] === null) return 1;
      else if (b[sortBy] === null) return -1;
      else return <any>new Date(b[sortBy]).getTime() - <any>new Date(a[sortBy]).getTime();
  });
   
}

  }
  sortDataOld(data:any,sortBy:any,sortingColm:any,order:any,sortingType:any,sortType:string){
    if(sortType === 'string'){
      if(sortingColm == 'officeAssigned'){
       order === 'asc' ? data.sort((a:any, b:any) => {
        if (a[sortBy] === null || a[sortBy] === "null") {
          return 1;
        } else if (b[sortBy] === null || b[sortBy] === "null") {
          return -1;
        } else {
          return a[sortBy].localeCompare(b[sortBy]);
        }
        }) : data.sort((a:any, b:any) => { 
          if (a[sortBy] === null || a[sortBy] === "null") {
            return -1;
          } else if (b.fname === null || b[sortBy] === "null") {
            return 1;
          } else {
            return b[sortBy].localeCompare(a[sortBy]);
          }
        })
      }
      else if(sortingColm == 'officeName'){
        order === 'asc' ?  data.sort((a:any, b:any) => {
          return a.officeName.localeCompare(b.officeName)
        }) : data.sort((a:any, b:any) => {  if (a.officeName > b.officeName) {
          return -1;
          }
        if (a.officeName < b.officeName) {
          return 1;
        }
        return 0;
        });
      }
  } else if(sortingType === 'number'){
    if(sortingColm == 'count'){
      order === 'asc' ?  data.sort((a:any, b:any) => {
       return  a.count - b.count;
      }) :  data.sort((a:any, b:any) => {
        return  b.count - a.count;
      });
   }
   else if(sortingColm === 'remoteLiteRejections'){
      order === 'asc' ?  data.sort((a:any, b:any) => {
        return  a.remoteLiteRejections - b.remoteLiteRejections;
      }):  data.sort((a:any, b:any) => {
        return  b.remoteLiteRejections - a.remoteLiteRejections;
      });
    }
   else if(sortingColm === 'countAndRemLiteReject'){
      // order === 'asc' ?  data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) < (p2.remoteLiteRejections+p2.count)) ? 1 : 0) : data.sort((p1:any, p2:any) => ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? -1 : ((p1.remoteLiteRejections+p1.count) > (p2.remoteLiteRejections+p2.count)) ? 1 : 0);
      order === 'asc' ?  data.sort((a:any, b:any) => {
        return (a.count + a.remoteLiteRejections) - (b.count + b.remoteLiteRejections)
      }) : data.sort((a:any, b:any) => {
        return (b.count + b.remoteLiteRejections) - (a.count + a.remoteLiteRejections)
      })
   }
    } else if (sortingType === 'date') {
      if (sortingColm === 'opdt') {
        order === 'asc' ?  data.sort((a:any, b:any) => {
          if (a.opdt === null && b.opdt === null) return 0;
          else if (a.opdt === null) return 1;
          else if (b.opdt === null) return -1;
          else return <any>new Date(a.opdt).getTime() - <any>new Date(b.opdt).getTime();
        }) : data.sort((a:any, b:any) =>{ 
          if (a.opdt === null && b.opdt === null) return 0;
          else if (a.opdt === null) return 1;
          else if (b.opdt === null) return -1;
          else return <any>new Date(b.opdt).getTime() - <any>new Date(a.opdt).getTime();
      });
    }
    else if(sortingColm === 'opdos'){
      order === 'asc' ?  data.sort((a:any, b:any) => {
        if (a.opdos === null && b.opdos === null) return 0;
        else if (a.opdos === null) return 1;
        else if (b.opdos === null) return -1;
        else return <any>new Date(a.opdos).getTime() - <any>new Date(b.opdos).getTime();
      }) : data.sort((a:any, b:any) =>{ 
        if (a.opdos === null && b.opdos === null) return 0;
        else if (a.opdos === null) return 1;
        else if (b.opdos === null) return -1;
        else return <any>new Date(b.opdos).getTime() - <any>new Date(a.opdos).getTime();
    });
    }
  }

  
}

getAllClients(callback:any){
  this.getData({},this.httpUrl['getAllClients'],callback)
}

fetchBillingClaimsByUuid(uuid:string,callback:any){
  this.getData({},this.httpUrl['fetchBillingClaimsByUuid']+"/"+uuid,callback)
}

getClientsName(callback:any){
  this.getData({},this.httpUrl['getClientsName'],callback)
}

}
