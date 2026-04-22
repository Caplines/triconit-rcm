import { Component, ElementRef, HostListener, Inject, LOCALE_ID, OnInit, ViewChild } from '@angular/core';
import { ClaimAssociateDetailModel } from '../models/claim-associate-detail-model';
import { TeamsM, TLUser } from '../models/claim-rcm-data-model';
import { Title } from '@angular/platform-browser';
import { AppConstants } from '../constants/app.constants';
import { ApplicationServiceService } from '../service/application-service.service';
import { DownLoadService } from '../service/download.service';
import Utils from '../util/utils';
import { formatNumber } from '@angular/common';
import { ngxCsv } from 'ngx-csv';

@Component({
  selector: 'app-admin-billing-claims',
  templateUrl: './admin-billing-claims.component.html',
  styleUrls: ['./admin-billing-claims.component.scss']
})
export class AdminBillingClaimsComponent implements OnInit {

  selectedBtype: number = 0;
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  switchBox: any = { 'billing': true, 'reBilling': false };
  isSorted: any = {};
  loader: any = { 'billingLoader': false, 'listClaimLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false, 'assignLoader': false };
  showFilteredDropdown: any = { 'officeName': false, 'claimType': false, 'insuranceType': false, 'insuranceName': false, 'lastTeamWorked': false, 'actionRequired': false, 'ageBracket': false, 'assignedToTeam': false, 'assignedTo': false };
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  date: any;
  modelElement: any = { 'modal': '', 'span': '' }
  @ViewChild('reassignmentSelectBox') reassignmentSelectBox!: ElementRef;
  teamsMs: Array<TeamsM>;
  assgnmentUsers: Array<TLUser>;
  @ViewChild('assignreason') assignreason!: ElementRef;

  isFilterAllSelected: any = { 'officeName': false, 'claimType': false, 'insuranceType': false, 'insuranceName': false, 'lastTeamWorked': false, 'actionRequired': false, 'ageBracket': false, 'assignedToTeam': false, 'assignedTo': false };

  filteredColumnData: any = { insuranceName: [], insuranceType: [], actionRequired: [], lastTeamWorked: [], ageBracket: [], claimType: [], assignedToTeam: [], assignedTo: [] };
  clientName: string = '';
  isFilterValueExist: boolean = false;
  isLastTeam: boolean = false;
  fliterName: string = '';
  assignmentTypeSelect: number = -1;
  tabValue: any;
  accessToListOfClaims: any;
  currentTeamId: number;
  showTooltipConfig: any = {};
  selectedHeaders: string[];
  listofClaimsForSubmission: any = [];
  selectedOfficeNames: Array<string> = [];
  accessAdminBillingClaims: boolean = false;
  companyData: any = [];
  showLoader: boolean = false;
  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  selectedCompanyUuid: any = "";
  selectedClientName: string = "";
  /** Client-side pagination over `filteredItems` (API returns the full list for the selected client). */
  listPageIndex = 0;
  listPageSize = 50;
  readonly listPageSizeOptions: number[] = [20, 50, 100, 200];
  private readonly ADMIN_LIST_PAGE_KEY = 'admin_billing_list_page_size';
  listGoToPageInput = 1;
  exportModalOpen = false;
  exportModalKind: 'csv' | 'pdf' = 'csv';
  exportScopeAll = true;
  exportRowLimitInput = 1000;
  exportLoading = false;

  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }

