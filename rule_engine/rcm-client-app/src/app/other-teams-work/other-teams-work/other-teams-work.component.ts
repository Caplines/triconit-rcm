import { ChangeDetectorRef, Component, OnInit, LOCALE_ID, Inject, HostListener, Input, ViewChild, ElementRef } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';
import { TeamsM, TLUser } from '../../models/claim-rcm-data-model';
import { DownLoadService } from 'src/app/service/download.service';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { formatNumber } from '@angular/common';

@Component({
  selector: 'app-other-teams-work',
  templateUrl: './other-teams-work.component.html',
  styleUrls: ['./other-teams-work.component.scss']
})
export class OtherTeamsWorkComponent implements OnInit {

  selectedBtype: number = 0;
  isRoleAssociate: boolean;
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  isSorted: any = {};
  loader: any = { 'billingLoader': false, 'listClaimLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false, 'assignLoader': false, 'bulkReassignmentLoader': false, 'bulkAgingLoader': false };
  showFilteredDropdown: any = { 'officeName': false, 'insuranceName': false, insuranceType: false, claimType: false, lastTeam: false, 'currentTeam': false, 'currentStatus': false, 'nextActionRequired': false, 'providerSpeciality': false, 'selectAging': false };
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  isFilterAllSelected: any = { 'officeName': false, 'ageBracket': false, 'insuranceName': false, 'insuranceType': false, claimType: false, lastTeam: false, 'currentTeam': false, 'currentStatus': false, 'nextActionRequired': false, 'providerSpeciality': false, 'selectAging': false };
  clientName: string = '';
  isFilterValueExist: boolean = false;

  selectedFilesMap: any = new Map();
  selectedFiles: any = [];

  showModal: boolean = false;
  isFilesSubmitted: boolean = false;
  errorMessage: any;
  otherTeams: any = [];
  submitBtnConfig: any = { 'remarks': [], 'otherTeamId': [] };
  currentClaimUuid: any;

  removedFilesMap: any = new Map();
  removedFiles: any = [];
  hasAttachmentFilesRemoved: boolean = false;
  currentTeamName: any;
  date: any;
  filteredAgeBracket: any = [];
  hasAttachedFilesWithRemark: boolean = false;
  alert: any = { 'alertMsg': '', 'showAlert': false };
  showTransferClaimModal = false;
  selectedClaimsTransfer: any = { "claimUuid": [], "remarks": "" };
  filteredInsuranceName: any = [];
  filteredInsuranceType: any = [];
  filteredClaimType: any = [];
  filteredLastTeam: any = [];
  filteredCurrentStatus: any = [];
  filteredNextActionRequired: any = [];
  filteredProviderSpeciality: any = [];
  filteredSelectAging: any = [];
  // Pagination state
  currentPage: number = 0;
  pageSize: number = 50;
  totalCount: number = 0;
  totalPages: number = 0;
  storedTotalCount: number = 0;
  goToPageInput: number = 1;
  serverSortBy: string = '';
  serverSortOrder: string = 'asc';
  serverFilters: any = {
    officeFilter: '',
    claimTypeFilter: '',
    ageBracketFilter: '',
    insuranceFilter: '',
    insuranceTypeFilter: '',
    currentStatusFilter: '',
    nextActionFilter: '',
    providerSpecialityFilter: '',
    lastTeamFilter: ''
  };
  /** Checkbox edits; applied to API only after "Apply filters" (matches billing list-of-claims). */
  pendingServerFilters: any = {
    officeFilter: '',
    claimTypeFilter: '',
    ageBracketFilter: '',
    insuranceFilter: '',
    insuranceTypeFilter: '',
    currentStatusFilter: '',
    nextActionFilter: '',
    providerSpecialityFilter: '',
    lastTeamFilter: ''
  };
  private readonly OTW_FILTER_STATE_KEY = 'otw_list_filter_state_v1';
  readonly pageSizeOptions: number[] = [20, 50, 100, 200];
  private readonly PAGE_SIZE_KEY = 'otw_items_per_page';
  /** Matches backend `data.listClaims.maxRecordsPerPage` — one request returns at most this many rows. */
  private readonly EXPORT_PAGE_CHUNK = 10000;
  exportModalOpen = false;
  exportModalKind: 'csv' | 'pdf' = 'csv';
  exportScopeAll = true;
  exportRowLimitInput = 1000;
  exportLoading = false;
  tabSwitch: any = { 'submitted': false, 'unSubmitted': true, 'myClaims': false };
  tabValue: any;
  modelElement: any = { 'modal': '', 'span': '' };
  modelElement1: any = { 'modal': '', 'span': '' }
  @ViewChild('reassignmentSelectBox') reassignmentSelectBox!: ElementRef;
  teamsMs: Array<TeamsM>;
  assgnmentUsers: Array<TLUser>;

  assignmentType: any = { 'assignSameTeam': 1, 'assignOtherTeam': 2, 'unAssign': 3 };
  assignmentTypeSelect: number = -1;
  listofClaimsForAssignAction: any = [];
  /** True when header "select all for reassignment" loaded all rows matching filters (every page), not only the current page. */
  reassignmentSelectAllMode = false;
  /** True when header "select all" for Send to Aging loaded all eligible rows across pages. */
  bulkAgingSelectAll = false;
  selectedOfficeNames: Array<string> = [];
  @ViewChild('assignreason') assignreason!: ElementRef;
  @ViewChild('radiox1') radiox1!: ElementRef;
  @ViewChild('radiox2') radiox2!: ElementRef;
  @ViewChild('radiox3') radiox3!: ElementRef;
  showColumns: any = { "currentStatus": false, "nextActionRequired": false, "dueDate": false, "providerSpeciality": false, "dueBalance": false, "showAttach": false, "dueDateSort": false };
  columnPermissionsByTeam: any = {
    'Aging': ['currentStatus', 'nextActionRequired', 'dueDateSort', 'providerSpeciality'],
    'CDP': ['currentStatus', 'nextActionRequired', 'dueDateSort', 'providerSpeciality'],
    'Credentialing': ['currentStatus', 'nextActionRequired', 'showAttach', 'dueDateSort'],
    'Patient Statement': ['currentStatus', 'nextActionRequired', 'providerSpeciality', 'dueBalance', 'dueDateSort'],
    'Payment Posting': ['currentStatus', 'nextActionRequired', 'providerSpeciality', 'dueDateSort'],
    'LC3': ['showAttach', 'dueDateSort'],
    'Office': ['showAttach', 'dueDateSort'],
    'Ortho': ['showAttach', 'dueDateSort'],
    'PPO IV': ['showAttach', 'dueDateSort'],
    'Medicaid IV': ['showAttach', 'dueDateSort'],
    'Need to hold': ['showAttach', 'dueDateSort'],
    'Quality': ['showAttach', 'dueDateSort'],
    'Patient Calling': ['showAttach', 'dueDateSort'],
  };

  showTooltipConfig: any = {};
  selectAllAging: any = null;
  selectedHeaders: string[];
  sendToAging: boolean = false;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    let el = event.target as HTMLElement | null;
    if (el && el.nodeType !== Node.ELEMENT_NODE) {
      el = el.parentElement;
    }
    if (el?.closest?.('.loc-claims-filter-anchor')) {
      return;
    }
    this.closeAllFilterDropdowns();
  }

  closeAllFilterDropdowns(): void {
    Object.keys(this.showFilteredDropdown).forEach((k) => {
      this.showFilteredDropdown[k] = false;
    });
  }

  constructor(@Inject(LOCALE_ID) private locale: string, private appService: ApplicationServiceService, public appConstants: AppConstants, private title: Title, private downloadService: DownLoadService, private cdr: ChangeDetectorRef) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    const savedSize = localStorage.getItem(this.PAGE_SIZE_KEY);
    if (savedSize) {
      const parsed = parseInt(savedSize, 10);
      if (this.pageSizeOptions.includes(parsed)) {
        this.pageSize = parsed;
      }
    }
    this.clientName = localStorage.getItem("selected_clientName");
    this.isRoleAssociate = Utils.isRoleAsso();
    this.currentTeamName = this.appConstants.teamData.find((e: any) => e.teamId == Utils.selectedTeam());
    this.tabSwitch.unSubmitted = false;
    this.tabSwitch.submitted = true;
    this.tabValue = 'submitted';
    this.fetchClaims("submitted", 0);
    this.fetchOtherTeams();
    this.showOrHideColumns(this.currentTeamName);

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

