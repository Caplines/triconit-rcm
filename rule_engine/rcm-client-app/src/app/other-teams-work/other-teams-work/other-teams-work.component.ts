import { Component, OnInit, LOCALE_ID, Inject, HostListener } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';
import { DecimalPipe, formatNumber } from '@angular/common';
import { DownLoadService } from 'src/app/service/download.service';

@Component({
  selector: 'app-other-teams-work',
  templateUrl: './other-teams-work.component.html',
  styleUrls: ['./other-teams-work.component.scss']
})
export class OtherTeamsWorkComponent implements OnInit {

  selectedBtype: number = 0;
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  isSorted: any = {};
  loader: any = { 'billingLoader': false, 'listClaimLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  showFilteredDropdown: any = { 'officeName': false };
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  isFilterAllSelected: any = { 'officeName': false };
  clientName: string = '';
  isFilterValueExist: boolean = false;
  fliterName: string = '';
  showModal: boolean = false;
  selectedFiles: File[] = [];
  totalFile: number = 0;

  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }

  constructor(@Inject(LOCALE_ID) private locale: string, private appService: ApplicationServiceService, public appConstants: AppConstants, private title: Title, private downloadService: DownLoadService) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    this.clientName = localStorage.getItem("selected_clientName");
    this.fetchClaims();
  }

  fetchOfficeByUuid() {
    this.appService.fetchOfficeByUuid((res: any) => {
      if (res.status) {
        res.data = res.data.map((e: any) => {
          return {
            ...e,
            "officeName": e.name,
          }
        })
        this.showFilterOptionOfficeName(res.data);
      }
    })
  }



  fetchClaims() {
    this.loader.listClaimLoader = true;
    let ths = this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, "Fresh", (res: any) => {
      if (res.status === 200) {
        ths.claimDetail = this.removePrefix(res.data);
        let data: any = ths.claimDetail.map((e: any) => {
          if (e.claimId.endsWith("_P")) {
            e['EstAmount'] = e.primeSecSubmittedTotal;
          } else {
            e['EstAmount'] = e.secTotal;
          }
          return e;
        })
        ths.claimDetail = data;
        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
      }
    });
  }

  removePrefix(data: any) {
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
        if (ele['claimId'].includes("_P")) {
          ele['claimType'] = "Primary"
        } else if (ele['claimId'].includes("_S")) {
          ele['claimType'] = "Secondary"
        }
      })
    });
    this.sortFiltereData(this.filteredOfficeName);
  }

  filterOfficeName(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.officeName = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredOfficeName.length; i++) {
        if (this.filteredOfficeName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.officeName = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
    }
  }


  addOrRemoveFilterStatus() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return checkbox.checked && checkbox['officeName'] === item['officeName'];
      });
    });
  }

  selectAll(event: any, filterProperty: any) {
    if (filterProperty == "officeName") {
      this.filteredOfficeName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterOfficeName("selectAll");
    }
  }

  sortFiltereData(filterValue: any) {
    filterValue.sort((a: any, b: any) => {
      const nameA = Object.keys(filterValue[0])[1] == 'insuranceType' ? a.insuranceType.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? a.insuranceName.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? a.officeName.toUpperCase() : '';
      const nameB = Object.keys(filterValue[0])[1] == 'insuranceType' ? b.insuranceType.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? b.insuranceName.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? b.officeName.toUpperCase() : '';
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }
      return 0;
    });

  }

  logout() {
    Utils.logout();
  }

  showHideFilteredDropdown(filterName: any) {
    filterName == 'officeName' ? this.showFilteredDropdown.officeName = true : this.showFilteredDropdown.officeName = false;
    this.fliterName = filterName;
  }

  get isRoleLead() {
    return Utils.isRoleLead();
  }

  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    if (this.selectedFiles.length == 0)
      document.getElementById("attach").innerHTML = 'Attach';
  }

  onFileChange(event: any) {
    for (let i = 0; i < event.target.files.length; i++) {
      this.selectedFiles.unshift(event.target.files[i]);
    }

  }

  uploadFiles() {
    if (this.selectedFiles.length > 0) {
      const formData = new FormData();
      for (const file of this.selectedFiles) {
        formData.append('files[]', file, file.name);
      }
    }
  }

  removeItem(file: File) {
    const index = this.selectedFiles.indexOf(file);
    if (index !== -1) {
      this.selectedFiles.splice(index, 1);
    }
  }

  uploadItem() {
    this.totalFile = this.selectedFiles.length;
    document.getElementById("attach").innerHTML = this.totalFile + ' Files Attached';
    this.closeModal();
  }
}