  constructor(@Inject(LOCALE_ID) private locale: string, private appService: ApplicationServiceService, public appConstants: AppConstants, private title: Title, private downloadService: DownLoadService) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "Admin Billing Claims");
  }

  ngOnInit() {
    const saved = localStorage.getItem(this.ADMIN_LIST_PAGE_KEY);
    if (saved) {
      const p = parseInt(saved, 10);
      if (this.listPageSizeOptions.includes(p)) {
        this.listPageSize = p;
      }
    }
    this.clientName = localStorage.getItem('selected_clientName') || '';
    this.currentTeamId = Utils.selectedTeam();
    this.getcompanyData();
  }

  private resetAdminListPagination(): void {
    this.listPageIndex = 0;
    this.listGoToPageInput = 1;
  }

  /** Keep current page valid when filters/sort change the row count. */
  private ensureListPageInRange(): void {
    const tp = this.listTotalPages;
    if (tp === 0) {
      this.listPageIndex = 0;
      this.listGoToPageInput = 1;
      return;
    }
    if (this.listPageIndex >= tp) {
      this.listPageIndex = tp - 1;
    }
    this.listGoToPageInput = this.listPageIndex + 1;
  }

  get listFilteredTotal(): number {
    return this.filteredItems.length;
  }

  get listTotalPages(): number {
    const n = this.filteredItems.length;
    if (n === 0) {
      return 0;
    }
    return Math.max(1, Math.ceil(n / this.listPageSize));
  }

  get pagedFilteredItems(): any[] {
    const start = this.listPageIndex * this.listPageSize;
    return this.filteredItems.slice(start, start + this.listPageSize);
  }

  get listPageStartItem(): number {
    if (this.filteredItems.length === 0) {
      return 0;
    }
    return this.listPageIndex * this.listPageSize + 1;
  }

  get listPageEndItem(): number {
    if (this.filteredItems.length === 0) {
      return 0;
    }
    return Math.min((this.listPageIndex + 1) * this.listPageSize, this.filteredItems.length);
  }

  get listPageNumbers(): number[] {
    const total = this.listTotalPages;
    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i);
    }
    const current = this.listPageIndex;
    const pages = new Set<number>([0, total - 1, current]);
    if (current > 1) {
      pages.add(current - 1);
    }
    if (current < total - 2) {
      pages.add(current + 1);
    }
    return Array.from(pages).sort((a, b) => a - b);
  }

  onListPageChange(page: number): void {
    if (page < 0 || page >= this.listTotalPages || page === this.listPageIndex) {
      return;
    }
    this.listPageIndex = page;
    this.listGoToPageInput = page + 1;
  }

  onListPageSizeChange(size: number | string): void {
    const n = typeof size === 'number' && !Number.isNaN(size) ? size : parseInt(String(size), 10);
    if (!this.listPageSizeOptions.includes(n)) {
      return;
    }
    this.listPageSize = n;
    localStorage.setItem(this.ADMIN_LIST_PAGE_KEY, String(n));
    this.resetAdminListPagination();
  }

  goToListSpecificPage(): void {
    if (this.listTotalPages === 0) {
      return;
    }
    const targetPage = Number(this.listGoToPageInput);
    if (!Number.isFinite(targetPage)) {
      this.listGoToPageInput = this.listPageIndex + 1;
      return;
    }
    const normalizedPage = Math.max(1, Math.min(targetPage, this.listTotalPages));
    this.listGoToPageInput = normalizedPage;
    this.onListPageChange(normalizedPage - 1);
  }

  onListPageEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.goToListSpecificPage();
    }
  }

  openExportModal(kind: 'csv' | 'pdf'): void {
    this.exportModalKind = kind;
    this.exportScopeAll = true;
    this.exportRowLimitInput = this.listFilteredTotal > 0 ? Math.min(1000, this.listFilteredTotal) : 1;
    this.exportModalOpen = true;
  }

  closeExportModal(): void {
    this.exportModalOpen = false;
  }

  confirmExport(): void {
    if (this.listFilteredTotal === 0) {
      return;
    }
    const kind = this.exportModalKind;
    const maxRows = this.exportScopeAll
      ? this.listFilteredTotal
      : Math.min(
          this.listFilteredTotal,
          Math.max(1, parseInt(String(this.exportRowLimitInput), 10) || 1)
        );
    this.closeExportModal();
    this.exportLoading = true;
    if (kind === 'csv') {
      this.loader.exportCSVLoader = true;
    } else {
      this.loader.exportPDFLoader = true;
    }
    const rows = this.filteredItems.slice(0, maxRows);
    Promise.resolve()
      .then(() => {
        if (kind === 'csv') {
          this.buildAndDownloadCsvFromRows(rows);
          this.loader.exportCSVLoader = false;
          this.exportLoading = false;
        } else {
          return this.downloadPdfWithRows(rows);
        }
      })
      .then(() => {
        if (kind === 'pdf') {
          this.loader.exportPDFLoader = false;
          this.exportLoading = false;
        }
      })
      .catch(() => {
        this.loader.exportCSVLoader = false;
        this.loader.exportPDFLoader = false;
        this.exportLoading = false;
      });
  }

  private downloadPdfWithRows(rows: any[]): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!rows || rows.length === 0) {
        resolve();
        return;
      }
      const data = {
        fileName: 'List_Of_Claims',
        data: rows,
        clientName: this.clientName,
        tabSwitch: this.tabValue,
        currentTeamId: this.currentTeamId
      };
      this.appService.lisOfClaimsPdfDownload(data, 'pdf', (res: any) => {
        if (res.status === 200) {
          this.date = new Date();
          this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
          this.downloadService.saveBolbData(
            res.body,
            `${localStorage.getItem('selected_clientName')}_List_of_Claims_${this.date}.pdf`
          );
          resolve();
        } else {
          console.log('something went wrong');
          reject(new Error('PDF export failed'));
        }
      });
    });
  }

  downloadPdf(): void {
    this.openExportModal('pdf');
  }

  getcompanyData() {
    this.appService.fetchCompanyNameData((callback: any) => {
      if (callback.status) {
        this.companyData = this.appService.sortByAlphabet(callback.data, 'name');
      }
    })
  }

  fetchClaims() {
    this.showLoader = true;
    let ths = this;
    let params: any = {
      "clientId": this.selectedCompanyUuid,
    }
    ths.appService.fetchAdminBillingClaims(params, (res: any) => {
      if (res.status == 200 && res.data) {
        ths.claimDetail = res.data;
        let data: any = ths.claimDetail.map((e: any) => {
          if (e.claimId.endsWith("_P")) {
            e['EstAmount'] = e.primeSecSubmittedTotal;
          } else {
            e['EstAmount'] = e.secTotal;
          }
          e['dueDateSort'] = e.followUpDate == null ? e.pendingSince : e.followUpDate;
          if (e['rebilledStatus'] == ths.appConstants.LIST_CLAIM_REBILL) e['statusType'] = ths.appConstants.RE_BILLING_ID;
          return e;
        })
        ths.claimDetail = data;

        this.showLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
        this.filterOptionClaimType();
        this.filterOptionActionRequired();
        this.filterOptionInsuranceName();
        this.filterOptionInsuranceType();
        this.filterOptionAssignedToTeam();
        this.filterOptionAssignedTo();
        // this.filterOptionLastTeamWorked();
        this.filterOptionAgeBracket();
        this.showAgeBracket_WithColor_AndClaimIdDigits();
        this.resetAdminListPagination();
      }

    })
  }

  fetchOfficeByUuid() {
    this.appService.fetchOfficeByClientUuid(this.selectedCompanyUuid, (res: any) => {
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

  filterOptionClaimType() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.claimType = [];
    }
    this.filteredColumnData.claimType.push({ 'checked': true, 'claimType': 'Primary' }, { 'checked': true, 'claimType': 'Secondary' });
    this.isFilterValueExist = true;
    this.isFilterAllSelected.claimType = true;
  }

  filterOptionAgeBracket() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.ageBracket = [];
    }
    this.filteredColumnData.ageBracket.push({ 'checked': true, 'ageBracket': '0-30' }, { 'checked': true, 'ageBracket': '31-60' }, { 'checked': true, 'ageBracket': '61-90' }, { 'checked': true, 'ageBracket': '91-180' },{ 'checked': true, 'ageBracket': '181-365' },{ 'checked': true, 'ageBracket': '365+' });
    this.isFilterValueExist = true;
    this.isFilterAllSelected.ageBracket = true;
  }

  filterOptionActionRequired() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.actionRequired = [];
    }
    this.filteredColumnData.actionRequired.push({ 'checked': true, 'actionRequired': "Billing", "statusType": 1 }, { 'checked': true, 'actionRequired': "Re-Billing", "statusType": 2 });
    this.isFilterValueExist = true;
    this.isFilterAllSelected.actionRequired = true;
  }

  filterOptionInsuranceName() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.insuranceName = [];
    }
    this.filteredItems.forEach((e: any) => {
      if (e.claimId.includes("_P")) {
        this.filteredColumnData.insuranceName.push({ 'checked': true, 'insuranceName': e.primaryInsurance });
        e['insuranceName'] = e.primaryInsurance;
      } else if (e.claimId.includes("_S")) {
        this.filteredColumnData.insuranceName.push({ 'checked': true, 'insuranceName': e.secondaryInsurance });
        e['insuranceName'] = e.secondaryInsurance;
      }
    });
    this.filteredColumnData.insuranceName = Object.values(this.filteredColumnData.insuranceName.reduce((acc: any, { insuranceName }: any) => {
      if (!acc[insuranceName])
        acc[insuranceName] = { checked: true, insuranceName: insuranceName };
      return acc;
    }, {}));
    this.isFilterValueExist = true;
    this.sortFiltereData(this.filteredColumnData.insuranceName);
    this.isFilterAllSelected.insuranceName = true;
  }

  filterOptionInsuranceType() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.insuranceType = [];
    }
    this.filteredItems.forEach((e: any) => {
      if (e.claimId.includes("_P") && e.prName) {
        this.filteredColumnData.insuranceType.push({ 'checked': true, 'insuranceType': e.prName });
        e['insuranceType'] = e.prName;
      } else if (e.claimId.includes("_S") && e.secName) {
        this.filteredColumnData.insuranceType.push({ 'checked': true, 'insuranceType': e.secName });
        e['insuranceType'] = e.secName;
      }
    })
    this.filteredColumnData.insuranceType = Array.from(new Set(this.filteredColumnData.insuranceType.map((a: any) => a.insuranceType)))
      .map((insuranceType: any) => {
        return this.filteredColumnData.insuranceType.find((a: any) => a.insuranceType === insuranceType);
      });
    this.sortFiltereData(this.filteredColumnData.insuranceType);
    this.isFilterAllSelected.insuranceType = true;
  }

  filterOptionAssignedToTeam(){
    if (this.isFilterValueExist) {
      this.filteredColumnData.assignedToTeam = [];
    }
    this.filteredItems.forEach((e: any) => {
      if (!this.filteredColumnData.assignedToTeam.some((item: any) => item.assignedToTeam == e.assignedToTeam)) {
        this.filteredColumnData.assignedToTeam.push({ 'checked': true, 'assignedToTeam': e.assignedToTeam });
      }
    });
    this.sortFiltereData(this.filteredColumnData.assignedToTeam);
    this.isFilterAllSelected.assignedToTeam = true;
  }

  filterOptionAssignedTo() {
    if (this.isFilterValueExist) {
      this.filteredColumnData.assignedTo = [];
    }
    this.filteredItems.forEach((e: any) => {
      if (!this.filteredColumnData.assignedTo.some((item: any) => item.assignedTo == e.assignedTo)) {
        this.filteredColumnData.assignedTo.push({ 'checked': true, 'assignedTo': e.assignedTo });
      }
    });
    this.sortFiltereData(this.filteredColumnData.assignedTo);
    this.isFilterAllSelected.assignedTo = true;
  }

  // filterOptionLastTeamWorked() {
  //   this.appConstants.teamData.forEach((e: any) => {
  //     this.filteredColumnData.lastTeamWorked.push({ 'checked': true, 'lastTeam': e.teamName });
  //   })
  //   this.isFilterAllSelected.lastTeamWorked = true;
  // }

  removePrefix(data: any) {
    let arr: any = data;

    const claimIdCounts = new Map();
    const result = [];

    for (let obj of arr) {
      const claimId = obj.claimId.slice(0, -2); // remove the "_P" or "_S" suffix
      const count = claimIdCounts.get(claimId) || 0;
      claimIdCounts.set(claimId, count + 1);
      obj['dueDateSort'] = obj.followUpDate == null ? obj.pendingSince : obj.followUpDate;
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
    this.ensureListPageInRange();
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
      this.resetAdminListPagination();
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
      this.addOrRemoveFilterOffice();
      this.clearAssigmentArray();
      this.resetAdminListPagination();
    }
  }

  addOrRemoveFilterOffice() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveFilterInsName() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return checkbox.checked && checkbox['officeName'] === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveFilterInsType() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return checkbox.checked && checkbox['officeName'] === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveFilterStatus() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return checkbox.checked && checkbox['officeName'] === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveFilterClaimType() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });

  }

  addOrRemoveFilterAgeBracket() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveAssignedToTeam(){
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedTo'] === item['assignedTo'];
      });
    });
  }

  addOrRemoveAssignedTo() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['statusType'] == item['statusType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox['claimType'] === item['claimType'];
      });
    });

    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox['assignedToTeam'] === item['assignedToTeam'];
      });
    });
  }

  filterClaimType(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.claimType.length; i++) {
      if (this.filteredColumnData.claimType[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.claimType = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.claimType.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
      });
    });
    this.addOrRemoveFilterClaimType();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterAgeBracket(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.ageBracket.length; i++) {
      if (this.filteredColumnData.ageBracket[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.ageBracket = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.ageBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
      });
    });
    this.addOrRemoveFilterAgeBracket();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterActionRequired(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.actionRequired.length; i++) {
      if (this.filteredColumnData.actionRequired[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.actionRequired = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.actionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
    this.addOrRemoveFilterStatus();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterInsuranceName(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.insuranceName.length; i++) {
      if (this.filteredColumnData.insuranceName[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.insuranceName = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.insuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
    this.addOrRemoveFilterInsName();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterInsuranceType(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.insuranceType.length; i++) {
      if (this.filteredColumnData.insuranceType[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.insuranceType = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.insuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
    this.addOrRemoveFilterInsType();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterAssignedToTeam(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.assignedToTeam.length; i++) {
      if (this.filteredColumnData.assignedToTeam[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.assignedToTeam = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.assignedToTeam.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
    this.addOrRemoveAssignedToTeam();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  filterAssignedTo(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.assignedTo.length; i++) {
      if (this.filteredColumnData.assignedTo[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.assignedTo = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.assignedTo.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
    this.addOrRemoveAssignedTo();
    this.clearAssigmentArray();
    this.resetAdminListPagination();
  }

  // filterLastTeamWorked(filterProperty: any) {
  //   let isAllSelected: boolean = true;
  //   for (let i = 0; i < this.filteredColumnData.lastTeamWorked.length; i++) {
  //     if (this.filteredColumnData.lastTeamWorked[i].checked == false) {
  //       isAllSelected = false;
  //       break;
  //     }
  //   }
  //   this.isFilterAllSelected.lastTeamWorked = isAllSelected;
  //   this.filteredItems = this.claimDetail.filter((item: any) => {
  //     return this.filteredColumnData.lastTeamWorked.some((checkbox: any) => {
  //       return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
  //     });
  //   });
  //   this.clearAssigmentArray();
  // }

  onCompanySelect() {
    const selectedCompany = this.companyData.find((comp: any) => comp.companyUuid == this.selectedCompanyUuid);
    if (selectedCompany) {
      this.selectedClientName = selectedCompany.name;
    } else {
      this.selectedClientName = '';
    }
  }

  buildAndDownloadCsvFromRows(sourceRows: any[]) {
    let options: any = {
      showLabels: true,
      headers: [
        "Office",
        "Claim Id",
        "Patient ID",
        "Patient Name",
        'DOS',
        "Claim Age",
        "TFL",
        "Pending Since Date",
        "Age Bracket",
        "Claim Type",
        "Action Required",
        "Insurance Name",
        "Insurance Type",
        "Estimated Amount",
        "BillingAmount",
        "Assigned Team",
        "Due Date",
        "Assigned To"
      ]
    }

    let excelData: any;
    excelData = [...sourceRows];
    excelData = excelData.map((e: any) => {
      if (e.dos) {
        let date: Date = new Date(e.dos);
        e = { ...e, dos: `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}` };
      }
      else {
        e = { ...e, dos: '' };
      }
      if (this.currentTeamId == 3) {
        e = { ...e, ['actionRequired']: "Auditing" };
      }
      else {
        if (e.statusType == this.appConstants.BILLING_ID) {
          e = { ...e, ['actionRequired']: "BILLING" };
        } else {
          e = { ...e, ['actionRequired']: "RE-BILLING" };
        }
      }
      if (e.claimId.endsWith("_P")) {
        e = { ...e, ['claimType']: "Primary" };
      } else {
        e = { ...e, ['claimType']: "Secondary" };
      }
      if (e.pendingSince) {
        let date: Date = new Date(e.pendingSince);
        e = { ...e, pendingSince: `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}` };
      }
      else {
        e = { ...e, pendingSince: '' };
      }
      if (e.dueDateSort) {
        let date: Date = new Date(e.dueDateSort);
        e = { ...e, dueDateSort: `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}` };
      }
      else {
        e = { ...e, dueDateSort: '' };
      }
      return e;
    })      //method add value as "-" or "0", if its empty or null.

    excelData = excelData.map((e: any) => {
      return {
        "Office Name": e.officeName,
        "Claim Id": e.newClaimId,
        "Patient ID": e.patientId,
        "Patient Name": e.patientName,
        'DOS': e.dos,
        "Claim Age": e.claimAge,
        "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
        "Pending Since Date": e.pendingSince,
        "Age Bracket": e.ageBracket,
        "Claim Type": e.claimType,
        "Action Required": e.actionRequired,
        "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
        "Insurance Type": e.prName ? e.prName : e.secName,
        "Estimated Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
        "BillingAmount": e.billedAmount ? '$' + formatNumber(e.billedAmount, this.locale, '.0-0').toString() : "$0",
        "Assigned Team": e.assignedToTeam,
        "Due Date": e.dueDateSort,
        "Assigned To": e.assignedTo || ""
      }
    })  //method aligns the header to the value in CSV.
    excelData = excelData.map(
      ({ claimId, opdos, opdt, secTotal, uuid, statusType, EstAmount, ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData, `${'Bulk Billing'} ${this.selectedClientName} ${this.date}`, options);
  }

  exportToCsv(): void {
    this.openExportModal('csv');
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
    if (filterProperty == "claimType") {
      this.filteredColumnData.claimType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterClaimType("claimType");
    }
    if (filterProperty == "actionRequired") {
      this.filteredColumnData.actionRequired.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterActionRequired("statusType");
    }
    if (filterProperty == "insuranceName") {
      this.filteredColumnData.insuranceName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceName("insuranceName");
    }
    if (filterProperty == "insuranceType") {
      this.filteredColumnData.insuranceType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceType("insuranceType");
    }
    if (filterProperty == "ageBracket") {
      this.filteredColumnData.ageBracket.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterAgeBracket("ageBracket");
    }
    if (filterProperty == "assignedToTeam") {
      this.filteredColumnData.assignedToTeam.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterAssignedToTeam("assignedToTeam");
    }
    if (filterProperty == "assignedTo") {
      this.filteredColumnData.assignedTo.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterAssignedTo("assignedTo");
    }
  }

  sortFiltereData(filterValue: any) {
    filterValue.sort((a: any, b: any) => {
      const nameA = Object.keys(filterValue[0])[1] == 'insuranceType' ? a.insuranceType?.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? a.insuranceName?.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? a.officeName?.toUpperCase()
            //: Object.keys(filterValue[0])[4] == 'assignedTo' ? b.officeName?.toUpperCase()
            : '';// ignore upper and lowercase
      const nameB = Object.keys(filterValue[0])[1] == 'insuranceType' ? b.insuranceType?.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? b.insuranceName?.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? b.officeName?.toUpperCase()
            //: Object.keys(filterValue[0])[4] == 'assignedTo' ? b.officeName?.toUpperCase()  
            : '';// ignore upper and lowercase
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }
      // names must be equal
      return 0;
    });

  }

  showHideFilteredDropdown(filterName: any) {
    this.showFilteredDropdown[filterName] = !this.showFilteredDropdown[filterName];
    this.fliterName = filterName;
  }

  getMonthName(month: any) {
    const monthNames = [
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    return monthNames[month];
  }

  showAgeBracket_WithColor_AndClaimIdDigits() {
    let currentDate: any = new Date().setHours(0, 0, 0, 0); // To set the time equal
    this.filteredItems.forEach((e: any) => {
      if (e.dos) {
        let dos: any = new Date(e.dos);
        const diffTime = Math.abs(currentDate - dos);
        let diffDays: any = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        e.ageBracket = (diffDays <= 30) ? `0-30` : 
        (diffDays > 30 && diffDays <= 60) ? `31-60` : 
        (diffDays > 60 && diffDays <= 90) ? `61-90` : 
        (diffDays > 90 && diffDays <= 180) ? '91-180' :
        (diffDays > 180 && diffDays <= 365) ? '181-365' :
        (diffDays > 365) ? '365+' : '';
      }
      if (e.claimId) {
        e.newClaimId = e.claimId.replace('_P', "").replace('_S', "")
      }
      if (e.claimAge && e.timelyFilingLimitData) {
        if (Number(e.timelyFilingLimitData) - e.claimAge < 30) {
          e.colorChange = true;
        }
        else {
          e.colorChange = false;
        }
      }
    });
  }

  toggleTooltip(tooltip: any) {
    this.showTooltipConfig[tooltip] = !this.showTooltipConfig[tooltip];
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape' || event.keyCode === 27) {
        this.showTooltipConfig[tooltip] = false;
      }
    })
    if (!this.showTooltipConfig[tooltip]) {
      document.removeAllListeners('keydown');
    }
  }

  createClaimAssignmentList(event: any, data: any) {
    if (event.srcElement.checked) {
      this.listofClaimsForSubmission.push(data);
      data.assignAction = true;
      if (this.listofClaimsForSubmission.length == this.filteredItems.length) {
        this.reassignmentSelectBox.nativeElement.checked = true;
      }
    } else {
      this.reassignmentSelectBox.nativeElement.checked = false;
      this.listofClaimsForSubmission = this.listofClaimsForSubmission.filter((obj: any) => { return obj.uuid !== data.uuid });
    }
  }

  selectAllReAssigment(event: any) {
    let ths = this;
    ths.selectedOfficeNames = [];
    ths.listofClaimsForSubmission = [];
    ths.filteredItems.forEach((element: any) => {
      element.assignAction = false;
    });
    //listofClaimsForSubmission
    if (event.srcElement.checked) {
      ths.filteredItems.forEach((element: any) => {
        element.assignAction = true;
      });
      ths.listofClaimsForSubmission = ths.filteredItems;
    }
  }

  openAssigmentPopUp() {
    this.openAssignmentModal();
  }

  clearAssigmentArray() {
    this.listofClaimsForSubmission = [];
    this.reassignmentSelectBox.nativeElement.checked = false;
    this.filteredItems.forEach((element: any) => {
      element.assignAction = false;
    });
  }

  openAssignmentModal() {
    this.modelElement.modal = document.getElementById("assgn-modal");
    this.modelElement.span = document.getElementsByClassName("close")[0];
    this.modelElement.modal.style.display = "block";
  }

  closeModal() {
    this.assignreason.nativeElement.value = "";
    this.modelElement.modal.style.display = "none";
  }

  makeAsssignment() {
    let ths = this;
    if (ths.assignreason.nativeElement.value.trim() === '') return;
    let claimIds: Array<string> = [];
    ths.listofClaimsForSubmission.forEach((element: any) => {
      claimIds.push(element.uuid);
    });
    ths.loader.assignLoader = true;
    let obj: any = { "claimIds": claimIds, "clientId": this.selectedCompanyUuid, "comment": ths.assignreason.nativeElement.value };

    ths.appService.submitAdminBillingClaims(obj, (res: any) => {
      if (res.status === 200) {
        ths.closeModal();
        ths.clearAssigmentArray();
        ths.loader.assignLoader = false;
        this.fetchClaims();
      }
    });
  }
}
