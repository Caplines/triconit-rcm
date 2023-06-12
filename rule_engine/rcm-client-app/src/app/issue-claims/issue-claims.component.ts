import { Component, ElementRef, HostListener, Input, ViewChild } from '@angular/core';
import { ApplicationServiceService } from '../service/application-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';
import { ngxCsv } from 'ngx-csv';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

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
  loader: any = {'exportPDFLoader': false, 'exportCSVLoader': false };
  date: any;
  showFilteredDropdown: any = { 'officeName': false};

  @ViewChild('modalElement')modalElementRef!:ElementRef;
  
  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }
  
  constructor(private appSer: ApplicationServiceService,private router:Router,private title:Title) {
    title.setTitle(Utils.defaultTitle + "Issue Claims")
  }

  ngOnInit() {
    this.userInfo.currentClientName = localStorage.getItem("selected_clientName");
    this.issueClaim();
    this.fetchIssueClaims();
  }

  issueClaim(){
    this.appSer.fetchIssueClaimCounts((res:any)=>{
      if(res.status==200){
         this.issueClaimsCount = res.data;
      }
      else{
        //ERROR
      }
    });
  }

  fetchIssueClaims(){
    let cName = JSON.parse(localStorage.getItem('clients'));
    cName.find((ele:any)=>{
      if(ele.name == this.userInfo.currentClientName){
        this.clientUuid = ele.id;
      }
    });
    if(this.clientUuid){
      this.appSer.fetchIssueClaims(this.clientUuid+`/${this.issueClaimPageNum}`, (res: any) => {
       if (res.status === 200 && res.data) {
        this.fetchOfficeByUuid();
          this.totalPages = res.data[0].totalPages;
         if (this.issueClaimPageNum == 0) {
           this.issueCl = res.data[0].data;
           this.filterOfficeName();
         }
         if (this.issueClaimPageNum != 0 && res.data[0].totalPages != this.issueClaimPageNum) {
           this.issueCl.push.apply(this.issueCl, res.data[0].data);
           this.filterOfficeName();
         }
          //this.modal();
          this.filteredItems = this.issueCl;
        }
      });
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
      this.filteredItems = this.issueCl.filter((item: any) => {
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
  saveToPdf(divName: any) {
    this.loader.exportPDFLoader = true;
    let m: any = document.querySelector(".table-wrapper-scroll-y");
    m.classList.remove('table-wrapper-scroll-y');
    m.classList.remove('table-inner-scrollbar');
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      // Insert office name
      pdf.setFontSize(10);  // Adjust the font size as needed
      pdf.text(`Issue Claims - ${ this.userInfo.currentClientName}`, 2, 6);
      pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_Issue Claims_${this.date}`);
      this.loader.exportPDFLoader = false;
      m.classList.add('table-wrapper-scroll-y');
      m.classList.add('table-inner-scrollbar');
    });
  }
  
  exportToCsv() {
    this.loader.exportCSVLoader = true;
    let options: any = {
      showLabels: true,
      headers: ["Office", "Claim ID", "Upload Date", "Issue due to which Claim could not be Uploaded", "Source"]
    }
    let excelData: any;
    excelData = [...this.filteredItems];  //creating a copy of data so that nothing affects original data.
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
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_Issue Claims_${this.date}`, options);
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
}
