import { Component, ElementRef, HostListener, Input, ViewChild } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';
import { ngxCsv } from 'ngx-csv';
import { DownLoadService } from '../service/download.service';
import {AppConstants} from '../constants/app.constants'

@Component({
  selector: 'app-issue-claims',
  templateUrl: './issue-claims.component.html',
  styleUrls: ['./issue-claims.component.scss']
})
export class IssueClaimComponent {
  currentTeamId:any;
  userInfo: any = { 'currentClientName': '', 'currentTeamId': '' }
  issueClaimPageNum:any=0;
  totalPages:number;
  clientUuid:string="-1";
  filteredItems: any = [];
  isSorted: any = {};
  filteredOfficeName: any = [];
  isFilterAllSelected: any = { 'officeName': false};
  filterName: string = '';
  loader: any = {'exportPDFLoader': false, 'exportCSVLoader': false, 'showLoader': false , 'unarchive':false };
  date: any;
  showFilteredDropdown: any = { 'officeName': false};
  selectedClaimsToArchiveData:any = [];
  loginUserType:any='';
  showIssueClaim:boolean= true;
  showMessage:any={};
  filtertedArchiveItems:any=[];
  totalArchivePages:number = 0;
  paginationPages:any= [];
  tabSwitch: any = { 'IssueClaims': true, 'ArchiveClaims': false};
  tabSwitchValue:any='Issue';

  issueClaimConfig:any= {'issueCount':0,'issueClaimsData':[]};
  archiveClaimConfig:any= {'archiveCount':0,'archiveClaimsData':[]};
  showUnArchiveModal:boolean=false;

