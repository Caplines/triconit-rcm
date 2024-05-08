import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import Utils from '../../util/utils';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { Title } from '@angular/platform-browser';
import { DownLoadService } from 'src/app/service/download.service';
import { AppConstants } from 'src/app/constants/app.constants';


@Component({
  selector: 'app-production-component',
  templateUrl: './production.component.html',
  styleUrls: ['./production.component.css'],
  encapsulation:ViewEncapsulation.None
})
export class ProductionComponent implements OnInit {

  productionData:any=[];
  alert:any={'showAlertPopup':false,'alertMsg':''};
  total:any=0;
  days:any=0;
  date:any;
  selectedDate:any={'startDate':'','endDate':''};
  loader:any= {'showLoader':false,'exportPDFLoader':false,'exportCSVLoader':false,'fetch':false};
  isDataAvailable:boolean=false;
  clientName:string='';
  currentTeamName:any='';
  fetchbtnDisable=true;
  isSorted:any={};
  agingCategory:any='ageWise';
  agingTotalConfig:any={
    countForAgeRange1:0,
    countForAgeRange2:0,
    countForAgeRange3:0,
    countForAgeRange4:0,

    billedCount:0,
    closedCount:0,
    pendingForBillingCount:0,
    reBillingCount:0,
    reviewedCount:0,
    submittedCount:0,
    voidedCount:0,
  };
  cdpCategory:any='followUp';
  cdpTotalConfig:any={  
    total:0,
    days:0
  }
  patientStatTotalConfig:any={statement1:0,statement2:0,statement3:0};
  patientCallTotalConfig:any={paymentPromised:0,paymentMade:0,wrongNo:0,notReadyToPay:0,statementRequested:0};
  paymentPostingTotalConfig:any={total:0,days:0,amountPosted:0};
  

  constructor(private appService: ApplicationServiceService,private title:Title,private downloadService:DownLoadService,public constant:AppConstants) { 
     title.setTitle(Utils.defaultTitle + "Production");
     
    }

  ngOnInit(): void {
   
    this.clientName = localStorage.getItem("selected_clientName");
    window.addEventListener("resize", this.setTopOnTotalRow);
    this.getCurrentTeamName();
  }

  getCurrentTeamName(){
   let teamName =  this.constant.teamData.find((e:any)=>e.teamId == Utils.selectedTeam());
       this.currentTeamName = teamName.unFormatedName;
  }

  save(){
  this.fetchbtnDisable=false;
  this.loader.showLoader=true;
  this.loader.fetch = true;
  this.isDataAvailable=true;
  this.total=0;
  this.days=0;
    this.appService.getProductionData({ "startDate": this.selectedDate.startDate, "endDate": this.selectedDate.endDate }, (callback: any) => {
      if (callback.status == 200 && callback.data) {
        this.loader.showLoader = false;
        this.loader.fetch = false;
        this.fetchbtnDisable = false;
        if (this.currentTeamName === "AGING" || this.currentTeamName === 'CDP') {
          this.productionData = callback.data[0];
          this.countTotalForAgingCdp();
        }
         else {
          this.productionData = callback.data;
          this.countTotalForTeamExceptAgingCdp();
        }
        if (this.productionData.length == 0) {
          this.loader.showLoader = false;
          this.isDataAvailable = true;
        }
        this.fetchbtnDisable = true;
           
      } else this.alert.alertMsg = callback.message ? callback.message : 'Something went wrong';
    });
    
 }

  countTotalForTeamExceptAgingCdp() {
    let count = 0;
    this.productionData.forEach((x: any) => {
      this.total = this.total + x.total;
      if (x.days == '0') {
        count++;
      } else {
        this.days = this.days + x.days;
      }
    });
    this.days = this.days / (this.productionData.length - count);          //calucating average

    if (this.productionData.length > 0) {
      this.sortAvgDays();
    }

    if(this.currentTeamName == "PATIENT_STATEMENT"){
        this.countTotalForPatientStatment();
    }
    else if(this.currentTeamName == "PATIENT_CALLING"){
      this.countTotalForPatientCalling();
    }
    else if(this.currentTeamName == "PAYMENT_POSTING"){
      this.countTotalForPaymentPosting();
    } 
  }

  countTotalForPaymentPosting(){
    this.productionData.forEach((e: any) => {
      this.paymentPostingTotalConfig.total = this.paymentPostingTotalConfig.total + e.total;
      this.paymentPostingTotalConfig.days = this.paymentPostingTotalConfig.days + e.days;
      this.paymentPostingTotalConfig.amountPosted = this.paymentPostingTotalConfig.amountPosted + e.totalAmountReceivedInBank;
  })
  }

  countTotalForPatientCalling(){
    this.productionData.forEach((e: any) => {
      this.patientCallTotalConfig.paymentPromised = this.patientCallTotalConfig.paymentPromised + e.paymentPromised;
      this.patientCallTotalConfig.paymentMade = this.patientCallTotalConfig.paymentMade + e.paymentMade;
      this.patientCallTotalConfig.wrongNo = this.patientCallTotalConfig.wrongNo + e.wrongNo;
      this.patientCallTotalConfig.notReadyToPay = this.patientCallTotalConfig.notReadyToPay + e.notReadyToPay;
      this.patientCallTotalConfig.statementRequested = this.patientCallTotalConfig.statementRequested + e.statementRequested;
})
  }

