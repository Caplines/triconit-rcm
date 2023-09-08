import { Component, HostListener } from '@angular/core';
import { Title } from '@angular/platform-browser';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { ngxCsv } from 'ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import Utils from '../../util/utils';
import { DatePipe } from '@angular/common';
import { DownLoadService } from 'src/app/service/download.service';
import { AppConstants } from 'src/app/constants/app.constants';
@Component({
  selector: 'all-pendency',
  templateUrl: './all-pendency.component.html',
  styleUrls: ['./all-pendency.component.scss']
})
export class AllPendencyComponent {

  pendencyData: any = [];
  teamData: any = this.constants.teamData;
  currentTeamId: any;
  showLoader: any = { 'loader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  date: any;
  clientName: string = '';
  showFilteredDropdown: any = { 'officeName': false };
  isFilterAllSelected: any = { 'officeName': false };
  filteredOfficeName: any = [];
  filteredItems: any = [];
  tabSwitch: any = { 'withoutDos': true, 'withDos': false ,'withDateOfPending':false};
  isSorted: any = {};

  totalCount: any = this.constants.teamData;
  datePipeString:any;
  fliterName:string= '';
  tabValue:any="withoutDos";
  currentTeamName:string='';
  teamName: any = ["INTERNAL_AUDIT", "LC3", "OFFICE", "PATIENT_CALLING", "BILLING"];

  allUserClients:any=[];

  officeData:any= [];

  constructor(public constants:AppConstants,private _service: ApplicationServiceService, private title: Title,private datePipe: DatePipe,private downloadService:DownLoadService) {
    title.setTitle(Utils.defaultTitle + "Pendency - Other Teams")
  }
  ngOnInit(): void {
    this.getAllUserClients();
    this.currentTeamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
    this.setTopOnTotalRow();
    window.addEventListener("resize", this.setTopOnTotalRow);  //event added todynamically set style top on totalRow
  }

  getAllUserClients(){
    this.showLoader.loader=true;
      this._service.fetchAllUserClients((res:any)=>{
        if(res.status==200){
          console.log(res);
          this.allUserClients = res.data;
            this.getAllPendencyDetailsByUuid(res.data);
        }
      })
  }

  getAllPendencyDetailsByUuid(data:any){
        this.loopThroughData(data,0);
      }
  
  loopThroughData(data:any,currentidx:any){

    if(data.length == currentidx){
      this.showLoader.loader=false;
      this.addClientNameCrossToOfficeName();
      this.showFilterOptionOfficeName(this.pendencyData);
      this.total(this.pendencyData);
      this.filterOfficeName();
      return;
    }
   else{
          const uuid = data[currentidx].uuid;
          this._service.fetchClientNamebyUuid(uuid,(res:any)=>{
            if(res.status==200){
                if(res.data.onlyOffice.length>0){
                    res.data.onlyOffice.forEach((e:any)=>{
                          e['clientUuid'] = uuid;
                    })
                  this.pendencyData = [...this.pendencyData,...res.data.onlyOffice];
                }
              this.loopThroughData(data,currentidx = currentidx+1)
                this.officeData = res.data.header;
            }
          })
  }
}

  total(data: any) {
    if (data) {
      data.forEach((e: any) => {
        this.teamData.forEach((team: any) => {
          if (team.teamId != this.currentTeamId) {
            this.totalCount.find((ele: any) => {
              if (ele.teamName == team.teamName) {
                ele['count'] = ele['count'] + e.counts1[team.unFormatedName.toUpperCase()];
              }
            })
          }
        })
      })
    }
  }

  saveToPdf(divName: any) {
    this.showLoader.exportPDFLoader = true;
    let m: any = document.querySelectorAll(".table-wrapper-scroll-y");
    m.forEach((e: any) => {
      e.classList.remove('table-wrapper-scroll-y');
      e.classList.remove('table-inner-scrollbar');
    })
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      // Insert office name
      pdf.setFontSize(10);  // Adjust the font size as needed
      pdf.text(`Pendency - Other Teams - ${this.clientName}`, 2, 6);
      pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_Pendency - Other Teams_${this.date}`);
      this.showLoader.exportPDFLoader = false;
      m.forEach((e: any) => {
        e.classList.add('table-wrapper-scroll-y');
        e.classList.add('table-inner-scrollbar');
      })
    });
  }

  exportToCsv(fromTable: any) {
    let totalRow: any = {};
    this.showLoader.exportCSVLoader = true;
    let headers: any = [];
    headers.push("Office");
    this.teamData.forEach((e: any) => {
      if (e.teamId != this.currentTeamId) {
        e.teamName === 'Internal_Audit' ? headers.push("Internal Audit") :
        e.teamName === 'Patient_Calling' ? headers.push("Patient Calling") :
        headers.push(e.teamName);
    
        }
      })

    let options: any = {
      showLabels: true,
      headers: headers
    }
    let excelData: any;
    excelData = JSON.parse(JSON.stringify(this.pendencyData));

    if (fromTable == 'table') {
      let data: any = {};
      excelData = excelData.map((e: any) => {
        return {
          'Office': e.officeName,
          'InternalAudit': e.counts1['INTERNAL_AUDIT'],
          'PatientCalling': e.counts1['PATIENT_CALLING'],
          'Lc3': e.counts1['LC3'],
          'office': e.counts1['OFFICE'],
          'Billing': e.counts1['BILLING'],
        }
      })

      this.totalCount.forEach((e: any) => {
        if (e.teamId != this.currentTeamId) {
          totalRow['name'] = 'Total';
          totalRow[`${e.teamName}`] = e.count;
        }
      })
      excelData.unshift(totalRow);
    }

    else if (fromTable == 'dos-table') {
      excelData = excelData.map((e: any) => {
        return {
          'Office': e.officeName,
          'InternalAudit': e.dates1['INTERNAL_AUDIT'] ? this.datePipe.transform(new Date(e.dates1['INTERNAL_AUDIT']),'MMM dd, YYYY') : "-",
          'PatientCalling': e.dates1['PATIENT_CALLING'] ? this.datePipe.transform(new Date(e.dates1['PATIENT_CALLING']),'MMM dd, YYYY') : "-",
          'Lc3': e.dates1['LC3'] ? this.datePipe.transform(new Date(e.dates1['LC3']),'MMM dd, YYYY') : "-",
          'office': e.dates1['OFFICE'] ? this.datePipe.transform(new Date(e.dates1['OFFICE']),'MMM dd, YYYY') : "-",
          'Billing': e.dates1['BILLING'] ? this.datePipe.transform(new Date(e.dates1['BILLING']),'MMM dd, YYYY') : "-",
        }
      })
    }
    else if (fromTable == 'dop-table') {
      excelData = excelData.map((e: any) => {
        return {
          'Office': e.officeName,
          'InternalAudit': e.datesPending['INTERNAL_AUDIT'] ? this.datePipe.transform(new Date(e.datesPending['INTERNAL_AUDIT']),'MMM dd, YYYY') : "-",
          'PatientCalling': e.datesPending['PATIENT_CALLING'] ? this.datePipe.transform(new Date(e.datesPending['PATIENT_CALLING']),'MMM dd, YYYY') : "-",
          'Lc3': e.datesPending['LC3'] ? this.datePipe.transform(new Date(e.datesPending['LC3']),'MMM dd, YYYY') : "-",
          'office': e.datesPending['OFFICE'] ? this.datePipe.transform(new Date(e.datesPending['OFFICE']),'MMM dd, YYYY') : "-",
          'Billing': e.datesPending['BILLING'] ? this.datePipe.transform(new Date(e.datesPending['BILLING']),'MMM dd, YYYY') : "-",
        }
      })
    }

    excelData = this.removeCurrentTeamNameFromExcel(excelData);

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_Pendency - Other Teams_${this.date}`, options);
    this.showLoader.exportCSVLoader = false;
  }

