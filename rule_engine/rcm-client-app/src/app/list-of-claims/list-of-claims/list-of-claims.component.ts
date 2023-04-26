import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-list-of-claims',
  templateUrl: './list-of-claims.component.html',
  styleUrls: ['./list-of-claims.component.scss']
})
export class ListOfClaimsComponent implements OnInit {

  selectedBtype: number = 0;
  selectedSubtype: string = "Fresh";
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  switchBox: any = { 'billing': true, 'reBilling': false };
  isSorted: boolean = false;
  loader: any = { 'billingLoader': false, 'listClaimLoader': false };
  showFilteredDropdown: boolean = false;
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  date:any;
  isFilterAllSelected:boolean=true;

  constructor(private appService: ApplicationServiceService, public appConstants: AppConstants,private title:Title) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    this.fetchClaims(this.selectedSubtype);
  }

  fetchOfficeByUuid() {
    this.appService.fetchOfficeByUuid((res: any) => {
      if (res.status) {
        this.showFilterOptionOfficeName(res.data);
      }
    })
  }



  fetchClaims(subType: string) {
    this.loader.listClaimLoader = true;
    let ths = this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, subType, (res: any) => {
      if (res.status === 200) {
        ths.claimDetail =  this.removePrefix(res.data);
        // ths.claimDetail =  res.data;
        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();

      } 
      // else {
      //   this.loader.listClaimLoader = false;
      //   if(res.data == "not Autorized")
      //   this.logout();
      //   //ERROR
      // }

    });
  }

  removePrefix(data:any){
    const arr: any = data;

    const claimIdCounts = new Map();
    const result = [];

    for (const obj of arr) {
      const claimId = obj.claimId.slice(0, -2); // remove the "_P" or "_S" suffix
      const count = claimIdCounts.get(claimId) || 0;
      claimIdCounts.set(claimId, count + 1);
    }

    for (const obj of arr) {
      const claimId = obj.claimId.slice(0, -2); // remove the "_P" or "_S" suffix
      const count = claimIdCounts.get(claimId);
      if (count > 1 && obj.claimId.endsWith("_P")) {
        result.push(obj);
      } else if (count === 1) {
        result.push(obj);
      }
    }
    // this.claimDetail = result;
    return result;
  }

  sortData(data: any, sortProp: string, order: any, sortType: string) {
    this.appService.sortData(data, sortProp, order, sortType);
  }

  showFilterOptionOfficeName(data: any) {
    this.filteredOfficeName = data;
    this.filteredOfficeName.forEach((e: any) => {
      this.claimDetail.forEach((ele: any) => {
          e['checked'] = true;
          if(ele['claimId'].includes("_P")){
            ele['claimType'] ="Primary" 
          }else if(ele['claimId'].includes("_S")){
            ele['claimType']="Secondary"
          }
      })
    });
    console.log(this.claimDetail);
    
  }

  filterOfficeName(e?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredOfficeName.length; i++) {
        if (this.filteredOfficeName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox.name === item.officeName;
        });
      });

    }
    
  }

  saveToPdf(divName: any) {
    let m:any=document.querySelector(".table-wrapper-scroll-y");
    m.classList.remove('table-wrapper-scroll-y');
    m.classList.remove('table-inner-scrollbar');
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      pdf.addImage(content, "PNG", 0, 0, width, height)
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`);
      m.classList.add('table-wrapper-scroll-y');
      m.classList.add('table-inner-scrollbar');
    });
  }

  exportToCsv() {
    let options: any = {
      showLabels:true,
      headers: ["Office", "Patient ID", "Patient Name",'Date of Service',  "Claim Age","Timely Filing Limit (Days)", "Claim Type","Action Required", "Insurance Name","Insurance Type",  "Estimated Amount","Last Team that Worked on this claim", ]
    }
    let excelData: any;
    excelData = [...this.filteredItems];  //creating a copy of data so that nothing affects original data.
    excelData = excelData.map((e: any) => {
      if (e.dos) {
        let date: Date = new Date(e.dos);
        e = { ...e, dos: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` };
      }
      else {
        e = { ...e, dos: '' };
      }
      if (e.statusType == this.appConstants.BILLING_ID) {
        e = { ...e, ['actionRequired']: "BILLING" };
      } else {
        e = { ...e, ['actionRequired']: "RE-BILLING" };
      }
      if (e.claimId.endsWith("_P")) {
        e = { ...e, ['claimType']: "Primary" };
      } else {
        e = { ...e, ['claimType']: "Secondary" };
      }
      if(e.lastTeam == null){
        e = {...e,lastTeam:'-'}
      }
      return e;
    })      //method add value as "-" or "0", if its empty or null.

    excelData = excelData.map(
      ({ claimId,opdos, opdt, secTotal, uuid, billedAmount, statusType,  ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.

      excelData = excelData.map((e:any)=>{
        return{
          "Office Name":e.officeName,
          "Patient ID":e.patientId,
          "Patient Name":e.patientName,
          'Date of Service':e.dos,
          "Claim Age":e.claimAge,
          "Timely Filing Limit (Days)":e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          "Claim Type":e.claimType,
          "Action Required":e.actionRequired,
          "Insurance Name":e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type":e.prName? e.prName : e.secName,
          "Estimated Amount": e.claimId?.endsWith("_P") ? (e.primTotal ? e.primTotal : "0") : e.secTotal ? e.secTotal : "0",
          'Last Team':e.lastTeam,
        }
      })  //method aligns the header to the value in CSV.

      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(excelData.sort());
      new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
  }


  selectAll(event:any){
    if(event.target.value == "selectAll"){
      this.filteredOfficeName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
    }
    this.filterOfficeName("selectAll");
  }

  logout() {
    Utils.logout();
  }
}
