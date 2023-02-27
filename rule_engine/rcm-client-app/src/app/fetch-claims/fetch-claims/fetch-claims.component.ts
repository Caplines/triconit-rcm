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
  expandCollapse:boolean=true;
  switchBox:any={'billing':true,'reBilling':false};
  isSorted:boolean=false;
  loader:any={'billingLoader':false,'listClaimLoader':false};
  totalClaimData:any={'oldestOpdt':'','oldestOpdos':'','totalCount':0,'totalRemLiteReject':0}

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants) {
    this.selectedBtype=this.appConstants.BILLING_ID;
    this.log =this.claimDetail= [];

   }

  
  ngOnInit(): void {

    this.fetchClaimsByBillingType(this.selectedBtype);
    this.fetchClaims(this.selectedSubtype);
  }

  

   
  fetchClaimsByBillingType(type:number){
    this.loader.billingLoader=true;
    if(type==1){
      this.switchBox.billing= true;
      this.switchBox.reBilling=false;
    }else{ 
      this.switchBox.billing= false;
      this.switchBox.reBilling=true;
    }
    let ths=this;
    this.selectedBtype=type;
    this.totalClaimData.totalRemLiteReject=this.totalClaimData.totalCount=0;
    ths.appService.fetchAssociateClaimBillLogs(type,(res:any)=>{
      if (res.status=== 200){
       ths.log= res.data;
       ths.calcCount(ths.log)
       ths.calcRemLiteReject(ths.log)
       this.loader.billingLoader=false;

      }else{
        //ERROR
      }
     
    });
  }

  fetchClaims(subType:string){
    this.loader.listClaimLoader=true;
    let ths=this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype,subType,(res:any)=>{
      if (res.status=== 200){
       ths.claimDetail= res.data;
       
       ths.loader.listClaimLoader=false;
      }else{
        //ERROR
      }
     
    });
  }

  sortData(data:any,sortProp:string,order:any,sortType:string){ 
    this.appService.sortData(data,sortProp,order,sortType);
  }

  calcCount(data:any){
    data.forEach((e:any)=>{
      this.totalClaimData.totalCount = this.totalClaimData.totalCount + e.count;
 });
  }
  calcRemLiteReject(data:any){
    data.forEach((e:any)=>{
      this.totalClaimData.totalRemLiteReject = this.totalClaimData.totalRemLiteReject + e.remoteLiteRejections;
   });
  }
}
