import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimRcmDataModel } from '../../models/claim-rcm-data-model';
import {ClaimRulesPullDataModel} from '../../models/claim-rules-pull-data-model';

import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss']
})
export class BillingClaimsComponent implements OnInit {



  claimRcm: ClaimRcmDataModel;
  claimARulesPullDataModel:ClaimRulesPullDataModel={};

  claimUUid:string="";

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
    ths.appService.fetchivfDataForClaim(ths.claimUUid,(res:any)=>{
      if (res.status=== 200){
      
      }
     
    });
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
    

    ths.appService.getClaimRuleData( ths.claimARulesPullDataModel,(res:any)=>{
      if (res.status=== 200){
      
      }
     
    });
  }
}