  @ViewChild('modalElement')modalElementRef!:ElementRef;
  @ViewChild('archiveSelectBox')archiveSelectBox!:ElementRef;
  @ViewChild('unArchiveSelectBox')unArchiveSelectBox!:ElementRef;
  
  
  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.filterName] = false;
      }, 300);
    }
  }
  
  constructor(private appSer: ApplicationServiceService,private router:Router,private title:Title,private downloadService:DownLoadService, private appConstant:AppConstants) {
    title.setTitle(Utils.defaultTitle + "Upload Errors");
    this.loginUserType = localStorage.getItem("selected_roleName");
  }

  ngOnInit() {
    this.userInfo.currentClientName = localStorage.getItem("selected_clientName");
    this.fetchIssueClaimDataWithCount();
  }

  fetchIssueClaimDataWithCount(){
    this.appSer.fetchIssueClaimCounts((res:any)=>{
      if(res.status==200 && res.data){
         this.issueClaimConfig.issueCount = res.data.issueCount; 
         this.archiveClaimConfig.archiveCount = res.data.archiveCount; 
         this.fetchIssueClaims();
      }
      else{
        //ERROR
      }
    });
  }

  async fetchIssueClaims(){
    let clientName = Utils.getClientsFromLS();
      await clientName.find((ele:any)=>{
          if(ele.name == this.userInfo.currentClientName){
            this.clientUuid = ele.id;
          }
        });

    if(this.clientUuid){
      this.appSer.fetchIssueClaims(this.clientUuid, (res: any) => {
       if (res.status === 200 && res.data) {
         this.issueClaimConfig.issueClaimData = res.data;
           this.fetchOfficeByUuid();
           this.filterOfficeName();
         }
        }
      );
    }
  }
   
  loadMore(){
    ++this.issueClaimPageNum;
    if(this.issueClaimPageNum < this.totalPages){
      this.fetchIssueClaims();
    }
}

  onModalScroll(){
    const modalElement = this.modalElementRef.nativeElement;
    const scrollTop = modalElement.scrollTop;
    const scrollHeight = modalElement.scrollHeight;
    const clientHeight = modalElement.clientHeight;
    if (scrollTop + clientHeight >= scrollHeight) {
      this.loadMore();
    }
  }

  goToClaimDetailPage() {
    window.location.href = "/tool-update";
    window.close();
  }

  sortData(data: any, sortProp: string, order: any, sortType: string) {
    this.appSer.sortData(data, sortProp, order, sortType);
  }
  showHideFilteredDropdown(filterName: any) {
    filterName == 'officeName' ? this.showFilteredDropdown.officeName = true : this.showFilteredDropdown.officeName = false;
    this.filterName = filterName;
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

  filterOfficeName(e?: any, filterProperty?: any) {
    if (!e) {
      this.filteredItems = this.issueClaimConfig.issueClaimData;
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
      this.showIssueClaim ? this.filteredItems = this.issueClaimConfig?.issueClaimData?.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox['officeName'] === item['officeName'];
        });
      }) :
      this.filtertedArchiveItems = this.archiveClaimConfig.archiveClaimsData?.data?.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox['officeName'] === item['officeName'];
        });
      });
    }
  }

  fetchOfficeByUuid() {
    this.appSer.fetchOfficeByUuid((res: any) => {
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

  showFilterOptionOfficeName(data: any) {
    this.filteredOfficeName = data;
    this.filteredOfficeName.forEach((e: any) => {
      this.issueClaimConfig.issueClaimData.forEach((ele: any) => {
        e['checked'] = true;
      })
    });
    this.sortFiltereData(this.filteredOfficeName);
  }
  
  exportToCsv() {
    this.loader.exportCSVLoader = true;
    let options: any = {
      showLabels: true,
      headers: ["Office", "Claim ID", "Upload Date", "Issue due to which Claim could not be Uploaded", "Source"]
    }
    let excelData: any;
    this.showIssueClaim ? excelData = [...this.filteredItems] :  excelData = [...this.filtertedArchiveItems] //creating a copy of data so that nothing affects original data.
    excelData = excelData.map((e: any) => {
      if (e.createdDate) {
        let date: Date = new Date(e.createdDate);
        e = { ...e, createdDate: `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}` };
      }
      else {
        e = { ...e, createdDate: '' };
      }
      return e;
    })   

    excelData = excelData.map((e: any) => {
      return {
        "Office": e.officeName,
        "Claim ID": this.showIssueClaim ? e.claimId:e.newClaimId,
        "Upload Date": e.createdDate,
        'Issue due to which Claim could not be Uploaded': e.issue,
        "Source": e.source,
      }
    })  //method aligns the header to the value in CSV.

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData, this.showIssueClaim ? `${localStorage.getItem("selected_clientName")}_Upload Errors_${this.date}`: `${localStorage.getItem("selected_clientName")}_Upload Errors Claims-Archived_${this.date}`, options);
    this.loader.exportCSVLoader = false;
  }

  getMonthName(month: any) {
    const monthNames = [
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    return monthNames[month];
  }

  sortFiltereData(filterValue: any) {
    filterValue.sort((a: any, b: any) => {
      const nameA = Object.keys(filterValue[0])[4] == 'officeName' ? a.officeName.toUpperCase() : '';// ignore upper and lowercase
      const nameB = Object.keys(filterValue[0])[4] == 'officeName' ? b.officeName.toUpperCase() : '';// ignore upper and lowercase
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

  downloadPdf(){
    this.loader.exportPDFLoader = true;
    if(this.tabSwitchValue=='Issue' && this.filteredItems?.length>0){
    let data = {"fileName":this.showIssueClaim ? "Upload Errors" : "Upload Errors Claims-Archived","data":this.showIssueClaim ? this.filteredItems : this.filtertedArchiveItems,"clientName": this.userInfo.currentClientName,"issueClaimCounts":this.showIssueClaim ? this.issueClaimConfig.issueCount: this.archiveClaimConfig.archiveCount,"tabSwitch":this.tabSwitchValue};
    this.appSer.issueClaimPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.date = new Date();
        this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
        this.downloadService.saveBolbData(res.body, this.showIssueClaim ? `${localStorage.getItem("selected_clientName")}_Upload Errors_${this.date}.pdf` : `${localStorage.getItem("selected_clientName")}_Upload Errors Claims-Archived_${this.date}.pdf`);
        this.loader.exportPDFLoader = false;
      }else{
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    })
  }else if(this.tabSwitchValue=='Archive' && this.filtertedArchiveItems?.length>0){
    let data = {"fileName":this.showIssueClaim ? "Upload Errors" : "Upload Errors Claims-Archived","data":this.showIssueClaim ? this.filteredItems : this.filtertedArchiveItems,"clientName": this.userInfo.currentClientName,"issueClaimCounts":this.showIssueClaim ? this.issueClaimConfig.issueCount: this.archiveClaimConfig.archiveCount,"tabSwitch":this.tabSwitchValue};
    this.appSer.issueClaimPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.downloadService.saveBolbData(res.body, this.showIssueClaim ? `${localStorage.getItem("selected_clientName")}_Upload Errors_${this.date}.pdf` : `${localStorage.getItem("selected_clientName")}_Upload Errors Claims-Archived_${this.date}.pdf`);
        this.loader.exportPDFLoader = false;
      }else{
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    }) 
  }else{
    console.log("Data is Empty")
    this.loader.exportPDFLoader = false;
  }
}

selectClaimsToArchive(e:any,id:any){
    if (this.selectedClaimsToArchiveData.length == 0) {
      this.selectedClaimsToArchiveData.push({'id': id, 'archiveStatus': e});
    } else {
      let exist = this.selectedClaimsToArchiveData.some((item: any) => item.id == id);
      if (!exist) {
        this.selectedClaimsToArchiveData.push({'id': id, 'archiveStatus': e});
      } else {
        let indx = this.selectedClaimsToArchiveData.findIndex((item: any) => item.id == id);
        this.selectedClaimsToArchiveData.splice(indx, 1);
      }
    }
    
  }

  archiveSelectedClaims(){
    this.showHideMessage();
    this.loader.showLoader=true;
    let params:any = {
      'archiveClaims': this.selectedClaimsToArchiveData
    };
    this.appSer.saveArchiveClaims(params,(res:any)=>{
      if(res.status== 200){
        this.showMessage = {'msg':res.data,'status':res.status}; 
        this.showHideMessage();
        // this.getArchiveClaimsCount();
        // this.issueClaim();
        this.fetchIssueClaimDataWithCount();
        if(this.selectedClaimsToArchiveData.length == this.filteredItems.length){
          this.issueClaimConfig.issueClaimData=[];
          this.filteredItems=[];
          let archiveSelectboxEl = this.archiveSelectBox.nativeElement;
          archiveSelectboxEl.checked = false;
        }
        this.selectedClaimsToArchiveData=[];
        this.loader.showLoader=false;
      }
      else{
        this.showMessage = {'msg':res.message,'status':res.status}; 
        this.loader.showLoader=false;
        this.showHideMessage();
        //ERROR
      }
    });
  }
  
  receiveChildEvent(event:any){
    if(event['action'] === 'pageNum'){
      this.getArchiveClaims(event.value);
    }
  }

   getArchiveClaims(pgNum:any){
    if(this.archiveClaimConfig.archiveCount>0){
    this.loader.showLoader=true;
    let data = this.clientUuid + "/" + pgNum;
    this.appSer.fetchArchiveClaims(data,(res:any)=>{
      if(res.status){
        this.archiveClaimConfig.archiveClaimsData = res?.data[0];
        this.filtertedArchiveItems = res?.data[0].data;
        this.filtertedArchiveItems.forEach((e:any)=>{
          if(e.claimId.startsWith(`${e.id}_${this.appConstant.ARCHIVE_PREFIX}`)){
            e.newClaimId = e.claimId.replace(`${e.id}_${this.appConstant.ARCHIVE_PREFIX}`,'')
         }
        })
        this.totalArchivePages = res?.data[0].totalPages;
        this.pagnationPages();
        this.loader.showLoader=false;
      }
      else{
        this.loader.showLoader=false;
        //ERROR
      }
    })
  }
  }

  getArchiveClaimsCount(){
    this.appSer.fetchArchiveClaimsCount((res:any)=>{
      this.archiveClaimConfig.archiveCount = res.data;
    });
  }

  pagnationPages(){
    this.paginationPages= [...Array(this.totalArchivePages).keys()];
  }

  selectAllArchieveClaims(event: any){
    let isAllSelected:boolean = event.target.checked;
    if (isAllSelected) {
      this.filteredItems.forEach((e: any) => {
        if (!e.archive) {
          e.archive = true;
          this.selectedClaimsToArchiveData.push({'id':e.id, 'archiveStatus': e.archive})
        }
      });
    } else {
      this.filteredItems.forEach((e: any) => {
        if (e.archive) {
          e.archive = false;
          this.selectedClaimsToArchiveData = [];
        }
      });
    }
  }

  switchTab(tab:any){
    if(tab=='IssueClaims'){
      this.tabSwitch.IssueClaims=true;
      this.tabSwitch.ArchiveClaims=false;
      this.showIssueClaim = true;
      this.tabSwitchValue = 'Issue';
      this.fetchIssueClaimDataWithCount();
    }
    else{
      this.tabSwitch.IssueClaims=false;
      this.tabSwitch.ArchiveClaims=true;
      this.showIssueClaim=false;
      this.tabSwitchValue = 'Archive';
      this.getArchiveClaims(0);
    }
    let event = { target: { checked: true } };  //added so that when tab is swtiched then by default all data should show.
    this.selectAll(event, 'officeName');
    
  }

  showHideMessage(){
    const alert = document.getElementById('alert') as HTMLElement;
    if(alert){
      alert.style.display='block';
    }
    setTimeout(()=>{
      document.getElementById('alert').style.display ='none';
    },2000);

  }

  UnarchiveClaims(data: any,isUnarchiveAll:boolean) {
    let param = {
      "id": data.id? data.id : '',
      "claimId": data.claimId ? data.claimId :'',
      "unArchiveAll":isUnarchiveAll
    };
    this.loader.unarchive = true;
    this.loader.showLoader = true;

    this.appSer.saveUnarchiveClaims(param, (res: any) => {
      if (res.status == 200 && res.data.unArchiveStatus) {
        this.showMessage = { 'msg': res.data.message, 'status': res.status };
          this.loader.showLoader = false;
          this.showHideMessage();
          this.filtertedArchiveItems = this.removeUnArchivedItem(data);
          this.getArchiveClaimsCount();
          let hasUnarchivedAll:boolean=isUnarchiveAll;
          this.closeConfirmationModal(hasUnarchivedAll);
          this.loader.unarchive = false;
      }
      else if(res.status == 200 && !res.data.unArchiveStatus) {
        this.showMessage = { 'msg': res.data.message, 'status': res.status };
        this.loader.showLoader = false;
        this.closeConfirmationModal(false);
        this.showHideMessage();
        this.loader.unarchive = false;
      }
      else {
        this.showMessage = { 'msg': res.message, 'status': res.status };
        this.loader.showLoader = false;
        this.closeConfirmationModal(false);
        this.showHideMessage();
        this.loader.unarchive = false;
      }
    })
  }
  
  removeUnArchivedItem(data: any) {
    let idx = this.filtertedArchiveItems.findIndex((item:any)=>item.id== data.id);
    this.filtertedArchiveItems.splice(idx,1);
    return this.filtertedArchiveItems;
  }

  UnarchiveClaimsConfirmModal(){
        this.showUnArchiveModal = true;
  }

  closeConfirmationModal(hasUnarchivedAll:boolean){
    this.showUnArchiveModal = false;
    // this.unArchiveSelectBox.nativeElement.checked=false;
    if(hasUnarchivedAll) location.reload();
  }

  UnarchiveCurrentPageClaims(){
    this.loader.unarchive = true;
    let urachiveCurrentPageClaims:any = {'unarchiveClaims':[]};
    this.filtertedArchiveItems.forEach((item:any)=>{
      urachiveCurrentPageClaims.unarchiveClaims.push({'claimId':item.claimId,'id':item.id});
    })

    this.appSer.unarchiveCurrentpageClaims(urachiveCurrentPageClaims,(res:any)=>{
        if(res.status == 200 && res.data){
          this.showMessage = { 'msg': res.data.message, 'status': res.status };
          this.loader.unarchive = false;
          this.showHideMessage();
          setTimeout(() => {
            location.reload();
          }, 0);
        }
    })

    console.log(this.filtertedArchiveItems);
    
  }
}
