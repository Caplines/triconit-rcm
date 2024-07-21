import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import Utils from '../../util/utils';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import { Title } from '@angular/platform-browser';
import { DownLoadService } from 'src/app/service/download.service';
import { AppConstants } from 'src/app/constants/app.constants';

import { DatePipe } from '@angular/common';
import { PmlDatePicker } from '../../shared/date-picker/datepicker-options';

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
  selectedDate: any = {'startDate': null, 'endDate': null};
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
  showFilteredDropdown: any = { 'officeName': false, 'companyName': false };
  isFilterAllSelected: any = { 'officeName': false, 'companyName': false };
  filteredCompanyName: any = [];
  filteredItems: any = [];
  filterName: any;
  selectedHeaders: string[];
  currentTeamId: any;

  constructor(private appService: ApplicationServiceService, private title: Title, private downloadService: DownLoadService, public constant: AppConstants, public datepipe: DatePipe, public pmlDatePicker: PmlDatePicker) {
    title.setTitle(Utils.defaultTitle + "Production");
  }

  ngOnInit(): void {
   
    this.clientName = localStorage.getItem("selected_clientName");
    this.currentTeamId = localStorage.getItem("selected_teamId");
    window.addEventListener("resize", this.setTopOnTotalRow);
    this.getCurrentTeamName();
  }

  getCurrentTeamName(){
   let teamName =  this.constant.teamData.find((e:any)=>e.teamId == Utils.selectedTeam());
       this.currentTeamName = teamName.unFormatedName;
  }

  clearAllTotals(){
    this.total = 0;
    this.days = 0;
    this.paymentPostingTotalConfig.total = 0;
    this.paymentPostingTotalConfig.days = 0;
    this.paymentPostingTotalConfig.amountPosted = 0;
    this.patientCallTotalConfig.paymentPromised = 0;
    this.patientCallTotalConfig.paymentMade = 0;
    this.patientCallTotalConfig.wrongNo = 0;
    this.patientCallTotalConfig.notReadyToPay = 0;
    this.patientCallTotalConfig.statementRequested = 0;
    this.patientStatTotalConfig.statement1 = 0;
    this.patientStatTotalConfig.statement2 = 0;
    this.patientStatTotalConfig.statement3 = 0;
    this.agingTotalConfig.countForAgeRange1 = 0;
    this.agingTotalConfig.countForAgeRange2 = 0;
    this.agingTotalConfig.countForAgeRange3 = 0;
    this.agingTotalConfig.countForAgeRange4 = 0;
    this.agingTotalConfig.billedCount = 0;
    this.agingTotalConfig.closedCount = 0;
    this.agingTotalConfig.pendingForBillingCount = 0;
    this.agingTotalConfig.pendingForReviewCount = 0;
    this.agingTotalConfig.reBillingCount = 0;
    this.agingTotalConfig.reviewedCount = 0;
    this.agingTotalConfig.voidedCount = 0;
    this.cdpTotalConfig.total = 0;
    this.cdpTotalConfig.days = 0;
  }

  save(){
  this.fetchbtnDisable=false;
  this.loader.showLoader=true;
  this.loader.fetch = true;
  this.isDataAvailable=true;
  this.clearAllTotals();
    this.appService.getProductionData({ "startDate": this.transformDate(this.selectedDate.startDate), "endDate": this.transformDate(this.selectedDate.endDate) }, (callback: any) => {
      if (callback.status == 200 && callback.data) {
        this.loader.showLoader = false;
        this.loader.fetch = false;
        this.fetchbtnDisable = false;
        if (this.currentTeamName === "AGING" || this.currentTeamName === 'CDP') {
          this.productionData = callback.data[0];
          console.log(this.productionData);
          this.showFilterOptioncompanyName(this.productionData);
          this.filterCompanyName();
          this.countTotalForAgingCdp();
        }
         else {
          this.productionData = callback.data;
          console.log(this.productionData);
          this.showFilterOptioncompanyName(this.productionData);
          this.filterCompanyName();
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
    let count = 0;
      if(this.currentTeamName === "AGING"){
          if(this.agingCategory == 'ageWise'){
            if (!this.productionData.listOfAgeWiseData) return;
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
      }
      else if (this.currentTeamName === "CDP") {
        if (this.cdpCategory == 'followUp') {
          if (!this.productionData.cdpForInsuranceFollowUp) return;
          this.productionData.cdpForInsuranceFollowUp.forEach((e: any) => {
            this.cdpTotalConfig.total = this.cdpTotalConfig.total + e.total;
            if (e.days == '0') {
              count++;
            } else {
              this.cdpTotalConfig.days = this.cdpTotalConfig.days + e.days;
            }
          });
          this.cdpTotalConfig.days = this.cdpTotalConfig.days / (this.productionData.cdpForInsuranceFollowUp.length - count);
        } 
        else {
          this.productionData.cdpForAppeal.forEach((e: any) => {
            this.cdpTotalConfig.total = this.cdpTotalConfig.total + e.total;
            if (e.days == '0') {
              count++;
            } else {
              this.cdpTotalConfig.days = this.cdpTotalConfig.days + e.days;
            }
          });
          this.cdpTotalConfig.days = this.cdpTotalConfig.days / (this.productionData.cdpForAppeal.length - count);
        }
      }
  }

  transformDate(date: Date) {
    return this.datepipe.transform(date, 'yyyy-MM-dd');
  }

  receiveChildEvent(event: any) {
    if (event['action'] === 'changeDatePicker') {
      if (event.model == 'startDate') {
        if (event.value != null)
          this.selectedDate['startDate'] = new Date(event.value);
        else
          this.selectedDate['startDate'] = null;
      }
      if (event.model == 'endDate') {
        if (event.value != null)
          this.selectedDate['endDate'] = new Date(event.value);
        else
          this.selectedDate['endDate'] = null;
      }
    }
    this.selectDate();
  }

  selectDate() {
    if (this.selectedDate.startDate && this.selectedDate.endDate) {
      if (this.selectedDate.startDate.getTime() <= this.selectedDate.endDate.getTime()) {
        this.fetchbtnDisable = true;
      } else {
        this.fetchbtnDisable = false;
      }
    } else {
      this.fetchbtnDisable = true;
    }
  }

  get staticUtil(): any {
    return Utils;
  }

  exportToCsv() {
    this.loader.exportCSVLoader = true;

    const headerConfigs = {
      default: [
        "Client",
        "Associate Name",
        "Total Production",
        "Average Productivity (Per Day)",
        this.staticUtil.isNotTeamPosting() ? "" : "Amount Posted",
        this.staticUtil.isNotTeamPatientStatement() ? "" : "Statement Type 1",
        this.staticUtil.isNotTeamPatientStatement() ? "" : "Statement Type 2",
        this.staticUtil.isNotTeamPatientStatement() ? "" : "Statement Type 3",
      ],
      agingAgeWise: [
        "Office Name",
        "0 - 30",
        "30 - 60",
        "60 - 90",
        "90+",
      ],
      agingClaimWise: [
        "Associate Name",
        "Billed Count",
        "Closed Count",
        "Pending For Billing Count",
        "Pending For Review Count",
        "Re Billing Count",
        "Reviewed Count",
        "Submitted Count",
        "Voided Count",
      ]
    }

    if (this.currentTeamName === "AGING") {
      if (this.agingCategory == 'ageWise') {
        this.selectedHeaders = headerConfigs.agingAgeWise;
      } else {
        this.selectedHeaders = headerConfigs.agingClaimWise;
      }
    }
    else {
      this.selectedHeaders = headerConfigs.default;
    }

    let options: any = {
      showLabels: true,
      headers: this.selectedHeaders
    }

    let excelData: any;
    if (this.currentTeamName === "AGING") {
      if (this.agingCategory == 'ageWise') {
        excelData = [...this.productionData.listOfAgeWiseData];
      } else {
        excelData = [...this.productionData.listOfCurrentStatusWiseData];
      }
    }
    else if (this.currentTeamName === "CDP") {
      if (this.cdpCategory == 'followUp') {
        excelData = [...this.productionData.cdpForInsuranceFollowUp];
      } else {
        excelData = [...this.productionData.cdpForAppeal];
      }
    } else {
      excelData = [...this.productionData];
    }
    excelData = excelData.map((e: any) => {
      if (e.cd) {
        let date: Date = new Date(e.cd);
        e = { ...e, cd: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` }
      }
      else {
        e = { ...e, cd: '' };
      }
      if (e.fname) {
        e['fullName'] = e.fname ? e.fname + " " + e.lname : "-";
      }
      return e;
    })

    if (this.currentTeamName === "AGING") {
      if (this.agingCategory == 'ageWise') {
        excelData = excelData.map((e: any) => {
          return {
            "Office Name": e.officeName,
            "0 - 30": e.countForAgeRange1,
            "30 - 60": e.countForAgeRange2,
            "60 - 90": e.countForAgeRange3,
            "90+": e.countForAgeRange4,
          }
        })
      } else {
        excelData = excelData.map((e: any) => {
          return {
            "Associate Name": e.associateName,
            "Billed Count": e.billedCount,
            "Closed Count": e.closedCount,
            "Pending For Billing Count": e.pendingForBillingCount,
            "Pending For Review Count": e.pendingForReviewCount,
            "Re Billing Count": e.reBillingCount,
            "Reviewed Count": e.reviewedCount,
            "Submitted Count": e.submittedCount,
            "Voided Count": e.voidedCount,
          }
        })
      }
    }
    else if (this.currentTeamName === "CDP") {
      excelData = excelData.map((e: any) => {
        return {
          "Client": e.companyName,
          "Associate Name": e.fullName,
          "Total Production": e.total,
          "Average Productivity (Per Day)": e.days,
        }
      })
      excelData.unshift({           //method is used to show Total Row in CSV.
        "Client": 'Total',
        "Associate Name": '-',
        "Total Production": this.cdpTotalConfig.total,
        "Average Production (Per Day)": this.cdpTotalConfig.days,
      })
    }
    else {
      excelData = excelData.map(({ uuid, fname, lname, cd, ...excelData }: any) => excelData) //to remove required properties in excel
      excelData = excelData.map((e: any) => {
        return {
          "Client": e.companyName,
          "Associate Name": e.fullName,
          "Total Production": e.total,
          "Average Productivity (Per Day)": e.days,
          "Amount Posted": this.staticUtil.isNotTeamPosting() ? "" : e.amountPosted,
          "Statement Type 1": this.staticUtil.isNotTeamPatientStatement() ? "" : e.statementType1,
          "Statement Type 2": this.staticUtil.isNotTeamPatientStatement() ? "" : e.statementType2,
          "Statement Type 3": this.staticUtil.isNotTeamPatientStatement() ? "" : e.statementType3,
        }
      })
      excelData.unshift({           //method is used to show Total Row in CSV.
        "OfficeName": 'Total',
        "ClientNme": '-',
        "Total Production": this.total,
        "Average Production (Per Day)": this.days,
        "Amount Posted": this.staticUtil.isNotTeamPosting() ? "" : this.paymentPostingTotalConfig.amountPosted,
        "Statement Type 1": this.staticUtil.isNotTeamPatientStatement() ? "" : this.patientStatTotalConfig.statement1,
        "Statement Type 2": this.staticUtil.isNotTeamPatientStatement() ? "" : this.patientStatTotalConfig.statement2,
        "Statement Type 3": this.staticUtil.isNotTeamPatientStatement() ? "" : this.patientStatTotalConfig.statement3,
      })
    }

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;

    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_Production_${this.date}`, options);
    this.loader.exportCSVLoader = false;

  }

  downloadPdf() {
    this.loader.exportPDFLoader = true;
    let data = {};
    if (this.productionData.length != 0) {
      if (this.currentTeamName === 'AGING') {
        data = { "fileName": "Production", "clientName": this.clientName, "currentTeamName": this.currentTeamName, "agingPdfDto": this.productionData, "tabSwitchForAging": this.agingCategory };
      } else if (this.currentTeamName === 'PAYMENT_POSTING') {
        data = { "fileName": "Production", "clientName": this.clientName, "currentTeamName": this.currentTeamName, "paymentPosting": this.filteredItems };
      }
      else if (this.currentTeamName === 'PATIENT_CALLING') {
        data = { "fileName": "Production", "clientName": this.clientName, "currentTeamName": this.currentTeamName, "patientCalling": this.productionData };
      }
      else if (this.currentTeamName === 'PATIENT_STATEMENT') {
        data = { "fileName": "Production", "patientStatement": this.filteredItems, "clientName": this.clientName, "currentTeamName": this.currentTeamName };
      }
      else if (this.currentTeamName === 'CDP') {
        data = { "fileName": "Production", "cdpPdfDto": this.productionData, "clientName": this.clientName, "currentTeamName": this.currentTeamName, "tabSwitchForCDP": this.cdpCategory };
      } else {
        data = { "fileName": "Production", "currentTeamName": this.currentTeamName, "data": this.productionData, "clientName": this.clientName };
      }

      this.appService.productionPdfDownload(data, "pdf", (res: any) => {
        if (res.status === 200) {
          this.date = new Date();
          this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
          console.log(res.body);
          this.downloadService.saveBolbData(res.body, `${localStorage.getItem("selected_clientName")}_Production_${this.date}.pdf`);
          this.loader.exportPDFLoader = false;
        } else {
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
    if (this.currentTeamName === "CDP" && this.cdpCategory === 'appeal') {
      this.appService.sortData(data.cdpForAppeal, sortProp, order, sortType);
    }
    else if (this.currentTeamName === "CDP" && this.cdpCategory === 'followUp') {
      this.appService.sortData(data.cdpForInsuranceFollowUp, sortProp, order, sortType);
    }
    else {
      this.appService.sortData(data, sortProp, order, sortType);
    }
  }


setTopOnTotalRow(){
  let thead:any =  document.querySelector("thead tr th")
  let totalRow:any = document.querySelector(".totalRow");
  if(totalRow){
    totalRow.style.top = thead.clientHeight+"px";
   }
 } 

  selectAgeCategory(category: any) {
    this.agingCategory = category;
    this.clearAllTotals();
    this.countTotalForAgingCdp();
  }

  selectCdpCategory(category: any) {
    this.cdpCategory = category;
    this.showFilterOptioncompanyName(this.productionData);
    this.filterCompanyName();
    this.clearAllTotals();
    this.countTotalForAgingCdp();
  }

  showHideFilteredDropdown(filterName: any) {
    filterName == 'companyName' ? this.showFilteredDropdown.companyName = true : this.showFilteredDropdown.companyName = false;
    this.filterName = filterName;
  }

  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.filterName] = false;
      }, 500);
    }
  }

  selectAll(event: any, filterProperty: any) {
    if (filterProperty == "companyName") {
      this.filteredCompanyName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e['checked'] = false;
        }
      });
      this.filterCompanyName("selectAll");
    }
  }

  filterCompanyName(e?: any, filterProperty?: any) {
    if (!this.productionData) return;
    if (!e) {
      this.filteredItems = JSON.parse(JSON.stringify(this.productionData));
      this.isFilterAllSelected.companyName = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredCompanyName.length; i++) {
        if (this.filteredCompanyName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      } 
      this.isFilterAllSelected.companyName = isAllSelected;
      if (this.currentTeamName === "CDP" && this.cdpCategory === 'appeal' ) {
        this.filteredItems.cdpForAppeal = this.productionData.cdpForAppeal.filter((item: any) => {
          return this.filteredCompanyName.some((checkbox: any) => {
            return checkbox.checked && item.companyName == checkbox.companyName;
          })
        })
        this.clearAllTotals();
        this.totalFilteredCount(this.filteredItems.cdpForAppeal);
      }
      else if (this.currentTeamName === "CDP" && this.cdpCategory === 'followUp') {
        this.filteredItems.cdpForInsuranceFollowUp = this.productionData.cdpForInsuranceFollowUp.filter((item: any) => {
          return this.filteredCompanyName.some((checkbox: any) => {
            return checkbox.checked && item.companyName == checkbox.companyName;
          })
        })
        this.clearAllTotals();
        this.totalFilteredCount(this.filteredItems.cdpForInsuranceFollowUp);
      } 
      else if (this.currentTeamName === "PATIENT_STATEMENT") {
        this.filteredItems = this.productionData.filter((item: any) => {
          return this.filteredCompanyName.some((checkbox: any) => {
            return checkbox.checked && item.clientName == checkbox.clientName;
          })
        })
        this.clearAllTotals();
        this.totalFilteredCount(this.filteredItems);
      } 
      else {
        this.filteredItems = this.productionData.filter((item: any) => {
          return this.filteredCompanyName.some((checkbox: any) => {
            return checkbox.checked && item.companyName == checkbox.companyName;
          })
        })
        this.clearAllTotals();
        this.totalFilteredCount(this.filteredItems);
      }
    }
  }
  // productionData.cdpForAppeal
  // productionData.cdpForInsuranceFollowUp;

  totalFilteredCount(data: any) {
    if (data.length > 0) {
      let count = 0;
      if (this.currentTeamName === "CDP"){
        this.cdpTotalConfig.total = 0;
        this.cdpTotalConfig.days = 0;
        data.forEach((e: any) => {
          this.cdpTotalConfig.total += e.total;
          if(e.days == '0'){
            count++;
          } else {
            this.cdpTotalConfig.days += e.days;
          }
        })
        this.cdpTotalConfig.days = this.cdpTotalConfig.days / (data.length - count);
      } 
      else if (this.currentTeamName === "PAYMENT_POSTING"){
        this.paymentPostingTotalConfig.total = 0;
        this.paymentPostingTotalConfig.days = 0;
        this.paymentPostingTotalConfig.amountPosted = 0;
        data.forEach((e: any) => {
          this.paymentPostingTotalConfig.total += e.total;
          this.paymentPostingTotalConfig.amountPosted += e.totalAmountReceivedInBank;
          if(e.days == '0'){
            count++;
          } else {
            this.paymentPostingTotalConfig.days += e.days;
          }
        })
        this.paymentPostingTotalConfig.days = this.paymentPostingTotalConfig.days / (data.length - count);

      }
      else if (this.currentTeamName === "PATIENT_STATEMENT"){
        this.total = 0;
        this.days = 0;
        this.patientStatTotalConfig.statement1 = 0;
        this.patientStatTotalConfig.statement2 = 0;
        this.patientStatTotalConfig.statement3 = 0;
        data.forEach((e: any) => {
          this.total += e.total;
          this.patientStatTotalConfig.statement1 += e.statementType.statementType1;
          this.patientStatTotalConfig.statement2 += e.statementType.statementType2;
          this.patientStatTotalConfig.statement3 += e.statementType.statementType3;
          if (e.days == '0') {
            count++;
          } else {
            this.days += e.days;
          }
        })
        this.days = this.days / (data.length - count);
      }
      else{
        this.total = 0;
        this.days = 0;
        data.forEach((e: any) => {
          this.total += e.total;
          if (e.days == '0') {
            count++;
          } else {
            this.days += e.days;
          }
        })
        this.days = this.days / (data.length - count);
      }
    }
  }

  showFilterOptioncompanyName(data: any) {
    if (!this.productionData) return;
    this.filteredCompanyName = JSON.parse(JSON.stringify(data));
    if (!this.filteredCompanyName) return;
    const newArray: any = [];
    const seencompanyNames: any = {};
    if (this.currentTeamName === "AGING") {
      if (this.agingCategory == 'ageWise') {
        if (this.filteredCompanyName.listOfAgeWiseData.length == 0) return;
        this.filteredCompanyName.listOfAgeWiseData.forEach((item: any) => {
          if (!seencompanyNames.hasOwnProperty(item.officeName)) {
            seencompanyNames[item.officeName] = true;
            newArray.push({ 'checked': true, 'officeName': item.officeName });
          }
        });
      }
      else {
        if (this.filteredCompanyName.listOfCurrentStatusWiseData.length == 0) return;
        this.filteredCompanyName.listOfCurrentStatusWiseData.forEach((item: any) => {
          if (!seencompanyNames.hasOwnProperty(item.officeName)) {
            seencompanyNames[item.officeName] = true;
            newArray.push({ 'checked': true, 'officeName': item.officeName });
          }
        });
      }
    }
    else if (this.currentTeamName === 'CDP') {
      if (this.cdpCategory == 'followUp') {
        if (this.filteredCompanyName.cdpForInsuranceFollowUp.length == 0) return;
        this.filteredCompanyName.cdpForInsuranceFollowUp.forEach((item: any) => {
          if (!seencompanyNames.hasOwnProperty(item.companyName)) {
            seencompanyNames[item.companyName] = true;
            newArray.push({ 'checked': true, 'companyName': item.companyName });
          }
        });
      }
      else {
        if (this.filteredCompanyName.cdpForAppeal.length == 0) return;
        this.filteredCompanyName.cdpForAppeal.forEach((item: any) => {
          if (!seencompanyNames.hasOwnProperty(item.companyName)) {
            seencompanyNames[item.companyName] = true;
            newArray.push({ 'checked': true, 'companyName': item.companyName });
          }
        });
      }
    } 
    else if (this.currentTeamName === "PATIENT_STATEMENT") {
      this.filteredCompanyName.forEach((item: any) => {
        if (!seencompanyNames.hasOwnProperty(item.clientName)) {
          seencompanyNames[item.clientName] = true;
          newArray.push({ 'checked': true, 'clientName': item.clientName });
        }
      });
    } 
    else{
      this.filteredCompanyName.forEach((item: any) => {
        if (!seencompanyNames.hasOwnProperty(item.companyName)) {
          seencompanyNames[item.companyName] = true;
          newArray.push({ 'checked': true, 'companyName': item.companyName });
        }
      });
    }
    this.filteredCompanyName = newArray;
    console.log(newArray);
    if (this.currentTeamName == "AGING" ){
      this.sortFilteredData(this.filteredCompanyName, 'officeName');
    } else {
      this.sortFilteredData(this.filteredCompanyName, 'companyName');
    }
  }

  sortFilteredData(filterValue: any, sortBy: any) {
    filterValue.sort((a: any, b: any) => {
      return a[sortBy].localeCompare(b[sortBy]);
    });
  }
}