  fetchOtherTeams() {
    this.appService.fetchOtherTeams((res: any) => {
      if (res.status === 200) {
        this.otherTeams = res.data;
        this.teamsMs = res.data;

      }
    })
  }

  fetchClaims(subType: string, page: number = 0) {
    this.loader.listClaimLoader = true;
    this.currentPage = page;
    //this.clearAssigmentArray();
    let ths = this;
    const knownTotalCount = page === 0 ? 0 : this.storedTotalCount;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, subType, page, ths.pageSize, knownTotalCount, (res: any) => {
      if (res.status === 200) {
        ths.totalCount = res.data.totalCount;
        ths.totalPages = res.data.totalPages;
        ths.currentPage = res.data.page;
        ths.goToPageInput = ths.currentPage + 1;
        ths.storedTotalCount = res.data.totalCount;
        ths.claimDetail = this.removePrefix(res.data.claims);
        let data: any = ths.claimDetail.map((e: any) => {
          if (e.claimId.endsWith("_P")) {
            e['EstAmount'] = e.primeSecSubmittedTotal;
          } else {
            e['EstAmount'] = e.secTotal;
          }
          e['dueDateSort'] = e.followUpDate == null ? e.pendingSince : e.followUpDate;
          if (e['nextAction'] == ths.appConstants.NEED_TO_RE_BILL) e['statusType'] = ths.appConstants.RE_BILLING_ID;
          return e;
        })
        ths.claimDetail = data;
        //console.log(ths.claimDetail);

        this.fetchOfficeByUuid();
        if (!this._hasActiveFilters()) {
        this.filterOptionAgeBracket();
        this.filterOptionInsuranceName();
        this.filterOptionInsuranceType();
        this.filterOptionClaimType();
        this.filterOptionLastTeam();
        this.filterOptionCurrentStatus();
        this.filterOptionNextActionRequired();
        this.filterOptionProviderSpeciality();
          this.filterOptionSelectAging();
        }
        this.showAgeBracket_WithColor_AndClaimIdDigits();
        this.checkDiffForPaymentPosting();
        this._afterFetchSyncUi();
        ths.loader.listClaimLoader = false;
      }
    }, { sortBy: this.serverSortBy, sortOrder: this.serverSortOrder, ...this.serverFilters });
  }

  private _emptyOtwFilters(): any {
    return {
      officeFilter: '',
      claimTypeFilter: '',
      ageBracketFilter: '',
      insuranceFilter: '',
      insuranceTypeFilter: '',
      currentStatusFilter: '',
      nextActionFilter: '',
      providerSpecialityFilter: '',
      lastTeamFilter: ''
    };
  }

  private _otwFilterKeys(): string[] {
    return [
      'officeFilter', 'claimTypeFilter', 'ageBracketFilter', 'insuranceFilter', 'insuranceTypeFilter',
      'currentStatusFilter', 'nextActionFilter', 'providerSpecialityFilter', 'lastTeamFilter'
    ];
  }

  private _filtersAnyNonEmpty(f: any): boolean {
    return this._otwFilterKeys().some((k) => (f[k] || '') !== '');
  }

  private _filtersEqual(a: any, b: any): boolean {
    return this._otwFilterKeys().every((k) => (a[k] || '') === (b[k] || ''));
  }

  get hasUnappliedPendingFilters(): boolean {
    return !this._filtersEqual(this.serverFilters, this.pendingServerFilters);
  }

  get canResetFilters(): boolean {
    return this._filtersAnyNonEmpty(this.serverFilters)
      || this._filtersAnyNonEmpty(this.pendingServerFilters)
      || this.hasUnappliedPendingFilters;
  }

  applyPendingFilters(): void {
    this.loader.listClaimLoader = true;
    this.serverFilters = { ...this.pendingServerFilters };
    this._persistOtwFilterState();
    this._applyServerFilters();
  }

  resetFilters(): void {
    this.loader.listClaimLoader = true;
    this.serverFilters = this._emptyOtwFilters();
    this.pendingServerFilters = this._emptyOtwFilters();
    this._resetOtwFilterUiToAllSelected();
    this._persistOtwFilterState();
    this._applyServerFilters();
  }

  private _persistOtwFilterState(): void {
    try {
      const listViewTab = this.tabSwitch.myClaims ? 'myClaims' : this.tabSwitch.unSubmitted ? 'unSubmitted' : 'submitted';
      const payload = {
        serverFilters: { ...this.serverFilters },
        pendingServerFilters: { ...this.pendingServerFilters },
        serverSortBy: this.serverSortBy,
        serverSortOrder: this.serverSortOrder,
        listViewTab
      };
      localStorage.setItem(this.OTW_FILTER_STATE_KEY, JSON.stringify(payload));
    } catch {
      /* ignore */
    }
  }

  private _resetOtwFilterUiToAllSelected(): void {
    if (this.filteredOfficeName?.length) {
      this.filteredOfficeName.forEach((e: any) => { e.checked = true; });
    }
    this.isFilterAllSelected.officeName = true;
    const arrs: any[] = [
      this.filteredAgeBracket, this.filteredInsuranceName, this.filteredInsuranceType, this.filteredClaimType,
      this.filteredCurrentStatus, this.filteredNextActionRequired, this.filteredProviderSpeciality, this.filteredLastTeam,
      this.filteredSelectAging
    ];
    arrs.forEach((arr) => {
      if (arr?.length) {
        arr.forEach((e: any) => { e.checked = true; });
      }
    });
    Object.keys(this.isFilterAllSelected).forEach((k) => {
      if (this.isFilterAllSelected[k] !== undefined) {
        this.isFilterAllSelected[k] = true;
      }
    });
  }

  private _syncCheckboxListFromComma(comma: string, items: any[], valueKey: string): void {
    if (!items?.length) {
      return;
    }
    if (!comma || !String(comma).trim()) {
      items.forEach((e: any) => { e.checked = true; });
      return;
    }
    const selected = new Set(String(comma).split(',').map((s) => s.trim()).filter(Boolean));
    items.forEach((e: any) => {
      const v = String(e[valueKey]);
      e.checked = selected.has(v);
    });
  }

  private _everyItemChecked(items: any[]): boolean {
    if (!items?.length) {
      return true;
    }
    return items.every((x: any) => x.checked);
  }

  private _syncOfficeFilterUiFromPending(): void {
    if (!this.filteredOfficeName?.length) {
      return;
    }
    const raw = this.pendingServerFilters.officeFilter || '';
    if (!String(raw).trim()) {
      this.filteredOfficeName.forEach((e: any) => { e.checked = true; });
      this.isFilterAllSelected.officeName = true;
      return;
    }
    const selected = new Set(String(raw).split(',').map((s) => s.trim()).filter(Boolean));
    this.filteredOfficeName.forEach((e: any) => {
      const name = String(e.officeName || e.name || '').trim();
      e.checked = selected.has(name);
    });
    this.isFilterAllSelected.officeName = this._everyItemChecked(this.filteredOfficeName);
  }

  private _syncAllFilterUiFromPending(): void {
    this._syncOfficeFilterUiFromPending();
    const p = this.pendingServerFilters;
    this._syncCheckboxListFromComma(p.claimTypeFilter, this.filteredClaimType, 'claimType');
    this.isFilterAllSelected.claimType = this._everyItemChecked(this.filteredClaimType);
    this._syncCheckboxListFromComma(p.ageBracketFilter, this.filteredAgeBracket, 'ageBracket');
    this.isFilterAllSelected.ageBracket = this._everyItemChecked(this.filteredAgeBracket);
    this._syncCheckboxListFromComma(p.insuranceFilter, this.filteredInsuranceName, 'insuranceName');
    this.isFilterAllSelected.insuranceName = this._everyItemChecked(this.filteredInsuranceName);
    this._syncCheckboxListFromComma(p.insuranceTypeFilter, this.filteredInsuranceType, 'insuranceType');
    this.isFilterAllSelected.insuranceType = this._everyItemChecked(this.filteredInsuranceType);
    this._syncCheckboxListFromComma(p.currentStatusFilter, this.filteredCurrentStatus, 'currentStatus');
    this.isFilterAllSelected.currentStatus = this._everyItemChecked(this.filteredCurrentStatus);
    this._syncCheckboxListFromComma(p.nextActionFilter, this.filteredNextActionRequired, 'nextActionRequired');
    this.isFilterAllSelected.nextActionRequired = this._everyItemChecked(this.filteredNextActionRequired);
    this._syncCheckboxListFromComma(p.providerSpecialityFilter, this.filteredProviderSpeciality, 'providerSpeciality');
    this.isFilterAllSelected.providerSpeciality = this._everyItemChecked(this.filteredProviderSpeciality);
    this._syncCheckboxListFromComma(p.lastTeamFilter, this.filteredLastTeam, 'lastTeam');
    this.isFilterAllSelected.lastTeam = this._everyItemChecked(this.filteredLastTeam);
  }

  private _reapplySelectAgingClientFilter(): void {
    if (!this.filteredSelectAging?.length) {
      this.filteredItems = this.claimDetail;
      return;
    }
    const allSel = this.filteredSelectAging.every((x: any) => x.checked);
    if (allSel) {
      this.filteredItems = this.claimDetail;
      return;
    }
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) =>
        checkbox.checked && checkbox.selectAging === (item.diffForPaymentPosting ? 'Unlocked' : 'Locked'));
    });
  }

  /** After server data and filter dropdowns are built; sync checkboxes to pending + client-only Aging lock filter. */
  private _afterFetchSyncUi(): void {
    this.filteredItems = this.claimDetail;
    this._syncAllFilterUiFromPending();
    this._reapplySelectAgingClientFilter();
  }

  private _onFilterDropdownClearBranch(): void {
    this.filteredItems = this.claimDetail;
    this._syncAllFilterUiFromPending();
    this._reapplySelectAgingClientFilter();
  }

  private _hasActiveFilters(): boolean {
    return Object.values(this.serverFilters).some((v: any) => v !== '');
  }

  private _applyServerFilters() {
    this.currentPage = 0;
    this.storedTotalCount = 0;
    this.clearAssigmentArray();
    if (this.tabSwitch.myClaims) {
      this.fetchClaimsLead('MyClaims', 0);
    } else {
      this.fetchClaims(this.tabValue, 0);
    }
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
    this.serverSortBy = sortProp;
    this.serverSortOrder = order;
    this.currentPage = 0;
    this.storedTotalCount = 0;
    this.clearAssigmentArray();
    this._persistOtwFilterState();
    if (this.tabSwitch.myClaims) {
      this.fetchClaimsLead('MyClaims', 0);
    } else {
      this.fetchClaims(this.tabValue, 0);
    }
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
        ele['insuranceName'] = ele.primaryInsurance;
        ele['insuranceType'] = ele.prName;
        ele['currentStatus'] = ele.claimStatus;
        ele['nextActionRequired'] = ele.nextAction;
        ele['providerSpeciality'] = ele.providerSpeciality;
        ele['selectAging'] = ele.selectAging;
      })
    });
    this.sortFiltereData(this.filteredOfficeName);
    this._syncOfficeFilterUiFromPending();
  }

  filterOfficeName(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredOfficeName.length; i++) {
        if (this.filteredOfficeName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.officeName = isAllSelected;
      this.pendingServerFilters.officeFilter = isAllSelected ? ''
        : this.filteredOfficeName.filter((x: any) => x.checked).map((x: any) => x.officeName || x.name).join(',');
      this._persistOtwFilterState();
    }
  }

  filterAgeBracket(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredAgeBracket.length; i++) {
      if (this.filteredAgeBracket[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.ageBracket = isAllSelected;
    this.pendingServerFilters.ageBracketFilter = isAllSelected ? ''
      : this.filteredAgeBracket.filter((x: any) => x.checked).map((x: any) => x.ageBracket).join(',');
    this._persistOtwFilterState();
  }

  async filterOptionInsuranceName() {
    this.filteredInsuranceName = await this.claimDetail.map((e: any) => { return { insuranceName: e.primaryInsurance, checked: true } });
    if (this.filteredInsuranceName.length > 2) {
      this.filteredInsuranceName = this.removeDuplicateValues(this.filteredInsuranceName, 'insuranceName');
    }
    this.filterInsuranceName();

  }

  filterInsuranceName(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    } else {

      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredInsuranceName.length; i++) {
        if (this.filteredInsuranceName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.insuranceName = isAllSelected;
      this.pendingServerFilters.insuranceFilter = isAllSelected ? ''
        : this.filteredInsuranceName.filter((x: any) => x.checked).map((x: any) => x.insuranceName).join(',');
      this._persistOtwFilterState();
    }
  }

  filterOptionAgeBracket() {
    if (this.isFilterValueExist) {
      this.filteredAgeBracket = [];
    }
    this.filteredAgeBracket.push({ 'checked': true, 'ageBracket': '0-30' }, { 'checked': true, 'ageBracket': '31-60' }, { 'checked': true, 'ageBracket': '61-90' }, { 'checked': true, 'ageBracket': '91-180' }, { 'checked': true, 'ageBracket': '181-365' }, { 'checked': true, 'ageBracket': '365+' });
    this.isFilterValueExist = true;
    this.isFilterAllSelected.ageBracket = true;
  }


  addOrRemoveFilterOffice() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
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
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  addOrRemoveInsuranceName() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
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
    else if (filterProperty == "ageBracket") {
      this.filteredAgeBracket.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterAgeBracket("ageBracket");
    }
    else if (filterProperty == "insuranceName") {
      this.filteredInsuranceName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceName("insuranceName");
    }
    else if (filterProperty == "insuranceType") {
      this.filteredInsuranceType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceType("insuranceType");
    }
    else if (filterProperty == "claimType") {
      this.filteredClaimType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterClaimType("claimType");
    }
    else if (filterProperty == "lastTeam") {
      this.filteredLastTeam.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterLastTeam("lastTeam");
    }
    else if (filterProperty == "currentStatus") {
      this.filteredCurrentStatus.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterCurrentStatus("currentStatus");
    }
    else if (filterProperty == "nextActionRequired") {
      this.filteredNextActionRequired.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterNextActionRequired("nextActionRequired");
    }
    else if (filterProperty == "providerSpeciality") {
      this.filteredProviderSpeciality.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterProviderSpeciality("providerSpeciality");
    }
    else if (filterProperty == "selectAging") {
      this.filteredSelectAging.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterSelectAging("selectAging");
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
    this.showFilteredDropdown[filterName] = !this.showFilteredDropdown[filterName];
  }

  get isRoleLead() {
    return Utils.isRoleLead();
  }

  closeModal() {
    this.showModal = false;
  }


  receiveChildEvent(event: any) {
    if (event['action'] === 'fileSelected') {
      this.setSelectedFileForComponent(event.claimUuid, event.value);
    } else if (event['action'] === 'filesSelectedToRemove') {
      this.setSelectedFileToRemove(event.claimUuid, event.value)
    } else if (event['action'] === 'fileUploadedSuccess') {
      this.alert.alertMsg = event.value;
      this.alert.showAlert = true;
      this.hasAttachedFilesWithRemark = event.hasAttachedFiles;
      setTimeout(() => { this.alert.alertMsg = ''; this.alert.showAlert = false; }, 2000);
    } else if (event['action'] === 'hasAttachedFileForSameUser') {
      this.hasAttachedFilesWithRemark = event.hasAttachedFiles;
    }
  }

  setSelectedFileForComponent(claimUuid: any, file: File) {
    this.selectedFilesMap.set(claimUuid, file);
  }

  setSelectedFileToRemove(claimUuid: any, fileParam: any) {
    this.removedFilesMap.set(claimUuid, fileParam);
  }

  getSelectedFileForComponent(claimUuid: any) {
    return this.selectedFilesMap.get(claimUuid);
  }

  getSelectedFilesToRemove(claimUuid: any) {
    return this.removedFilesMap.get(claimUuid);
  }

  openModalAndValidateFields(data: any) {
    this.currentClaimUuid = data.uuid;
    // this.selectedFiles = this.getSelectedFileForComponent(data.uuid);
    // this.removedFiles = this.getSelectedFilesToRemove(data.uuid);
    if (this.submitBtnConfig['remarks'][data.uuid]) {
      this.errorMessage = '';
      // this.showModal=true;
      this.submitConfirmation();
    } else {
      data['isInvalid'] = true;
    }
    // if(!this.selectedFiles || this.selectedFiles?.length==0){
    //   this.selectedFiles= [];
    //   if(this.claimDetail.some((item:any)=>item.uuid == data.uuid ? item.attachmentCount : undefined)){
    //     this.errorMessage= '';
    // } else{
    //   this.errorMessage = "No Files Are Attached !";
    // }
    // }
    // if(!this.removedFiles){
    //   this.removedFiles=[];
    // }
  }


  submitConfirmation() {
    const remarks = this.submitBtnConfig['remarks'][this.currentClaimUuid];
    if (!remarks) {
      this.errorMessage = "Remarks Are Mandatory !";
    }
    else {

      this.AssignClaimWithRemark(this.currentClaimUuid, this.hasAttachedFilesWithRemark);
      // if(this.isRemoveFileArrayNotEmpty()){
      //   this.removeAttachmentFile();
      // } else {
      //   this.loopThroughData(this.selectedFiles, 0);
      // }
    }
  }

  isRemoveFileArrayNotEmpty() {
    if (Array.isArray(this.removedFiles)) {
      return false;
    } else {
      return true;
    }
  }

  // removeAttachmentFile(){
  //   this.appService.removeAttachmentFile(this.removedFiles,(res:any)=>{
  //     if(res.status == 200){
  //       // this.loopThroughData(this.selectedFiles, 0);
  //       if(!this.hasAttachmentFilesRemoved){
  //         this.errorMessage  = res.data.message;
  //       }
  //     } else{
  //       this.errorMessage  = res.data.message;
  //     }
  //   })
  // }

  selectOtherTeam(event: any, claimUuid: any) {
    this.submitBtnConfig['otherTeamId'][claimUuid] = event.target.value;
  }

  //   loopThroughData(dataArray: any[], currentIndex: number) {
  //     if (currentIndex >= dataArray.length) {
  //       // this.AssignClaimWithRemark(dataArray[0]?.claimUuid ? dataArray[0]?.claimUuid : this.currentClaimUuid);
  //       return;
  //     }
  //     const currentData = dataArray[currentIndex];
  //     let formData: any = new FormData();
  //     formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.currentClaimUuid);
  //     formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
  //     formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
  //     this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
  //       if (res.data.status) {
  //         this.loopThroughData(dataArray, currentIndex + 1);
  //       } else {
  //         this.errorMessage = res.data.message;
  //       }
  //     })
  // }

  AssignClaimWithRemark(claimUuid: any, hasAttachedFiles: boolean) {

    this.isOtherTLExist((res: any) => {
      if (res) {
        let hasRemarks = this.submitBtnConfig['remarks'][claimUuid];
        if (hasRemarks) {
          let params: any = {
            "remark": this.submitBtnConfig['remarks'][claimUuid],
            "claimUuid": claimUuid,
            "assignToTeamId": this.submitBtnConfig['otherTeamId'][claimUuid] ? +this.submitBtnConfig['otherTeamId'][claimUuid] : null,   //converting string into number using unary operator +
            "attachmentsWithRemarks": AppConstants.ATTACH_WITH_REMARKS

          }
          this.appService.AssignClaimWithRemark(params, (res: any) => {
            if (res.status == 200) {
              this.showModal = false;
              this.errorMessage = '';
              this.removeSubmittedClaimRow(claimUuid);
            }
          })
        } else {
          this.errorMessage = "Remarks Are Mandatory";
        }
      }
    })
  }


  removeSubmittedClaimRow(claimUuid: any) {
    let index = this.filteredItems.findIndex((item: any) => item.uuid == claimUuid);
    this.filteredItems.splice(index, 1);
  }

  openExportModal(kind: 'csv' | 'pdf'): void {
    this.exportModalKind = kind;
    this.exportScopeAll = true;
    this.exportRowLimitInput = this.totalCount > 0 ? Math.min(1000, this.totalCount) : 1;
    this.exportModalOpen = true;
  }

  closeExportModal(): void {
    this.exportModalOpen = false;
  }

  confirmExport(): void {
    if (this.totalCount === 0 || this.hasUnappliedPendingFilters) {
      return;
    }
    const kind = this.exportModalKind;
    const maxRows = this.exportScopeAll
      ? this.totalCount
      : Math.min(
          this.totalCount,
          Math.max(1, parseInt(String(this.exportRowLimitInput), 10) || 1)
        );
    this.closeExportModal();
    this.exportLoading = true;
    if (kind === 'csv') {
      this.loader.exportCSVLoader = true;
    } else {
    this.loader.exportPDFLoader = true;
    }
    this.collectClaimsForExport(maxRows)
      .then((rows) => {
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

  private fetchExportPagePromise(page: number, pageSize: number, knownTotalCount: number, isLead: boolean): Promise<any> {
    return new Promise((resolve) => {
      const opts = { sortBy: this.serverSortBy, sortOrder: this.serverSortOrder, ...this.serverFilters };
      const cb = (res: any) => resolve(res);
      if (isLead) {
        this.appService.fetchLeadClaimDet(this.selectedBtype, 'MyClaims', page, pageSize, knownTotalCount, cb, opts);
      } else {
        this.appService.fetchAssociateClaimDet(this.selectedBtype, this.tabValue, page, pageSize, knownTotalCount, cb, opts);
      }
    });
  }

  private mapRowsForExport(raw: any[], isLead: boolean): any[] {
    const prefixed = (raw || []).map((e: any) => ({ ...e }));
    const base = this.removePrefix(prefixed);
    return base.map((e: any) => {
      const x = { ...e };
      if (x.claimId.endsWith('_P')) {
        x['EstAmount'] = x.primeSecSubmittedTotal;
      } else {
        x['EstAmount'] = x.secTotal;
      }
      x['dueDateSort'] = x.followUpDate == null ? x.pendingSince : x.followUpDate;
      if (!isLead && x['nextAction'] == this.appConstants.NEED_TO_RE_BILL) {
        x['statusType'] = this.appConstants.RE_BILLING_ID;
      }
      return x;
    });
  }

  private enrichExportRowsForDisplay(claims: any[]): void {
    const currentDate: any = new Date().setHours(0, 0, 0, 0);
    claims.forEach((e: any) => {
      if (e.dos) {
        const dos: any = new Date(e.dos);
        const diffTime = Math.abs(currentDate - dos);
        const diffDays: any = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        e.ageBracket =
          diffDays <= 30
            ? `0-30`
            : diffDays > 30 && diffDays <= 60
              ? `31-60`
              : diffDays > 60 && diffDays <= 90
                ? `61-90`
                : diffDays > 90 && diffDays <= 180
                  ? '91-180'
                  : diffDays > 180 && diffDays <= 365
                    ? '181-365'
                    : diffDays > 365
                      ? '365+'
                      : '';
      }
        if (e.claimId) {
        e.newClaimId = e.claimId.replace('_P', '').replace('_S', '');
      }
      if (e.claimAge && e.timelyFilingLimitData) {
        e.colorChange = Number(e.timelyFilingLimitData) - e.claimAge < 30;
      }
    });
  }

  private async collectClaimsForExport(targetRows: number): Promise<any[]> {
    const isLead = this.tabSwitch.myClaims && this.isRoleLead;
    if (this.totalCount === 0 || targetRows <= 0) {
      return [];
    }
    const maxRows = Math.min(targetRows, this.totalCount);
    const accumulated: any[] = [];
    let page = 0;
    let knownTotalCount = 0;
    while (accumulated.length < maxRows) {
      const chunkSize = Math.min(this.EXPORT_PAGE_CHUNK, maxRows - accumulated.length);
      const res: any = await this.fetchExportPagePromise(page, chunkSize, knownTotalCount, isLead);
      if (!res || res.status !== 200) {
        throw new Error('Export fetch failed');
      }
      knownTotalCount = res.data.totalCount;
      const raw = res.data.claims || [];
      const mapped = this.mapRowsForExport(raw, isLead);
      accumulated.push(...mapped);
      if (accumulated.length > maxRows) {
        accumulated.splice(maxRows);
      }
      if (raw.length === 0) {
        break;
      }
      if (accumulated.length >= maxRows) {
        break;
      }
      const totalPages = res.data.totalPages != null ? res.data.totalPages : 1;
      if (page >= totalPages - 1) {
        break;
      }
      page++;
    }
    this.enrichExportRowsForDisplay(accumulated);
    return accumulated;
  }

  private downloadPdfWithRows(rows: any[]): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!rows || rows.length === 0) {
        resolve();
        return;
      }
      rows.forEach((e: any) => {
        if (e.claimId) {
          e.newClaimId = e.claimId.replace(/\D/g, '');
        }
        if (e.dueBalance == null) {
          e.dueBalance = 0;
        }
      });
      const data = {
        fileName: 'List_Of_Claims',
        data: rows,
        clientName: this.clientName,
        currentTeamName: this.currentTeamName.teamName
      };
      this.appService.othersTeamPdfDownload(data, 'pdf', (res: any) => {
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

  getMonthName(month: any) {
    const monthNames = [
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    return monthNames[month];
  }

  buildAndDownloadCsvFromRows(sourceRows: any[]) {
    const headerConfigs = {
      default: [ // medical iv , need to hold, ortho, patient calling, ppo iv, quality, lc3
        "Office", "Claim ID", "Patient ID", "Patient Name",
        "DOS", "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Estimated Amount", "Assigned By",
        "Last Team's Remarks", "Pending Since Date", "Due Date",
      ],
      teamCdp: [
        "Office", "Claim ID", "Patient ID", "Patient Name",
        "DOS", "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Estimated Amount", "Assigned By",
        "Last Team's Remarks", "Pending Since Date", "Current Status",
        "Current Action Required", "Due Date", "Provider Speciality"
      ],
      teamAging: [
        "Office", "Claim ID", "Patient ID", "Patient Name",
        'DOS', "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Est. Amount", "Assigned By",
        "Last Team's Remarks", "Pending Since Date", "Current Status",
        "Current Action Required", "Due Date", "Provider Speciality"
      ],
      teamOffice: [
        "Office", "Patient Name", 'DOS', "TFL", "Age Bracket",
        "Insurance Name", "Insurance Type", "Claim Type", "Est. Amount",
        "Assigned By", "Last Team's Remarks", "Pending Since Date", "Due Date",
      ],
      teamPosting: [
        "Office", "Claim ID", "Patient ID", "Patient Name",
        'DOS', "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Est. Amount", "Pending Since Date",
        "Current Status", "Current Action Required", "Due Date", "Provider Speciality"
      ],
      teamCredentialing: [
        "Office", "Claim ID", "Patient ID", "Patient Name",
        'DOS', "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Est. Amount", "Assigned By",
        "Last Team's Remarks", "Pending Since Date", "Current Status",
        "Current Action Required", "Due Date",
      ],
      teamPatientStatement: [
        "Office", "Claim ID", "Patient ID", "Patient Name",
        "DOS", "Claim Age", "TFL", "Age Bracket", "Insurance Name",
        "Insurance Type", "Claim Type", "Estimated Amount", "Assigned By",
        "Last Team's Remarks", "Pending Since Date", "Current Status",
        "Current Action Required", "Due Date", "Provider Speciality", "Due Balance"
      ],
    };

    if (this.staticUtil.selectedTeam() == AppConstants.CDP_TEAM) {
      this.selectedHeaders = headerConfigs.teamCdp;
    } else if (this.staticUtil.selectedTeam() == AppConstants.AGING_TEAM) {
      this.selectedHeaders = headerConfigs.teamAging;
    } else if (this.staticUtil.selectedTeam() == AppConstants.OFFICE_TEAM) {
      this.selectedHeaders = headerConfigs.teamOffice;
    } else if (this.staticUtil.selectedTeam() == AppConstants.PAYMENT_POSTING_TEAM) {
      this.selectedHeaders = headerConfigs.teamPosting;
    } else if (this.staticUtil.selectedTeam() == AppConstants.CREDENTIALING_TEAM) {
      this.selectedHeaders = headerConfigs.teamCredentialing;
    } else if (this.staticUtil.selectedTeam() == AppConstants.PATIENT_STATEMENT_TEAM) {
      this.selectedHeaders = headerConfigs.teamPatientStatement;
    } else {
      this.selectedHeaders = headerConfigs.default;
    }

    let options: any = {
      showLabels: true,
      headers: this.selectedHeaders
    }
    let excelData: any = [...sourceRows];
    excelData = excelData.map((e: any) => {
      if (e.dos) {
        let date: Date = new Date(e.dos);
        e = { ...e, dos: `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}` };
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
      if (e.lastTeam == null) {
        e = { ...e, lastTeam: '-' }
      }
      if (e.lastTeamRemark == null) {
        e = { ...e, lastTeamRemark: '-' }
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

    if (this.staticUtil.selectedTeam() == AppConstants.CDP_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Current Status": e.claimStatus ? e.claimStatus : "N/A",
          "Current Action Required": e.nextAction ? e.nextAction : "N/A",
          "Due Date": e.dueDateSort,
          "Provider Speciality": e.providerSpeciality ? e.providerSpeciality : "N/A",
        }
      })
    }
    else if (this.staticUtil.selectedTeam() == AppConstants.AGING_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Current Status": e.claimStatus ? e.claimStatus : "N/A",
          "Current Action Required": e.nextAction ? e.nextAction : "N/A",
          "Due Date": e.dueDateSort,
          "Provider Speciality": e.providerSpeciality ? e.providerSpeciality : "N/A",
        }
      })
    }
    else if (this.staticUtil.selectedTeam() == AppConstants.OFFICE_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Due Date": e.dueDateSort,
        }
      })
    }
    else if (this.staticUtil.selectedTeam() == AppConstants.PAYMENT_POSTING_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Pending Since Date": e.pendingSince,
          "Current Status": e.claimStatus ? e.claimStatus : "N/A",
          "Current Action Required": e.nextAction ? e.nextAction : "N/A",
          "Due Date": e.dueDateSort,
          "Provider Speciality": e.providerSpeciality ? e.providerSpeciality : "N/A",
        }
      })
    }
    else if (this.staticUtil.selectedTeam() == AppConstants.CREDENTIALING_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Current Status": e.claimStatus ? e.claimStatus : "N/A",
          "Current Action Required": e.nextAction ? e.nextAction : "N/A",
          "Due Date": e.dueDateSort,
        }
      })
    }
    else if (this.staticUtil.selectedTeam() == AppConstants.PATIENT_STATEMENT_TEAM) {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Current Status": e.claimStatus ? e.claimStatus : "N/A",
          "Current Action Required": e.nextAction ? e.nextAction : "N/A",
          "Due Date": e.dueDateSort,
          "Provider Speciality": e.providerSpeciality ? e.providerSpeciality : "N/A",
          "Due Balance": e.dueBalance ? '$' + formatNumber(e.dueBalance, this.locale, '.0-0').toString() : "$0",
        }
      })
    }
    else {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim ID": e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          'Age Bracket': e.ageBracket,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Claim Type": e.claimType,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Assigned By": e.lastTeam,
          "Last Team's Remarks": e.lastTeamRemark,
          "Pending Since Date": e.pendingSince,
          "Due Date": e.dueDateSort,
        }
      }) //method aligns the header to the value in CSV.
    }
    // excelData = excelData.map(
    //   ({ claimId, opdos, opdt, secTotal, uuid, statusType, EstAmount, ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.  // values which excluded are coming as undefined so that's why its commented out.

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    //console.log(excelData.sort());
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
  }

  exportToCsv(): void {
    this.openExportModal('csv');
  }

  /*showClaimIdWithDigits() {
    this.filteredItems.forEach((e: any) => {
      if (e.claimId) {
        e.newClaimId = e.claimId.replace(/\D/g, "");  // returns only digits
      }
    });

  }**/

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

  isOtherTLExist(callback: any) {
    let params: any = {
      "claimUuid": this.currentClaimUuid,
      "assignToTeamId": this.submitBtnConfig['otherTeamId'][this.currentClaimUuid] ? +this.submitBtnConfig['otherTeamId'][this.currentClaimUuid] : null,
    };

    // console.log(params);
    this.appService.isOtherTeamTLExist(params, (res: any) => {
      if (res.data.responseStatus) {
        callback(res.data.responseStatus);
      } else {
        this.errorMessage = res.data.message;
        setTimeout(() => {
          this.errorMessage = '';
        }, 6000);
      }
    })
  }

  closeError() {
    this.errorMessage = '';
  }

  async filterOptionInsuranceType() {
    this.filteredInsuranceType = await this.claimDetail.map((e: any) => { return { insuranceType: e.prName, checked: true } });
    if (this.filteredInsuranceType.length > 2) {
      this.filteredInsuranceType = this.removeDuplicateValues(this.filteredInsuranceType, 'insuranceType');
    }
    this.filterInsuranceType();
  }

  async filterOptionCurrentStatus() {
    this.filteredCurrentStatus = await this.claimDetail.map((e: any) => { return { currentStatus: e.claimStatus, checked: true } });
    if (this.filteredCurrentStatus.length > 1) {
      this.filteredCurrentStatus = this.removeDuplicateValues(this.filteredCurrentStatus, 'currentStatus');
    }
    this.filterCurrentStatus();
  }

  async filterOptionNextActionRequired() {
    this.filteredNextActionRequired = await this.claimDetail.map((e: any) => { return { nextActionRequired: e.nextAction, checked: true } });
    if (this.filteredNextActionRequired.length > 1) {
      this.filteredNextActionRequired = this.removeDuplicateValues(this.filteredNextActionRequired, 'nextActionRequired');
    }
    this.filterNextActionRequired();
  }

  async filterOptionProviderSpeciality() {
    this.filteredProviderSpeciality = await this.claimDetail.map((e: any) => { return { providerSpeciality: e.providerSpeciality, checked: true } });
    if (this.filteredProviderSpeciality.length > 1) {
      this.filteredProviderSpeciality = this.removeDuplicateValues(this.filteredProviderSpeciality, 'providerSpeciality');
    }
    this.filterProviderSpeciality();
  }

  async filterOptionSelectAging() {
    this.filteredSelectAging = await this.claimDetail.map((e: any) => { return { selectAging: e.diffForPaymentPosting ? "Unlocked" : "Locked", checked: true } });
    if (this.filteredSelectAging.length > 1) {
      this.filteredSelectAging = this.removeDuplicateValues(this.filteredSelectAging, 'selectAging');
    }
    this.filterSelectAging();
  }

  removeDuplicateValues(filterValue: any, property: any) {
    let newArray: any = [];
    let uniqueObject: any = {};
    let objTitle: any;
    for (let i in filterValue) {
      objTitle = filterValue[i][property];
      uniqueObject[objTitle] = filterValue[i];
    }
    for (let i in uniqueObject) {
      newArray.push(uniqueObject[i]);
    }
    // newArray.pop();
    return newArray;
  }

  filterInsuranceType(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredInsuranceType.length; i++) {
      if (this.filteredInsuranceType[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.insuranceType = isAllSelected;
    this.pendingServerFilters.insuranceTypeFilter = isAllSelected ? ''
      : this.filteredInsuranceType.filter((x: any) => x.checked).map((x: any) => x.insuranceType).join(',');
    this._persistOtwFilterState();
  }

  filterCurrentStatus(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredCurrentStatus.length; i++) {
      if (this.filteredCurrentStatus[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.currentStatus = isAllSelected;
    this.pendingServerFilters.currentStatusFilter = isAllSelected ? ''
      : this.filteredCurrentStatus.filter((x: any) => x.checked).map((x: any) => x.currentStatus).join(',');
    this._persistOtwFilterState();
  }

  filterNextActionRequired(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredNextActionRequired.length; i++) {
      if (this.filteredNextActionRequired[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.nextActionRequired = isAllSelected;
    this.pendingServerFilters.nextActionFilter = isAllSelected ? ''
      : this.filteredNextActionRequired.filter((x: any) => x.checked).map((x: any) => x.nextActionRequired).join(',');
    this._persistOtwFilterState();
  }

  filterProviderSpeciality(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredProviderSpeciality.length; i++) {
      if (this.filteredProviderSpeciality[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.providerSpeciality = isAllSelected;
    this.pendingServerFilters.providerSpecialityFilter = isAllSelected ? ''
      : this.filteredProviderSpeciality.filter((x: any) => x.checked).map((x: any) => x.providerSpeciality).join(',');
    this._persistOtwFilterState();
  }

  filterSelectAging(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.selectAging = true;
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredSelectAging.length; i++) {
      if (this.filteredSelectAging[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.selectAging = isAllSelected;
    // selectAging is a client-computed field (diffForPaymentPosting), keep client-side
    if (isAllSelected) {
      this.filteredItems = this.claimDetail;
    } else {
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredSelectAging.some((checkbox: any) => {
          return checkbox.checked && checkbox.selectAging === (item.diffForPaymentPosting ? 'Unlocked' : 'Locked');
        });
      });
    }
  }

  addOrRemoveInsuranceType() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  addOrRemoveCurrentStatus() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  addOrRemoveNextActionRequired() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  addOrRemoveProviderSpeciality() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  addOrRemoveSelectAging() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceName'] === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return checkbox.checked && checkbox['officeName'] === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return checkbox.checked && checkbox['insuranceType'] === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return checkbox.checked && checkbox['currentStatus'] === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox['nextActionRequired'] === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return checkbox.checked && checkbox['providerSpeciality'] === item['providerSpeciality'];
      });
    });
  }

  async filterOptionClaimType() {
    this.filteredClaimType = await this.claimDetail.map((e: any) => { return { claimType: e.claimId.endsWith("P") ? "Primary" : "Secondary", checked: true } });

    if (this.filteredClaimType.length > 2) {
      this.filteredClaimType = this.removeDuplicateValues(this.filteredClaimType, 'claimType');
    }
    this.filterClaimType();
  }

  filterClaimType(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredClaimType.length; i++) {
      if (this.filteredClaimType[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.claimType = isAllSelected;
    this.pendingServerFilters.claimTypeFilter = isAllSelected ? ''
      : this.filteredClaimType.filter((x: any) => x.checked).map((x: any) => x.claimType).join(',');
    this._persistOtwFilterState();
  }

  addOrRemoveFilterClaimType() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  async filterOptionLastTeam() {
    this.filteredLastTeam = await this.claimDetail.map((e: any) => { return { lastTeam: e.lastTeam, checked: true } });

    if (this.filteredLastTeam.length > 2) {
      this.filteredLastTeam = this.removeDuplicateValues(this.filteredLastTeam, 'lastTeam');
    }
    this.filterLastTeam();
  }

  filterLastTeam(e?: any, filterProperty?: any) {
    if (!e) {
      this._onFilterDropdownClearBranch();
      return;
    }
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredLastTeam.length; i++) {
      if (this.filteredLastTeam[i].checked == false) { isAllSelected = false; break; }
      }
      this.isFilterAllSelected.lastTeam = isAllSelected;
    this.pendingServerFilters.lastTeamFilter = isAllSelected ? ''
      : this.filteredLastTeam.filter((x: any) => x.checked).map((x: any) => x.lastTeam).join(',');
    this._persistOtwFilterState();
  }

  addOrRemoveFilterLastTeam() {
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox['ageBracket'] === item['ageBracket'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceName']) === item['insuranceName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredOfficeName.some((checkbox: any) => {
        return (checkbox.checked && checkbox['officeName']) === item['officeName'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredInsuranceType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['insuranceType']) === item['insuranceType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredClaimType.some((checkbox: any) => {
        return (checkbox.checked && checkbox['claimType']) === item['claimType'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredCurrentStatus.some((checkbox: any) => {
        return (checkbox.checked && checkbox['currentStatus']) === item['currentStatus'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredNextActionRequired.some((checkbox: any) => {
        return (checkbox.checked && checkbox['nextActionRequired']) === item['nextActionRequired'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredProviderSpeciality.some((checkbox: any) => {
        return (checkbox.checked && checkbox['providerSpeciality']) === item['providerSpeciality'];
      });
    });
    this.filteredItems = this.filteredItems.filter((item: any) => {
      return this.filteredSelectAging.some((checkbox: any) => {
        return (checkbox.checked && checkbox['selectAging']) === item.diffForPaymentPosting ? "Unlocked" : "Locked";
      });
    });
  }

  fetchClaimsLead(subType: string, page: number = 0) {
    this.loader.listClaimLoader = true;
    let ths = this;
    this.currentPage = page;
    const knownTotalCountLead = page === 0 ? 0 : this.storedTotalCount;
    ths.appService.fetchLeadClaimDet(ths.selectedBtype, subType, page, ths.pageSize, knownTotalCountLead, (res: any) => {
      if (res.status === 200) {
        ths.totalCount = res.data.totalCount;
        ths.totalPages = res.data.totalPages;
        ths.currentPage = res.data.page;
        ths.goToPageInput = ths.currentPage + 1;
        ths.storedTotalCount = res.data.totalCount;
        ths.claimDetail = this.removePrefix(res.data.claims);
        let data: any = ths.claimDetail.map((e: any) => {
          if (e.claimId.endsWith("_P")) {
            e['EstAmount'] = e.primeSecSubmittedTotal;
          } else {
            e['EstAmount'] = e.secTotal;
          }
          e['dueDateSort'] = e.followUpDate == null ? e.pendingSince : e.followUpDate;
          return e;
        })
        ths.claimDetail = data;
        // ths.claimDetail =  res.data;

        this.fetchOfficeByUuid();
        if (!this._hasActiveFilters()) {
        this.filterOptionAgeBracket();
        this.filterOptionInsuranceName();
        this.filterOptionInsuranceType();
        this.filterOptionClaimType();
        this.filterOptionLastTeam();
        this.filterOptionCurrentStatus();
        this.filterOptionNextActionRequired();
        this.filterOptionProviderSpeciality();
          this.filterOptionSelectAging();
        }
        this.showAgeBracket_WithColor_AndClaimIdDigits();
        this.checkDiffForPaymentPosting();
        this._afterFetchSyncUi();
        ths.loader.listClaimLoader = false;
      }
    }, { sortBy: this.serverSortBy, sortOrder: this.serverSortOrder, ...this.serverFilters });
  }

  switchTab(tab: any) {
    if (!this.claimDetail) return;
    this.currentPage = 0;
    this.storedTotalCount = 0;
    this.goToPageInput = 1;
    this.serverFilters = this._emptyOtwFilters();
    this.pendingServerFilters = this._emptyOtwFilters();
    this._persistOtwFilterState();
    if (tab == 'unSubmitted') {
      this.tabValue = 'unSubmitted';
      this.tabSwitch.unSubmitted = true;
      this.tabSwitch.submitted = false;
      this.tabSwitch.myClaims = false;
      this.fetchClaims("unSubmitted", 0);
    }
    if (tab == 'submitted') {
      this.tabValue = 'submitted';
      this.tabSwitch.unSubmitted = false;
      this.tabSwitch.submitted = true;
      this.tabSwitch.myClaims = false;
      this.fetchClaims("submitted", 0);
    }
    if (tab == 'myClaims') {
      this.tabValue = 'myClaims';
      this.tabSwitch.unSubmitted = false;
      this.tabSwitch.submitted = false;
      this.tabSwitch.myClaims = true;
      this.fetchClaimsLead('MyClaims', 0);
    }
  }

  onPageChange(page: number) {
    if (page < 0 || page >= this.totalPages || page === this.currentPage) return;
    this.clearAssigmentArray();
    if (this.tabSwitch.myClaims) {
      this.fetchClaimsLead('MyClaims', page);
    } else {
      this.fetchClaims(this.tabValue, page);
    }
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    localStorage.setItem(this.PAGE_SIZE_KEY, String(size));
    this.currentPage = 0;
    this.goToPageInput = 1;
    this.clearAssigmentArray();
    if (this.tabSwitch.myClaims) {
      this.fetchClaimsLead('MyClaims', 0);
    } else {
      this.fetchClaims(this.tabValue, 0);
    }
  }

  get pageNumbers(): number[] {
    const total = this.totalPages;
    if (total <= 7) return Array.from({ length: total }, (_, i) => i);
    const current = this.currentPage;
    const pages = new Set<number>([0, total - 1, current]);
    if (current > 1) pages.add(current - 1);
    if (current < total - 2) pages.add(current + 1);
    return Array.from(pages).sort((a, b) => a - b);
  }

  get pageStartItem(): number {
    if (this.totalCount === 0) return 0;
    return this.currentPage * this.pageSize + 1;
  }

  get pageEndItem(): number {
    if (this.totalCount === 0) return 0;
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalCount);
  }

  goToSpecificPage() {
    if (this.totalPages === 0) return;
    const targetPage = Number(this.goToPageInput);
    if (!Number.isFinite(targetPage)) {
      this.goToPageInput = this.currentPage + 1;
      return;
    }
    const normalizedPage = Math.max(1, Math.min(targetPage, this.totalPages));
    this.goToPageInput = normalizedPage;
    this.onPageChange(normalizedPage - 1);
  }

  onGoToPageEnter(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.goToSpecificPage();
    }
  }

  showOrHideColumns(currentTeamName: any) {

    const columnsToShow = this.columnPermissionsByTeam[currentTeamName.teamName] || [];
    Object.keys(this.showColumns).forEach(column => {
      this.showColumns[column] = columnsToShow.includes(column);
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

  get staticUtil(): any {
    return Utils;
  }

  /** Safe when `(change)` is on `<input type="checkbox">` (always use that, not the wrapping `<label>`). */
  private readCheckboxChecked(ev: Event): boolean {
    const t = ev.target as HTMLElement | null;
    if (t && (t as HTMLInputElement).type === 'checkbox') {
      return (t as HTMLInputElement).checked;
    }
    return false;
  }

  /** Sets `diffForPaymentPosting` / `newClaimId` for rows (grid or bulk fetch). */
  private applyDiffForPaymentPostingToRows(rows: any[]): void {
    const currentDate: any = new Date();
    rows.forEach((e: any) => {
      if (e.claimId) {
        e.newClaimId = e.claimId.replace(/\D/g, '');
      }
      if (e.updatedDate && this.currentTeamName.unFormatedName == 'PAYMENT_POSTING') {
        const updateDate: any = new Date(e.pendingSince);
        const diffDays: any = Math.floor((currentDate - updateDate) / (1000 * 60 * 60 * 24));
        e.diffForPaymentPosting = diffDays > 15;
      }
    });
  }

  checkDiffForPaymentPosting() {
    this.applyDiffForPaymentPostingToRows(this.filteredItems);
  }

  /** Same client-only Lock/Unlocked column filter as the table (`_reapplySelectAgingClientFilter`). */
  private applyClientSelectAgingFilterToRows(rows: any[]): any[] {
    if (!this.filteredSelectAging?.length) {
      return rows;
    }
    const allSel = this.filteredSelectAging.every((x: any) => x.checked);
    if (allSel) {
      return rows;
    }
    return rows.filter((item: any) =>
      this.filteredSelectAging.some(
        (checkbox: any) =>
          checkbox.checked &&
          checkbox.selectAging === (item.diffForPaymentPosting ? 'Unlocked' : 'Locked')
      )
    );
  }

  /** All rows matching applied server filters + sort + tab, then client Select Aging filter (same as export + aging column). */
  private async getAllRowsForBulkActions(): Promise<any[]> {
    if (this.totalCount === 0) {
      return [];
    }
    const raw = await this.collectClaimsForExport(this.totalCount);
    this.applyDiffForPaymentPostingToRows(raw);
    return this.applyClientSelectAgingFilterToRows(raw);
  }

  selectTransferClaims(event: any, data: any) {
    this.bulkAgingSelectAll = false;
    let isSelected: boolean = event.target.checked;
    if (isSelected) {
      this.selectedClaimsTransfer.claimUuid.push(data.uuid);
      data.selectAging = true;
    } else {
      this.selectedClaimsTransfer.claimUuid = this.selectedClaimsTransfer.claimUuid.filter((uuid: string) => uuid !== data.uuid);
      this.selectAllAging = null;
      data.selectAging = null;
    }
    let all: any = true;
    this.filteredItems.forEach((x: any) => {
      if (x.diffForPaymentPosting) {
        if (x.selectAging == null || x.selectAging == false)
          all = null;
      }
    });
    this.selectAllAging = all;
  }

  transferClaimsConfirmModal() {
    this.selectedClaimsTransfer.remarks = ''
    if (this.selectedClaimsTransfer.claimUuid.length > 0) {
      this.showTransferClaimModal = true;
    }
    else {
      this.errorMessage = "Please select at least one claim";
      setTimeout(() => {
        this.errorMessage = '';
      }, 2000);
      this.showTransferClaimModal = false;
    }
  }


  closeConfirmationModal() {
    this.showTransferClaimModal = false;
  }

  transferClaim() {
    if (this.selectedClaimsTransfer.claimUuid.length > 0) {
      this.sendToAging = true;
      this.appService.checkAnyTLOrAssoExist({ "claimUuids": this.selectedClaimsTransfer.claimUuid, "teamId": AppConstants.AGING_ID }, (res: any) => {
        if (res.data.responseStatus) {
          this.appService.claimTransferToAging(this.selectedClaimsTransfer, (res: any) => {
            if (res.status == 200) {
              this.closeConfirmationModal();
              this.alert.alertMsg = "Success";
              this.alert.showAlert = true;
              location.reload();
            }
            else {
              this.errorMessage = 'Failed to Transfer the claims';
              setTimeout(() => {
                this.errorMessage = '';
              }, 2000);
            }
          });
        }
        else {
          this.closeConfirmationModal();
          this.errorMessage = res.data.message;
          this.sendToAging = false;
          setTimeout(() => {
            this.errorMessage = '';
          }, 2000);
        }
      });
    }
    else {
      this.closeConfirmationModal();
      this.errorMessage = "Please select at least one claim";
      setTimeout(() => {
        this.errorMessage = '';
      }, 2000);
      this.showTransferClaimModal = false;
    }
  }

  async selectAllClaimsToAging(event: Event) {
    const isSelected = this.readCheckboxChecked(event);
    if (!isSelected) {
      this.bulkAgingSelectAll = false;
      this.selectAllAging = null;
      this.selectedClaimsTransfer.claimUuid = [];
    this.filteredItems.forEach((x: any) => {
      x.selectAging = null;
    });
      this.cdr.detectChanges();
      return;
    }
    if (this.hasUnappliedPendingFilters) {
      const inp = event.target as HTMLInputElement;
      if (inp?.type === 'checkbox') {
        inp.checked = false;
      }
      this.errorMessage = 'Apply filters before selecting all eligible claims.';
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
      return;
    }
    this.loader.bulkAgingLoader = true;
    try {
      const rows = await this.getAllRowsForBulkActions();
      const eligible = rows.filter((x: any) => x.diffForPaymentPosting);
      this.selectedClaimsTransfer.claimUuid = eligible.map((x: any) => x.uuid || x.claimUuid);
      const idSet = new Set(this.selectedClaimsTransfer.claimUuid);
      this.filteredItems.forEach((x: any) => {
        const uid = x.uuid || x.claimUuid;
        x.selectAging = idSet.has(uid) ? true : null;
      });
      this.selectAllAging = eligible.length > 0 ? true : null;
      this.bulkAgingSelectAll = eligible.length > 0;
    } catch {
      const inp = event.target as HTMLInputElement;
      if (inp?.type === 'checkbox') {
        inp.checked = false;
      }
      this.selectAllAging = null;
      this.selectedClaimsTransfer.claimUuid = [];
      this.errorMessage = 'Could not load all matching claims. Try again.';
      setTimeout(() => {
        this.errorMessage = '';
      }, 3000);
    } finally {
      this.loader.bulkAgingLoader = false;
      this.cdr.detectChanges();
    }
  }

  createClaimAssignmentList(event: Event, data: any) {
    const checked = (event.target && (event.target as HTMLInputElement).type === 'checkbox')
      ? (event.target as HTMLInputElement).checked
      : false;
    if (checked) {
      this.reassignmentSelectAllMode = false;
      this.listofClaimsForAssignAction.push(data);
      data.assignAction = true;
      if (this.listofClaimsForAssignAction.length == this.filteredItems.length) {
        this.reassignmentSelectBox.nativeElement.checked = true;
      }
    } else {
      this.reassignmentSelectAllMode = false;
      this.reassignmentSelectBox.nativeElement.checked = false;
      this.listofClaimsForAssignAction = this.listofClaimsForAssignAction.filter((obj: any) => {
        return obj.uuid !== data.uuid;
      });
    }
    this.cdr.detectChanges();
  }

  async selectAllReAssigment(event: Event) {
    const ths = this;
    const checked = ths.readCheckboxChecked(event);
    if (!checked) {
    ths.selectedOfficeNames = [];
    ths.listofClaimsForAssignAction = [];
      ths.reassignmentSelectAllMode = false;
    ths.filteredItems.forEach((element: any) => {
      element.assignAction = false;
    });
      ths.cdr.detectChanges();
      return;
    }
    ths.selectedOfficeNames = [];
    if (ths.hasUnappliedPendingFilters) {
      const inp = event.target as HTMLInputElement;
      if (inp?.type === 'checkbox') {
        inp.checked = false;
      }
      ths.errorMessage = 'Apply filters before selecting all claims for reassignment.';
      setTimeout(() => {
        ths.errorMessage = '';
      }, 3000);
      ths.cdr.detectChanges();
      return;
    }
    ths.loader.bulkReassignmentLoader = true;
    ths.cdr.detectChanges();
    try {
      const rows = await ths.getAllRowsForBulkActions();
      ths.listofClaimsForAssignAction = [...rows];
      ths.reassignmentSelectAllMode = rows.length > 0;
      const idSet = new Set(rows.map((r: any) => r.uuid || r.claimUuid));
      ths.filteredItems.forEach((el: any) => {
        const uid = el.uuid || el.claimUuid;
        el.assignAction = idSet.has(uid);
      });
      if (ths.reassignmentSelectBox?.nativeElement) {
        ths.reassignmentSelectBox.nativeElement.checked = rows.length > 0;
      }
    } catch {
      const inp = event.target as HTMLInputElement;
      if (inp?.type === 'checkbox') {
        inp.checked = false;
      }
      ths.errorMessage = 'Could not load all matching claims. Try again.';
      setTimeout(() => {
        ths.errorMessage = '';
      }, 3000);
    } finally {
      ths.loader.bulkReassignmentLoader = false;
      ths.cdr.detectChanges();
    }
  }

  openAssigmentPopUp() {
    this.openAssignmentModal();
  }

  clearAssigmentArray() {
    this.listofClaimsForAssignAction = [];
    this.reassignmentSelectAllMode = false;
    this.bulkAgingSelectAll = false;
    if (this.reassignmentSelectBox?.nativeElement) {
    this.reassignmentSelectBox.nativeElement.checked = false;
    }
    this.filteredItems.forEach((element: any) => {
      element.assignAction = false;
    });
  }

  openAssignmentModal() {
    this.modelElement1.modal = document.getElementById("assgn-modal");
    this.modelElement1.span = document.getElementsByClassName("close")[0];
    this.modelElement1.modal.style.display = "block";
    this.radiox1.nativeElement.checked = false;
    this.radiox2.nativeElement.checked = false;
    this.radiox3.nativeElement.checked = false;
    this.radiox1.nativeElement.parentNode.classList.remove("active");
    this.radiox2.nativeElement.parentNode.classList.remove("active");
    this.radiox3.nativeElement.parentNode.classList.remove("active");

  }

  setAssignType(type: number) {
    this.assignmentTypeSelect = type;
    if (this.assignmentType.assignSameTeam == type) {
      this.fetchUserNamesForOffices();
    } else if (this.assignmentType.assignOtherTeam == type) {

    } else if (this.assignmentType.unAssign == type) {

    }
  }

  closeModalAs() {
    this.modelElement1.modal.style.display = "none";
  }



  makeAsssignment(type: string) {
    let ths = this;
    if (ths.assignreason.nativeElement.value.trim() === '') return;
    let teamId = -1;
    let userId = "";
    let claimIds: Array<string> = [];
    if (ths.assignmentType.assignSameTeam === type) {
      let e: any = document.getElementById("selectUserAs");
      if (e.value.trim() === 'Select User') return;
      userId = e.value;
    } else if (ths.assignmentType.assignOtherTeam === type) {
      let e: any = document.getElementById("selectTeamAs");
      if (e.value.trim() === '') return;

      teamId = e.value;
    } else if (ths.assignmentType.unAssign === type) {

    }
    ths.listofClaimsForAssignAction.forEach((element: any) => {
      claimIds.push(element.uuid);
    });
    ths.loader.assignLoader = true;
    let obj: any = { "claimIds": claimIds, "teamId": teamId, "userId": userId, "type": type , "comment": ths.assignreason.nativeElement.value };
    ths.appService.reAssignClaimFromList(obj, (res: any) => {
      if (res.status === 200) {
        ths.closeModalAs();
        ths.radiox1.nativeElement.parentNode.classList.remove("active");
        ths.radiox2.nativeElement.parentNode.classList.remove("active");
        ths.radiox3.nativeElement.parentNode.classList.remove("active");
        ths.clearAssigmentArray();
        ths.loader.assignLoader = false;
        if (ths.tabSwitch.submitted) {
          ths.switchTab("submitted");
        } else if (ths.tabSwitch.unSubmitted) {
          ths.switchTab("unSubmitted");
        } else if (ths.tabSwitch.myClaims) {
          ths.switchTab("myClaims");
        }


      }
    });

  }

  fetchUserNamesForOffices() {
    let ths = this;
    const removeDups: any = null;//not needed for now incase needed uncomment the code
    /*ths.selectedOfficeNames = [];
    ths.listofClaimsForAssignAction.forEach((element: any) => {
      ths.selectedOfficeNames.push(element.officeName);
    });
    const removeDups = (arr: string[]): string[] => {
      return [...new Set(ths.selectedOfficeNames)];
    };*/
    ths.loader.assignLoader = true;
    ths.appService.fetchUserNamesByTeam({ "offices": removeDups }, (res: any) => {
      if (res.status === 200) {
        ths.assgnmentUsers = res.data;
        ths.loader.assignLoader = false;

      }
    });
  }

  assignSwitch(type: number) {
    if (type == this.assignmentTypeSelect) {
      return true;
    } else if (type == this.assignmentTypeSelect) {
      return true;
    } else if (type == this.assignmentTypeSelect) {
      return true;
    }
    return false;
  }
}