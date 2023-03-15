import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateLogModel } from '../../models/claim-associate-log-model';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';

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
  loader:any={'billingLoader':false,'listClaimLoader':false};
  totalClaimData:any={'oldestOpdt':'','oldestOpdos':'','totalCount':0,'totalRemLiteReject':0}

    constructor(private appService: ApplicationServiceService,public appConstants: AppConstants) {
    this.selectedBtype=this.appConstants.BILLING_ID;
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

  saveToPdf(divName: any) {
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      pdf.addImage(content, "PNG", 0, 0, width, height)
      pdf.save("Fetch-Claims.pdf")
    });
  }

  exportToCsv() {
    let options: any = {
      showLabels: true,
      headers: ["Office Name","Oldest Pending Date","Oldest Pending DOS",`${this.selectedBtype==this.appConstants.BILLING_ID? 'Number of Pending Fresh Cases' : 'Number of Pending Rebilling Cases'}`, "Number of Pending Remotelite Rejections"]
    }
    let excelData: any;
    excelData= [...this.log];  
    excelData = excelData.map((e:any)=>{
      if(e.opdos){
        let date:Date = new Date(e.opdos);
        e = {...e,opdos : `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`};
      } else {
        e = {...e,opdos:''};
      }
      if(e.opdt){
        let date:Date = new Date(e.opdt);
        e = {...e,opdt : `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`};
      } else {
        e = {...e,opdt:''};
      }
      return e;
    })

    excelData = excelData.map(({officeUuid,...newData}:any)=>newData)
    new ngxCsv(excelData, 'Fetch-Claims', options);
  }

}
