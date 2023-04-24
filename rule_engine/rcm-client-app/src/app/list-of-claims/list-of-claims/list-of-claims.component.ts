import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateLogModel } from '../../models/claim-associate-log-model';
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

  constructor(private appService: ApplicationServiceService, public appConstants: AppConstants,private title:Title) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle("List Of Claims");
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
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox.name === item.officeName;
        });
      });
    }
  }

  saveToPdf(divName: any) {
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      pdf.addImage(content, "PNG", 0, 0, width, height)
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("cname")}_List_of_Claims_${this.date}`);
    });
  }

  exportToCsv() {
    let options: any = {
      showLabels: true,
      headers: ["Last Team","Timely Filing Limit (Days)", 'Date of Service', "Patient Name", "Office Name", "Patient ID", "Claim Age","Insurance Type", "Insurance Name", "Claim Type",  "Estimated Amount", "Action Required",]
    }
    let excelData: any;
    excelData = [...this.filteredItems];
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
    })

    excelData = excelData.map(
      ({ claimId,claimType ,opdos, opdt, secName, secTotal, uuid, billedAmount, statusType,primTotal,  ...newClaimData }: any) => newClaimData);
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(excelData.sort());
      
    // new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
  }

  logout() {
    Utils.logout();
  }
}
