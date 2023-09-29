import { Component, ElementRef, HostListener, Input, ViewChild } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';
import { ngxCsv } from 'ngx-csv';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { DownLoadService } from '../service/download.service';
import {AppConstants} from '../constants/app.constants'

@Component({
  selector: 'app-issue-claims',
  templateUrl: './issue-claims.component.html',
  styleUrls: ['./issue-claims.component.scss']
})
export class IssueClaimComponent {
  currentTeamId:any;
  issueClaimsCount: number = 0;
  issueClientName:any='';
  issueCl : any=[];
  userInfo: any = { 'currentClientName': '', 'currentTeamId': '' }
  issueClaimPageNum:any=0;
  totalPages:number;
  clientUuid:string="-1";
  filteredItems: any = [];
  isSorted: any = {};
  filteredOfficeName: any = [];
  isFilterAllSelected: any = { 'officeName': false};
  fliterName: string = '';
  loader: any = {'exportPDFLoader': false, 'exportCSVLoader': false, 'showLoader': false };
  date: any;
  showFilteredDropdown: any = { 'officeName': false};
  archiveClaimsData:any = [];
  loginUserType:any='';
  archiveCl:any=[];
  showIssueClaim:boolean= true;
  archiveClaimsCount:number=0;
  showMessage:any=[];
  filtertedArchiveItems:any=[];
  totalArchivePages:number = 0;
  paginationPages:any= [];
  tabSwitch: any = { 'IssueClaims': true, 'ArchiveClaims': false};
  tabSwitchValue:any='Issue';

