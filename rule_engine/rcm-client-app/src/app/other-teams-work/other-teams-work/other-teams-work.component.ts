import { Component, OnInit, LOCALE_ID, Inject, HostListener, Input } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';
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

  selectedFilesMap: any= new Map();
  selectedFiles:any=[];

  showModal:boolean=false;
  isFilesSubmitted:boolean=false;
  errorMessage:any;
  otherTeams:any=[];
  submitBtnConfig:any={'remarks':[],'otherTeamId':[]};
  currentClaimUuid:any;

  removedFilesMap:any=new Map();
  removedFiles:any=[];
  hasAttachmentFilesRemoved:boolean=false;
  currentTeamName:any;

  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }

  constructor(@Inject(LOCALE_ID) private locale: string ,private appService: ApplicationServiceService, public appConstants: AppConstants, private title: Title, private downloadService: DownLoadService) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    this.clientName = localStorage.getItem("selected_clientName");
     this.currentTeamName = this.appConstants.teamData.find((e:any)=>e.teamId==Utils.selectedTeam());
    this.fetchClaims();
    this.fetchOtherTeams();
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
        this.showPendingSince();
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

  closeModal(){
    this.showModal=false;
  }


  receiveChildEvent(event:any){
    if(event['action'] === 'fileSelected'){
      this.setSelectedFileForComponent(event.claimUuid, event.value);
    } else if(event['action']==='filesSelectedToRemove'){
        this.setSelectedFileToRemove(event.claimUuid,event.value)
    }
  }

  setSelectedFileForComponent(claimUuid: any, file: File) {
    this.selectedFilesMap.set(claimUuid, file);
  }

  setSelectedFileToRemove(claimUuid: any, fileParam:any){
    this.removedFilesMap.set(claimUuid,fileParam);
  }
 
  getSelectedFileForComponent(claimUuid: any) {
    return this.selectedFilesMap.get(claimUuid);
  }
  
  getSelectedFilesToRemove(claimUuid: any) {
    return this.removedFilesMap.get(claimUuid);
  }
  
  openSubmitConfirmationModal(data: any) {
    this.currentClaimUuid = data.uuid
    this.selectedFiles = this.getSelectedFileForComponent(data.uuid);
    this.removedFiles = this.getSelectedFilesToRemove(data.uuid);
    console.log(this.removedFiles);
    
      this.submitBtnConfig['submitType'] = 'ath';
      this.submitBtnConfig['otherTeamId'][data.uuid]=null;
      if(this.submitBtnConfig['remarks'][data.uuid]){
        this.errorMessage = '' ;
        this.showModal=true;
     } else{
       data['isInvalid']=true;
     }
      if(!this.selectedFiles || this.selectedFiles?.length==0){
        this.selectedFiles= [];
        this.errorMessage = "No Files Are Attached !";
      }
  }

  isValidForOtherTeam(){
    const remarks = this.submitBtnConfig['remarks'][this.currentClaimUuid];
    if(!remarks){
      this.errorMessage = "Remarks Are Mandatory !";
    } else{
      if(this.isRemoveFileArrayNotEmpty()){
        this.removeAttachmentFile();
      } else {
        this.loopThroughData(this.selectedFiles, 0);
      }
    }
  }

  isValidForAssignTeam(){
        const remarks = this.submitBtnConfig['remarks'][this.currentClaimUuid];
        if(!remarks){
          this.errorMessage = "Remarks Are Mandatory !";
        } else{
              if(this.isRemoveFileArrayNotEmpty()){
                this.removeAttachmentFile();
              } else {
                this.loopThroughData(this.selectedFiles, 0);
              }
        }
  }

  submitConfirmation(){
    if (this.submitBtnConfig['submitType'] == 'oth') {
        this.isValidForOtherTeam();
    } else {
      this.isValidForAssignTeam();
    }
  }

  isRemoveFileArrayNotEmpty(){
      if( Array.isArray(this.removedFiles)){
        return false;
      } else{
        return true;
      }
    }

  submitOtherTeams(data:any){
    this.selectedFiles = this.getSelectedFileForComponent(data.uuid);
    this.removedFiles = this.getSelectedFilesToRemove(data.uuid);
    this.submitBtnConfig['submitType'] = 'oth';
    this.submitBtnConfig['claimUuid'] = data.uuid;
    this.currentClaimUuid = data.uuid;
    if (this.submitBtnConfig['remarks'][data.uuid]) {
      this.errorMessage = '';
      this.showModal = true;
    } else {
      data['isInvalid'] = true;
    } 
    if (!this.selectedFiles || this.selectedFiles?.length == 0) {
      this.selectedFiles=[];
      this.errorMessage = "No Files are atttached."
    }
    if(!this.removedFiles){
      this.removedFiles=[];
    }
  }

  removeAttachmentFile(){
    this.appService.removeAttachmentFile(this.removedFiles,(res:any)=>{
      if(res.status == 200){
        this.hasAttachmentFilesRemoved = res.data.fileResponseStatus;
        this.loopThroughData(this.selectedFiles, 0);
        if(!res.data.fileResponseStatus){
          this.errorMessage  = res.data.msg;
        }
      } else{
        this.errorMessage  = res.data.message;
      }
    })
  }

  selectOtherTeam(event:any,claimUuid:any){
      this.submitBtnConfig['otherTeamId'][claimUuid] = event.target.value;
  }

  loopThroughData(dataArray: any[], currentIndex: number) {
    if (currentIndex >= dataArray.length) {
      this.AssignClaimWithRemark(dataArray[0]?.claimUuid ? dataArray[0]?.claimUuid : this.currentClaimUuid);
      return;
    }
    const currentData = dataArray[currentIndex];
    let formData: any = new FormData();
    formData.append("claimUuid", currentData?.claimUuid ? currentData.claimUuid : this.currentClaimUuid);
    formData.append("attachmentTypeId", currentData?.attachmentTypeId ? currentData.attachmentTypeId : 0);
    formData.append("file", currentData?.file ? currentData.file : new File([""], "filename"));
    this.appService.submitFilesToAssignedClaims(formData, (res: any) => {
      if (res.data.fileResponseStatus) {
        this.loopThroughData(dataArray, currentIndex + 1);
      } else {
        this.errorMessage = res.data.msg;
      }
    })
}

