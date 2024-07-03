import { Component, OnInit, LOCALE_ID, Inject, HostListener, Input } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';
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
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  isSorted: any = {};
  loader: any = { 'billingLoader': false, 'listClaimLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  showFilteredDropdown: any = { 'officeName': false, 'insuranceName': false, insuranceType: false, claimType: false, lastTeam: false, 'currentTeam': false, 'currentStatus': false, 'nextActionRequired': false, 'providerSpeciality': false, 'selectAging': false };
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  isFilterAllSelected: any = { 'officeName': false, 'ageBracket': false, 'insuranceName': false, 'insuranceType': false, claimType: false, lastTeam: false, 'currentTeam': false, 'currentStatus': false, 'nextActionRequired': false, 'providerSpeciality': false, 'selectAging': false };
  clientName: string = '';
  isFilterValueExist: boolean = false;
  fliterName: string = '';

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
  selectedClaimsTransfer:any = {"claimUuid":[],"remarks":""};
  filteredInsuranceName: any = [];
  filteredInsuranceType: any = [];
  filteredClaimType: any = [];
  filteredLastTeam: any = [];
  filteredCurrentStatus: any = [];
  filteredNextActionRequired: any = [];
  filteredProviderSpeciality: any = [];
  filteredSelectAging: any = [];
  tabSwitch: any = { 'submitted': false, 'unSubmitted': true };
  tabValue: any;
  showColumns: any = { "currentStatus": false, "nextActionRequired": false, "dueDate": false, "providerSpeciality": false, "dueBalance": false, "showAttach": false };
  columnPermissionsByTeam: any = {
    'Aging': ['currentStatus', 'nextActionRequired', 'dueDate', 'providerSpeciality'],
    'CDP': ['currentStatus', 'nextActionRequired', 'dueDate', 'providerSpeciality'],
    'Credentialing': ['currentStatus', 'nextActionRequired', 'showAttach','dueDate'],
    'Patient Statement': ['currentStatus', 'nextActionRequired', 'providerSpeciality', 'dueBalance','dueDate'],
    'Payment Posting': ['currentStatus', 'nextActionRequired', 'providerSpeciality','dueDate'],
    'LC3': ['showAttach','dueDate'],
    'Office': ['showAttach','dueDate'],
    'Ortho': ['showAttach','dueDate'],
    'PPO IV': ['showAttach','dueDate'],
    'Medicaid IV': ['showAttach','dueDate'],
    'Need to hold': ['showAttach'],
    'Quality': ['showAttach'],
    'Patient Calling': ['showAttach','dueDate'],
  };

  showTooltipConfig:any={};
  selectAllAging:any=null;

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
    this.currentTeamName = this.appConstants.teamData.find((e: any) => e.teamId == Utils.selectedTeam());
    this.tabSwitch.unSubmitted = false;
    this.tabSwitch.submitted = true;
    this.tabValue='submitted';
    this.fetchClaims("submitted");
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

      }
    })
  }

  fetchClaims(subType: string) {
    this.loader.listClaimLoader = true;
    let ths = this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, subType, (res: any) => {
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
        console.log(ths.claimDetail);

        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
        this.filterOptionAgeBracket();
        this.filterOptionInsuranceName();
        this.filterOptionInsuranceType();
        this.filterOptionClaimType();
        this.filterOptionLastTeam();
        this.filterOptionCurrentStatus();
        this.filterOptionNextActionRequired();
        this.filterOptionProviderSpeciality();
        this.showClaimIdWithDigits();
        this.showAgeBracket_WithColor_AndClaimIdDigits();
        this.checkDiffForPaymentPosting();
        this.filterOptionSelectAging();
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
        ele['insuranceName'] = ele.primaryInsurance;
        ele['insuranceType'] = ele.prName;
        ele['currentStatus'] = ele.claimStatus;
        ele['nextActionRequired'] = ele.nextAction;
        ele['providerSpeciality'] = ele.providerSpeciality;
        ele['selectAging'] = ele.selectAging;
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
      this.addOrRemoveFilterOffice();
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
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredAgeBracket.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
      });
    });
    this.addOrRemoveFilterAgeBracket();
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
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.insuranceName = true;
    } else {

      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredInsuranceName.length; i++) {
        if (this.filteredInsuranceName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.insuranceName = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredInsuranceName.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveInsuranceName();
    }
  }

  filterOptionAgeBracket() {
    if (this.isFilterValueExist) {
      this.filteredAgeBracket = [];
    }
    this.filteredAgeBracket.push({ 'checked': true, 'ageBracket': '0-30' }, { 'checked': true, 'ageBracket': '31-60' }, { 'checked': true, 'ageBracket': '61-90' }, { 'checked': true, 'ageBracket': '90+' });
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
    // filterName == 'officeName' ? this.showFilteredDropdown.officeName = true : this.showFilteredDropdown.officeName = false;
    // filterName == 'ageBracket' ? this.showFilteredDropdown.ageBracket = true : this.showFilteredDropdown.ageBracket = false;
    this.fliterName = filterName;
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
              console.log(res);
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



  downloadPdf() {
    this.loader.exportPDFLoader = true;
    if (this.filteredItems.length != 0) {
      this.filteredItems.forEach((e: any) => {
        if (e.claimId) {
          e.newClaimId = e.claimId.replace(/\D/g, "");
        }
      })
      let data = { "fileName": "List_Of_Claims", "data": this.filteredItems, "clientName": this.clientName, "currentTeamName": this.currentTeamName.teamName };
      this.appService.othersTeamPdfDownload(data, "pdf", (res: any) => {
        if (res.status === 200) {
          this.date = new Date();
          this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
          console.log(res.body);
          this.downloadService.saveBolbData(res.body, `${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}.pdf`);
          this.loader.exportPDFLoader = false;
        } else {
          console.log("something went wrong");
          this.loader.exportPDFLoader = false;
        }
      })
    }
  }

  getMonthName(month: any) {
    const monthNames = [
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    return monthNames[month];
  }

  exportToCsv() {
    this.loader.exportCSVLoader = true;
    let options: any = {
      showLabels: true,
      headers: ["Office", this.staticUtil.isNotTeamOffice()?"Claim ID":"", this.staticUtil.isNotTeamOffice()?"Patient ID":"", "Patient Name", 'DOS', this.staticUtil.isNotTeamOffice()?"Claim Age":"", "TFL", "Age Bracket", "Insurance Name", "Insurance Type", "Claim Type", "Est. Amount", this.staticUtil.isNotTeamPosting()?"Assigned By":"", this.staticUtil.isNotTeamPosting()?"Last Team's Remarks":"", "Pending Since Date"]
    }
    let excelData: any;
    excelData = [...this.filteredItems];  //creating a copy of data so that nothing affects original data.
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
      return e;
    })      //method add value as "-" or "0", if its empty or null.


    excelData = excelData.map((e: any) => {
      return {
        "Office Name": e.officeName,
        "Claim ID": this.staticUtil.isNotTeamOffice()?e.newClaimId:"",
        "Patient ID":this.staticUtil.isNotTeamOffice()? e.patientId:"",
        "Patient Name": e.patientName,
        'DOS': e.dos,
        "Claim Age": this.staticUtil.isNotTeamOffice()?e.claimAge:"",
        "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
        'Age Bracket': e.ageBracket,
        "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
        "Insurance Type": e.prName ? e.prName : e.secName,
        "Claim Type": e.claimType,
        "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
        "Assigned By": this.staticUtil.isNotTeamPosting()?e.lastTeam:"",
        "Last Team's Remarks":this.staticUtil.isNotTeamPosting()? e.lastTeamRemark:"",
        "Pending Since Date": e.pendingSince,
      }
    })  //method aligns the header to the value in CSV.
    // excelData = excelData.map(
    //   ({ claimId, opdos, opdt, secTotal, uuid, statusType, EstAmount, ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.  // values which excluded are coming as undefined so that's why its commented out.

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    //console.log(excelData.sort());
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
    this.loader.exportCSVLoader = false;
  }

  showClaimIdWithDigits() {
    this.filteredItems.forEach((e: any) => {
      if (e.claimId) {
        e.newClaimId = e.claimId.replace(/\D/g, "");  // returns only digits
      }
    });

  }

  showAgeBracket_WithColor_AndClaimIdDigits() {
    let currentDate: any = new Date().setHours(0, 0, 0, 0); // To set the time equal
    this.filteredItems.forEach((e: any) => {
      if (e.dos) {
        let dos: any = new Date(e.dos);
        const diffTime = Math.abs(currentDate - dos);
        let diffDays: any = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        e.ageBracket = (diffDays <= 30) ? `0-30` : (diffDays > 30 && diffDays <= 60) ? `31-60` : (diffDays > 60 && diffDays <= 90) ? `61-90` : (diffDays > 90) ? `90+` : '';
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

    console.log(params);
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
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.insuranceType = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredInsuranceType.length; i++) {
        if (this.filteredInsuranceType[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.insuranceType = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredInsuranceType.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveInsuranceType();
    }
  }

  filterCurrentStatus(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.currentStatus = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredCurrentStatus.length; i++) {
        if (this.filteredCurrentStatus[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.currentStatus = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredCurrentStatus.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveCurrentStatus();
    }
  }

  filterNextActionRequired(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.nextActionRequired = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredNextActionRequired.length; i++) {
        if (this.filteredNextActionRequired[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.nextActionRequired = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredNextActionRequired.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveNextActionRequired();
    }
  }

  filterProviderSpeciality(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.providerSpeciality = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredProviderSpeciality.length; i++) {
        if (this.filteredProviderSpeciality[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.providerSpeciality = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredProviderSpeciality.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveProviderSpeciality();
    }
  }

  filterSelectAging(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.selectAging = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredSelectAging.length; i++) {
        if (this.filteredSelectAging[i].checked == false) {
          isAllSelected = false;
          break;
        }
      } 
      this.isFilterAllSelected.selectAging = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredSelectAging.some((checkbox: any) => {
          return checkbox.checked && checkbox.selectAging === (item.diffForPaymentPosting ? "Unlocked" : "Locked")
        });
      });
      this.addOrRemoveSelectAging();
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
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.claimType = true;
    } else {
      console.log(432);

      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredClaimType.length; i++) {
        if (this.filteredClaimType[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.claimType = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredClaimType.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveFilterClaimType();
    }
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
      this.filteredItems = this.claimDetail;
      this.isFilterAllSelected.lastTeam = true;
    } else {
      console.log(432);

      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredLastTeam.length; i++) {
        if (this.filteredLastTeam[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.lastTeam = isAllSelected;
      this.filteredItems = this.claimDetail.filter((item: any) => {
        return this.filteredLastTeam.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      });
      this.addOrRemoveFilterLastTeam();
    }
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

  switchTab(tab: any) {
    if (!this.claimDetail) return;
    if (tab == 'unSubmitted') {
      this.tabValue = 'unSubmitted';
      this.tabSwitch.unSubmitted = true;
      this.tabSwitch.submitted = false;
      this.fetchClaims("unSubmitted");
    }
    if (tab == 'submitted') {
      this.tabValue = 'submitted';
      this.tabSwitch.unSubmitted = false;
      this.tabSwitch.submitted = true;
      this.fetchClaims("submitted");
    }
  }

  showOrHideColumns(currentTeamName: any) {

    const columnsToShow = this.columnPermissionsByTeam[currentTeamName.teamName] || [];
    Object.keys(this.showColumns).forEach(column => {
      this.showColumns[column] = columnsToShow.includes(column);
    });
  }

  toggleTooltip(tooltip:any){
    this.showTooltipConfig[tooltip] = !this.showTooltipConfig[tooltip];
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape' || event.keyCode === 27) {
        this.showTooltipConfig[tooltip] = false;
      }
    })
    if(!this.showTooltipConfig[tooltip]){
      document.removeAllListeners('keydown');
    }
  }

  get staticUtil():any{
      return Utils;
  }

  checkDiffForPaymentPosting() {
    let currentDate: any = new Date();
    this.filteredItems.forEach((e: any) => {
      if (e.updatedDate && this.currentTeamName.unFormatedName == 'PAYMENT_POSTING') {
        let updateDate: any = new Date(e.updatedDate);
        var diffDays: any = Math.floor((currentDate - updateDate) / (1000 * 60 * 60 * 24));
        if (diffDays > 7) {
          e.diffForPaymentPosting = true;
        } else {
          e.diffForPaymentPosting = false;
        }
      }
    });
  }

  selectTransferClaims(event: any, data: any) {
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
      if(x.diffForPaymentPosting){
        if (x.selectAging == null || x.selectAging == false) 
          all = null;
      }
    });
    this.selectAllAging = all;
  }

  transferClaimsConfirmModal() {
    this.selectedClaimsTransfer.remarks=''
    if (this.selectedClaimsTransfer.claimUuid.length > 0) {
      this.showTransferClaimModal = true;
    }
    else{
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
              },2000);
            }
          });
        }
        else {
          this.closeConfirmationModal();
          this.errorMessage = res.data.message;
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
      },2000);
      this.showTransferClaimModal = false;
    }
  }

  selectAllClaimsToAging(event: any) {
    this.filteredItems.forEach((x: any) => {
      x.selectAging = null;
    });
    let isSelected: boolean = event.target.checked;
    if (isSelected) {
      this.selectAllAging = true;
      this.filteredItems.forEach((x: any) => {
        if(x.diffForPaymentPosting){
        this.selectedClaimsTransfer.claimUuid.push(x.uuid);
        x.selectAging = true;
        }
      });
    } else {
      this.selectAllAging = null;
      this.selectedClaimsTransfer.claimUuid = [];
    }
  }
}