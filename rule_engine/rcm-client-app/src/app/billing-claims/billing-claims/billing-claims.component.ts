import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimRcmDataModel } from '../../models/claim-rcm-data-model';
import {ClaimAssociateDetailModel} from '../../models/claim-associate-detail-model';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-billing-claims',
  templateUrl: './billing-claims.component.html',
  styleUrls: ['./billing-claims.component.scss']
})
export class BillingClaimsComponent implements OnInit {



  claimRcm: ClaimRcmDataModel;

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants,
      private route: ActivatedRoute) {
   this.claimRcm ={claimId:""};

   }

  
  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      console.log(params.get('uuid'))
       this.fetchClaimsByUuid(params.get('uuid') || '');
      });

   
  }

  

  
 

  fetchClaimsByUuid(uuid:string){

    let ths=this;
    ths.appService.fetchBillingClaimsByUuid(uuid,(res:any)=>{
      if (res.status=== 200){
       ths.claimRcm= res.data;
      
      }else{
      
      }
     
    });
  }
}