  countTotalForPatientStatment(){
    this.productionData.forEach((e: any) => {
          this.patientStatTotalConfig.statement1 = this.patientStatTotalConfig.statement1 + e.statementType.statementType1;
          this.patientStatTotalConfig.statement2 = this.patientStatTotalConfig.statement2 + e.statementType.statementType2;
          this.patientStatTotalConfig.statement3 = this.patientStatTotalConfig.statement3 + e.statementType.statementType3;
    })
  }

  countTotalForAgingCdp(){
      if(this.currentTeamName === "AGING"){
          if(this.agingCategory == 'ageWise'){
            this.productionData.listOfAgeWiseData.forEach((e:any)=>{
                this.agingTotalConfig.countForAgeRange1 = this.agingTotalConfig.countForAgeRange1 + e.countForAgeRange1;
                this.agingTotalConfig.countForAgeRange2 = this.agingTotalConfig.countForAgeRange2 + e.countForAgeRange2;
                this.agingTotalConfig.countForAgeRange3 = this.agingTotalConfig.countForAgeRange3 + e.countForAgeRange3;
                this.agingTotalConfig.countForAgeRange4 = this.agingTotalConfig.countForAgeRange4 + e.countForAgeRange4;
              });
            }else{
              this.productionData.listOfCurrentStatusWiseData.forEach((e:any)=>{
                this.agingTotalConfig.billedCount = this.agingTotalConfig.billedCount + e.billedCount;
                this.agingTotalConfig.closedCount = this.agingTotalConfig.closedCount + e.closedCount;
                this.agingTotalConfig.pendingForBillingCount = this.agingTotalConfig.pendingForBillingCount + e.pendingForBillingCount;
                this.agingTotalConfig.pendingForReviewCount = this.agingTotalConfig.pendingForReviewCount + e.pendingForReviewCount;
                this.agingTotalConfig.reBillingCount = this.agingTotalConfig.reBillingCount + e.reBillingCount;
                this.agingTotalConfig.reviewedCount = this.agingTotalConfig.reviewedCount + e.reviewedCount;
                this.agingTotalConfig.voidedCount = this.agingTotalConfig.voidedCount + e.voidedCount;
            });
          }
      }else if(this.currentTeamName === "CDP"){
        if(this.cdpCategory == 'followUp'){
              this.productionData.cdpForInsuranceFollowUp.forEach((e:any)=>{
                  this.cdpTotalConfig.total = this.cdpTotalConfig.total + e.total;
                  this.cdpTotalConfig.days = this.cdpTotalConfig.days + e.days;
              });
        }else{
          this.productionData.cdpForAppeal.forEach((e: any) => {
            this.cdpTotalConfig.total = this.cdpTotalConfig.total + e.total;
            this.cdpTotalConfig.days = this.cdpTotalConfig.days + e.days;
          });
        }
      }
  }

 selectDate(event:any,from:any){
  if(from == 'start') this.selectedDate.startDate = event.target.value ;
  if(from=='end') this.selectedDate.endDate = event.target.value;
  this.fetchbtnDisable=true;
  console.log(this.selectedDate.startDate,this.selectedDate.endDate );
  
  
 }

exportToCsv() {
  this.loader.exportCSVLoader=true;
  let options: any = {
    showLabels: true,
    headers: ["Client","Associate Name","Total Production", "Average Production (Per Day)"]
  }
  let excelData: any;
  excelData = [...this.productionData];
  excelData = excelData.map((e: any) => {
    if (e.cd) {
      let date: Date = new Date(e.cd);
      e = { ...e, cd: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` }
    }
    else {
      e = {...e,cd : ''};
    }
    if(e.fname){
      e['fullName'] = e.fname ? e.fname+ " "+ e.lname : "-";
    }
    return e;
  })
  excelData = excelData.map(({uuid,fname,lname,cd,...excelData }: any) => excelData) //to remove required properties in excel
  excelData = excelData.map((e:any)=>{
    return{
      "Client":e.companyName,
      "Associate Name":e.fullName,
      "Total Production":e.total,
      "Average Production (Per Day)":e.days,
    }
  })
  excelData.unshift(                                        //method is used to show Total Row in CSV.
  {
    "OfficeName":'Total',
    "ClientNme":'-',
    "Total Production":this.total,
    "Average Production (Per Day)":this.days
  }
) 
  this.date = new Date();
  this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
  
  new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_Production_${this.date}`, options);
  this.loader.exportCSVLoader=false;

}

downloadPdf(){
  this.loader.exportPDFLoader = true;
  if(this.productionData.length!=0){
  let data = {"fileName":"Production","data": this.productionData,"clientName": this.clientName};
  this. appService.productionPdfDownload(data,"pdf",(res: any) => {
    if (res.status === 200){
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(res.body);
      this.downloadService.saveBolbData(res.body, `${localStorage.getItem("selected_clientName")}_Production_${this.date}.pdf`);
      this.loader.exportPDFLoader = false;
    }else{
      console.log("something went wrong");
      this.loader.exportPDFLoader = false;
    }
  })
}
}

sortAvgDays(){
  this.isSorted['days'] =true;
    this.sortData(this.productionData,'days','asc','number');
}

sortData(data: any, sortProp: string, order: any, sortType: string) {
  this.appService.sortData(data, sortProp, order, sortType);
}


setTopOnTotalRow(){
  let thead:any =  document.querySelector("thead tr th")
  let totalRow:any = document.querySelector(".totalRow");
  if(totalRow){
    totalRow.style.top = thead.clientHeight+"px";
   }
 } 

 selectAgeCategory(category:any){
  this.agingCategory = category;
 }

 selectCdpCategory(category:any){
  this.cdpCategory = category;
 }

}



