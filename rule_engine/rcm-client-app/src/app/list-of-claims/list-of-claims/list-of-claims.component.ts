import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateLogModel } from '../../models/claim-associate-log-model';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';

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

  constructor(private appService: ApplicationServiceService, public appConstants: AppConstants) {
    this.selectedBtype = this.appConstants.BILLING_ID;

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
        ths.claimDetail = res.data;
        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();

      } else {
        //ERROR
      }

    });
  }

  sortData(data: any, sortProp: string, order: any, sortType: string) {
    this.appService.sortData(data, sortProp, order, sortType);
  }

  showFilterOptionOfficeName(data: any) {
    this.filteredOfficeName = data;
    this.filteredOfficeName.forEach((e: any) => {
      this.claimDetail.forEach((ele: any) => {
        if (e.name == ele.officeName) {
          e['checked'] = !e['checked'];
        }
      })
    });
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
      pdf.save("List-of-Claims.pdf")
    });
  }

  exportToCsv() {
    let options: any = {
      showLabels: true,
      headers: ["Office Name", "Patient ID", 'Date of Service', "Timely Filing Limit (Days)", "Patient Name", "Insurance Name", "Claim Age", "Insurance Type", "Estimated Amount", "Action Required", "Claim Type"]
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
      return e;
    })

    excelData = excelData.map(
      ({ claimId, lastTeam, opdos, opdt, secName, secTotal, uuid, secondaryInsurance, billedAmount, statusType, ...newClaimData }: any) => newClaimData);
    new ngxCsv(excelData, 'List-of-Claims', options);
  }
}
