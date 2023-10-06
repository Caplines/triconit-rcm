import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateLogModel } from '../../models/claim-associate-log-model';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-fetch-claims',
  templateUrl: './fetch-claims.component.html',
  styleUrls: ['./fetch-claims.component.scss']
})
export class FetchClaimsComponent implements OnInit {

  selectedBtype:number=0;
  log: Array<ClaimAssociateLogModel>;
  expandCollapse:boolean=true;
  switchBox:any={'billing':true,'reBilling':false};
  isSorted:boolean=false;
  loader:any={'billingLoader':false,'listClaimLoader':false,'exportPDFLoader':false,'exportCSVLoader':false};
  totalClaimData:any={'oldestOpdt':'','oldestOpdos':'','totalCount':0,'totalRemLiteReject':0}
  date:any;

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants,private title:Title) {
    this.selectedBtype=this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + 'Fetch Claims')
    this.log = [];
   }
  
  ngOnInit(): void {
    this.fetchClaimsByBillingType(this.selectedBtype);
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
      }
      // else{
      //   //ERROR
      //   this.loader.billingLoader = false;
      //   if(res.data == "not Autorized")
      //   this.logout();
      // }
     
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

  exportToCsv() {
    this.loader.exportCSVLoader=true;
    let options: any = {
      showLabels: true,
      headers: ["Office Name","Oldest Pending Since Date","Oldest Pending DOS",`${this.selectedBtype==this.appConstants.BILLING_ID? 'Number of Pending Fresh Cases' : 'Number of Pending Rebilling Cases'}`, "Number of Pending Remotelite Rejections"]
    }
    let excelData: any;
    excelData= [...this.log];  
    excelData = excelData.map((e:any)=>{
      if(e.opdos){
        let date:Date = new Date(e.opdos);
        e = {...e,opdos : `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`};
      } else {
        e = {...e,opdos:'-'};
      }
      if(e.opdt){
        let date:Date = new Date(e.opdt);
        e = {...e,opdt : `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`};
      } else {
        e = {...e,opdt:'-'};
      }
      return e;
    })

    excelData = excelData.map(({officeUuid,...newData}:any)=>newData);
    excelData = excelData.map((e:any)=>{
      return{
        "Office Name":e.officeName,
        "Oldest Pending Since Date":e.opdt,
        "OldestPendingDOS":e.opdos,
        'NumberofPendingFreshCases':e.count,
        "NumberofPendingRemoteliteRejections":e.remoteLiteRejections
      }
    })
    this.date = new Date();
    this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_Fetch_Claims_${this.date}`, options);
    this.loader.exportCSVLoader=false;
  }

  logout() {
    Utils.logout();
  }

}