  removeCurrentTeamNameFromExcel(excelData: any) {
    switch (true) {
      case this.currentTeamId == 3:
        excelData = excelData.map(({ InternalAudit, ...newData }: any) => newData);
        return excelData;
      case this.currentTeamId == 4:
        excelData = excelData.map(({ Aging, ...newData }: any) => newData);
        return excelData;
      case this.currentTeamId == 5:
        excelData = excelData.map(({ Posting, ...newData }: any) => newData);
        return excelData;
      case this.currentTeamId == 6:
        excelData = excelData.map(({ Quality, ...newData }: any) => newData);
        return excelData;
      case this.currentTeamId == 7:
        excelData = excelData.map(({ Billing, ...newData }: any) => newData);
        return excelData;
    }

  }

  selectAll(event: any, filterProperty: any) {

    if (filterProperty == "officeName") {
      this.filteredOfficeName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e['checked'] = false;
        }
      });
      this.filterOfficeName("selectAll");
    }
  }

  addClientNameCrossToOfficeName() {
    this.pendencyData.forEach((e: any) => {
      this.allUserClients.forEach((ele: any) => {
        if (e.clientUuid == ele.uuid) {
          e['clientName'] = ele.name;
        }
      })
    })
    console.log(this.filteredItems);
  }

  filterOfficeName(e?: any, filterProperty?: any) {
    if (!this.pendencyData) return;
    if (!e) {
      this.filteredItems = JSON.parse(JSON.stringify(this.pendencyData));
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
      this.filteredItems = this.pendencyData.filter((item: any) => {
        return this.filteredOfficeName.some((checkbox: any) => {
          return checkbox.checked && item.officeName == checkbox.officeName;
        })
      })
    }

  }
  
  removeDuplicateOfficeName(data:any){
    const unique = data.filter((obj:any, index:any) =>
          data.findIndex((item:any) => item.officeName == obj.officeName) == index);

          console.log(unique);
          
  }

  showFilterOptionOfficeName(data: any) {
    if (!this.pendencyData) return;
    this.filteredOfficeName = JSON.parse(JSON.stringify(data));
    this.filteredOfficeName.forEach((e: any) => {
        e['checked'] = true;
    });
    // this.removeDuplicateOfficeName(this.filteredOfficeName);
  }

  switchTab(tab: any) {
    if (!this.pendencyData) return;
    if(tab == 'withoutDos'){
      this.tabValue='withoutDos';
      this.tabSwitch.withoutDos = true;
      this.tabSwitch.withDos=false;
      this.tabSwitch.withDateOfPending = false;
    }
    else if(tab == 'withDOS'){
      this.tabValue='withDOS';
      this.tabSwitch.withDos = true;
      this.tabSwitch.withoutDos = false;
      this.tabSwitch.withDateOfPending = false;
    }
    else if(tab == 'withDOP'){
      this.tabValue='withDOP';
      this.tabSwitch.withDateOfPending = true;
      this.tabSwitch.withoutDos = false;
      this.tabSwitch.withDos=false;
    }

    // tab.withDos = !tab.withDos;
    // tab.withDateOfPending = !tab.withDateOfPending;
    // this.filteredItems = this.pendencyData;
    let event = { target: { checked: true } };  //added so that when tab is swtiched then by default all data should show.
    this.selectAll(event, 'officeName');
  }

  sortData(data: any, sortProp: string, order: any, sortType: string, teamName?: any) {
    this._service.sortData(data, sortProp, order, sortType, teamName);
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
  showHideFilteredDropdown(filterName:any){
    filterName == 'officeName' ? this.showFilteredDropdown.officeName = true  : this.showFilteredDropdown.officeName = false;
   this.fliterName = filterName;
  }

  @HostListener('mouseleave') onMouseLeave(event: Event){
    if(event?.target) {
      setTimeout(() => {
       this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  } 

  setTopOnTotalRow(){
    let thead:any =  document.querySelector("thead tr th")
    let totalRow:any = document.querySelector(".totalRow");
    if(totalRow){
      totalRow.style.top = thead.clientHeight+"px";
     }
   } 

   downloadPdf(){
    if(this.filteredItems.length!=0){
      const matchedTeam = this.teamData.find((item:any) => item.teamId === parseInt(this.currentTeamId));
    this.currentTeamName = matchedTeam ? matchedTeam.teamName.toUpperCase() : null;
    console.log(this.currentTeamName);    
    const teamsData: string[] = this.teamName.filter((team:any) => team !== this.currentTeamName);
    teamsData.sort((a, b) => a.localeCompare(b));
    let data = {"fileName":"AllPendancy","data": this.filteredItems,"clientName": this.clientName,"tabSwitch":this.tabValue,"currentTeamName":this.currentTeamName,"totalCount":this.totalCount,"currentTeamId":this.currentTeamId,"teamsData":teamsData};
    this. _service.allPendancyPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        console.log(res.body);
        this.downloadService.saveBolbData(res.body, "Pendancy- Other Teams.pdf");
      }else{
        console.log("something went wrong");
      }
    })
  }

}
}
