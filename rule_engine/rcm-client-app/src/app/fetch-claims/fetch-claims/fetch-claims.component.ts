import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateLogModel } from '../../models/claim-associate-log-model';
import {ClaimAssociateDetailModel} from '../../models/claim-associate-detail-model';
@Component({
  selector: 'app-fetch-claims',
  templateUrl: './fetch-claims.component.html',
  styleUrls: ['./fetch-claims.component.scss']
})
export class FetchClaimsComponent implements OnInit {

  selectedBtype:number=0;
  selectedSubtype:string="Fresh";

  log: Array<ClaimAssociateLogModel>;
  claimDetail:Array<ClaimAssociateDetailModel>;
  isFetchClaims:boolean=true;
  expandCollapse:any={'expandClaim':true,'expandTeamRemarks':true};
  

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants) {
    this.selectedBtype=this.appConstants.BILLING_ID;
    this.log =this.claimDetail= [];

   }

  
  ngOnInit(): void {

    this.fetchClaimsByBillingType(this.selectedBtype);
    this.fetchClaims(this.selectedSubtype);
  }

  

  
  fetchClaimsByBillingType(type:number){
    let ths=this;
    this.selectedBtype=type;
    ths.appService.fetchAssociateClaimBillLogs(type,(res:any)=>{
      if (res.status=== 200){
       ths.log= res.data;

      }else{
        //ERROR
      }
     
    });
  }

  fetchClaims(subType:string){

    let ths=this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype,subType,(res:any)=>{
      if (res.status=== 200){
       ths.claimDetail= res.data;

      }else{
        //ERROR
      }
     
    });
  }

  expandCollapseBox(el:any){
    if(el === 'claimDetails'){
      this.expandCollapse.expandClaim = !this.expandCollapse.expandClaim
    }
    else if(el === 'teamRemarks'){
      this.expandCollapse.expandTeamRemarks = !this.expandCollapse.expandTeamRemarks
    }
  }
}