AssignClaimWithRemark(claimUuid:any){
  let hasRemarks =   this.submitBtnConfig['remarks'][claimUuid];
  if(hasRemarks){
    let params:any= {
      "remark":this.submitBtnConfig['remarks'][claimUuid],
      "claimUuid":claimUuid,
      "assignToTeamId": this.submitBtnConfig['otherTeamId'][claimUuid] ? +this.submitBtnConfig['otherTeamId'][claimUuid] : null    //converting string into number using unary operator +
    }
  this.appService.AssignClaimWithRemark(params,(res:any)=>{
      if(res.status == 200){
          console.log(res);
          this.showModal=false;
          this.errorMessage='';
          this.removeSubmittedClaimRow(claimUuid);
      }
    })
  } else{
    this.errorMessage = "Remarks Are Mandatory";
  }
}

  removeSubmittedClaimRow(claimUuid:any){
    let index  = this.filteredItems.findIndex((item:any)=>item.uuid == claimUuid);
    this.filteredItems.splice(index,1);
  }

  

  downloadPdf(){
    if(this.filteredItems.length!=0){
    let data = {"fileName":"List_Of_Claims","data": this.filteredItems,"clientName": this.clientName,"currentTeamName":this.currentTeamName.teamName};
    this. appService.othersTeamPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        console.log(res.body);
        this.downloadService.saveBolbData(res.body, "List_Of_Claims.pdf");
      }else{
        console.log("something went wrong");
      }
    })
  }
  }

  showPendingSince(){
    this.filteredItems.forEach((e:any) => {
      if(e.dos){
           let dos:any = new Date(e.dos);
           let currentDate:any = new Date().setHours(0,0,0,0); // To set the time equal
           const diffTime = Math.abs(currentDate - dos);
           let diffDays:any = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
           e.pendingSince = diffDays < 2 ? `${diffDays} day` :  `${diffDays} days`
      }
    });
  }
  
}