  @ViewChild('modalElement')modalElementRef!:ElementRef;
  
  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }
  
  constructor(private appSer: ApplicationServiceService,private router:Router,private title:Title,private downloadService:DownLoadService, private appConstant:AppConstants) {
    title.setTitle(Utils.defaultTitle + "Upload Errors");
    this.loginUserType = localStorage.getItem("selected_roleName");
  }

  ngOnInit() {
    this.userInfo.currentClientName = localStorage.getItem("selected_clientName");
    this.issueClaim();
    this.getArchiveClaimsCount();
  }

  issueClaim(){
    this.appSer.fetchIssueClaimCounts((res:any)=>{
      if(res.status==200){
         this.issueClaimsCount = res.data;
        if( this.issueClaimsCount!=0){
          this.fetchIssueClaims();
        }
      }
      else{
        //ERROR
      }
    });
  }

  // fetchIssueClaims(){
  //   let cName = JSON.parse(localStorage.getItem('clients'));
  //   cName.find((ele:any)=>{
  //     if(ele.name == this.userInfo.currentClientName){
  //       this.clientUuid = ele.id;
  //     }
  //   });
  //   if(this.clientUuid){
  //     this.appSer.fetchIssueClaims(this.clientUuid+`/${this.issueClaimPageNum}`, (res: any) => {
  //      if (res.status === 200 && res.data) {
  //         this.totalPages = res.data[0].totalPages;
  //        if (this.issueClaimPageNum == 0) {
  //          this.issueCl = res.data[0].data;
  //          this.filterOfficeName();
  //        }
  //        if (this.issueClaimPageNum != 0 && res.data[0].totalPages != this.issueClaimPageNum) {
  //          this.issueCl.push.apply(this.issueCl, res.data[0].data);
  //          this.filterOfficeName();
  //        }
  //         //this.modal();
  //         this.filteredItems = this.issueCl;
  //       }
  //     });
  //   }
  // }

    fetchIssueClaims(){
    let cName = JSON.parse(localStorage.getItem('clients'));
    cName.find((ele:any)=>{
      if(ele.name == this.userInfo.currentClientName){
        this.clientUuid = ele.id;
      }
    });
    if(this.clientUuid){
      this.appSer.fetchIssueClaims(this.clientUuid, (res: any) => {
       if (res.status === 200 && res.data) {
          this.fetchOfficeByUuid();
           this.issueCl = res.data;
           this.filterOfficeName();
         }
          //this.modal();
          this.filteredItems = this.issueCl;
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
    this.fliterName = filterName;
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
      this.filteredItems = this.issueCl;
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
      this.showIssueClaim ? this.filteredItems = this.issueCl.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
        });
      }) :
      this.filtertedArchiveItems = this.archiveCl?.data?.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && checkbox[filterProperty] === item[filterProperty];
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
      this.issueCl.forEach((ele: any) => {
        e['checked'] = true;
      })
    });
    this.sortFiltereData(this.filteredOfficeName);
  }
  // saveToPdf(divName: any) {
  //   this.loader.exportPDFLoader = true;
  //   let m: any = document.querySelector(".table-wrapper-scroll-y");
  //   m.classList.remove('table-wrapper-scroll-y');
  //   m.classList.remove('table-inner-scrollbar');
  //   html2canvas(<any>document.getElementById(divName)).then(canvas => {
  //     const content = canvas.toDataURL('image/png');
  //     let pdf = new jsPDF('p', 'mm', 'a4');
  //     let width = pdf.internal.pageSize.getWidth();
  //     let height = canvas.height * width / canvas.width;
  //     // Insert office name
  //     pdf.setFontSize(10);  // Adjust the font size as needed
  //     pdf.text(this.showIssueClaim ? `Issue Claims - ${ this.userInfo.currentClientName}`:`Archieved Claims - ${ this.userInfo.currentClientName}`, 2, 6);
  //     pdf.addImage(content, "PNG", 0, 15, width, height);
  //     this.date = new Date();
  //     this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
  //     pdf.save(this.showIssueClaim ? `${localStorage.getItem("selected_clientName")}_Issue Claims_${this.date}`:`${localStorage.getItem("selected_clientName")}_Archieved Claims_${this.date}`);
  //     this.loader.exportPDFLoader = false;
  //     m.classList.add('table-wrapper-scroll-y');
  //     m.classList.add('table-inner-scrollbar');
  //   });
  // }
  
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
        "Claim ID": e.claimId,
        "Upload Date": e.createdDate,
        'Issue due to which Claim could not be Uploaded': e.issue,
        "Source": e.source,
      }
    })  //method aligns the header to the value in CSV.

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    console.log(excelData.sort());
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
    let data = {"fileName":this.showIssueClaim ? "Upload Errors" : "Upload Errors Claims-Archived","data":this.showIssueClaim ? this.filteredItems : this.filtertedArchiveItems,"clientName": this.userInfo.currentClientName,"issueClaimCounts":this.showIssueClaim ? this.issueClaimsCount: this.archiveClaimsCount,"tabSwitch":this.tabSwitchValue};
    this.appSer.issueClaimPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.downloadService.saveBolbData(res.body, this.showIssueClaim ? "Upload Errors.pdf" : "Upload Errors Claims-Archived.pdf");
        this.loader.exportPDFLoader = false;
      }else{
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    })
  }else if(this.tabSwitchValue=='Archive' && this.filtertedArchiveItems?.length>0){
    let data = {"fileName":this.showIssueClaim ? "Upload Errors" : "Upload Errors Claims-Archived","data":this.showIssueClaim ? this.filteredItems : this.filtertedArchiveItems,"clientName": this.userInfo.currentClientName,"issueClaimCounts":this.showIssueClaim ? this.issueClaimsCount: this.archiveClaimsCount,"tabSwitch":this.tabSwitchValue};
    this.appSer.issueClaimPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.downloadService.saveBolbData(res.body, this.showIssueClaim ? "Upload Errors.pdf" : "Upload Errors Claims-Archived.pdf");
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

  archiveClaimsSelection(e:any,id:any){
    if (this.archiveClaimsData.length == 0) {
      this.archiveClaimsData.push({'id': id, 'archiveStatus': e});
    } else {
      let exist = this.archiveClaimsData.some((item: any) => item.id == id);
      if (!exist) {
        this.archiveClaimsData.push({'id': id, 'archiveStatus': e});
      } else {
        let indx = this.archiveClaimsData.findIndex((item: any) => item.id == id);
        this.archiveClaimsData.splice(indx, 1);
      }
    }
  }

  archiveClaims(){
    this.showHideMessage();
    this.loader.showLoader=true;
    let params:any = {
      'archiveClaims': this.archiveClaimsData
    };
    this.appSer.saveArchiveClaims(params,(res:any)=>{
      if(res.status== 200){
        this.showMessage = {'msg':res.data,'status':res.status}; 
        this.showHideMessage();
        this.getArchiveClaimsCount();
        this.issueClaim();
        this.archiveClaimsData=[];
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
    if(this.archiveClaimsCount>0){
    this.loader.showLoader=true;
    let data = this.clientUuid + "/" + pgNum;
    this.appSer.fetchArchiveClaims(data,(res:any)=>{
      if(res.status){
        this.archiveCl = res?.data[0];
        this.filtertedArchiveItems = res?.data[0].data;
        this.filtertedArchiveItems.forEach((e:any)=>{
          if(e.claimId.includes(`${e.id}_${this.appConstant.ARCHIVE_PREFIX}`)){
            e.claimId = e.claimId.replace(`${e.id}_${this.appConstant.ARCHIVE_PREFIX}`,'')
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
      this.archiveClaimsCount = res.data;
    });
  }

  showArchiveClaims(){
    this.showIssueClaim=false;
    this.getArchiveClaims(0);
  }

  pagnationPages(){
    this.paginationPages= [...Array(this.totalArchivePages).keys()];
  }

  selectAllArchieveClaims(isAllSelected: any){
    if (isAllSelected) {
      this.filteredItems.forEach((e: any) => {
        if (!e.archive) {
          e.archive = true;
          this.archiveClaimsData.push({'id':e.id, 'archiveStatus': e.archive})
        }
      });
    } else {
      this.filteredItems.forEach((e: any) => {
        if (e.archive) {
          e.archive = false;
          this.archiveClaimsData = [];
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
    }
    else{
      this.tabSwitch.IssueClaims=false;
      this.tabSwitch.ArchiveClaims=true;
      this.showIssueClaim=false;
      this.showArchiveClaims();
      this.tabSwitchValue = 'Archive';
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

}