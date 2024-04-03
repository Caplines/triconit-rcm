import { Component, HostListener } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ngxCsv } from 'ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import Utils from '../../util/utils';
import { DatePipe } from '@angular/common';
import { DownLoadService } from 'src/app/service/download.service';
import { AppConstants } from 'src/app/constants/app.constants';
import { Router } from '@angular/router';
@Component({
  selector: 'all-pendency',
  templateUrl: './all-pendency.component.html',
  styleUrls: ['./all-pendency.component.scss']
})
export class AllPendencyComponent {

  pendencyData: any = [];
  teamData: any = this.constants.teamData.sort((a:any,b:any)=>a.teamName.localeCompare(b.teamName));
  currentTeamId: any;
  showLoader: any = { 'loader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  date: any;
  clientName: string = '';
  showFilteredDropdown: any = { 'officeName': false,'clientName':false };
  isFilterAllSelected: any = { 'officeName': false,'clientName':false };
  filteredOfficeName: any = [];
  filteredClientName:any=[];
  filteredItems: any = [];
  tabSwitch: any = { 'withoutDos': true, 'withDos': false ,'withDateOfPending':false};
  isSorted: any = {};

  totalCount: any = this.constants.teamData;
  datePipeString:any;
  filterName:string= '';
  tabValue:any="withoutDos";
  currentTeamName:string='';
  teamName: any = ["INTERNAL_AUDIT", "LC3", "OFFICE", "PATIENT_CALLING", "BILLING"];

  allUserClients:any=[];

  officeData:any= [];
  showTooltipConfig:any={};
  tooltipText:any={
    'CDP':'CDP team identifies reasons for claim denials & suggests solutions.',
    'CREDENTIALING':'Credentialing team helps in provider enrollment with insurance companies.',
    'INTERNAL_AUDIT':'Internal Audit team ensures you are following billing & clinical compliance.',
    'LC3':'LC3 Team ensures insurance guidelines and billing accuracy.',
    'MEDICAID_IV':'Medicaid IV team verifies patient eligibility for Medicaid patients.',
    'AGING':'Aging team follows up on claim status and work on denials.',
    'ORTHO':'Ortha team follows up on Orthodontics procedure claim status and work on denials.',
    'PATIENT_CALLING':'Patient calling team manages patient inquiries, booking and confirming appointments.',
    'PATIENT_STATEMENT':'Patient Statement team addresses patient responsibility after the claims get settled.',
    'PAYMENT_POSTING':'Posting team manages payment posting and closure of the claim in PMS',
    'PPO_IV':'PPO IV team verifies patient eligibility for PPO insurance patients.',
    'QUALITY':'Quality team ensures the accuracy of all RCM related tasks.',
  }

  constructor(public constants:AppConstants,private _service: ApplicationServiceService, private title: Title,private datePipe: DatePipe,private downloadService:DownLoadService,private router:Router) {
    title.setTitle(Utils.defaultTitle + "Pendency - Other Teams")
    this.clearTotalCount();
  }
  ngOnInit(): void {
    this.getAllUserClients();
    this.currentTeamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
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
      this.showFilterOptionClientName(this.pendencyData);
      this.total(this.pendencyData);
      this.filterOfficeName();
      this.filterClientName();
      this.setTopOnTotalRow();


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

  exportToCsv(fromTable: any) {
    let totalRow: any = {};
    this.showLoader.exportCSVLoader = true;
    let headers: any = [];
    headers.push("Client");
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
        return this.returnData(e,'counts1');
      })

      this.totalCount.forEach((e: any) => {
        if (e.teamId != this.currentTeamId) {
          totalRow['name'] = 'Total';
          totalRow['empty'] = '';
          totalRow[`${e.teamName}`] = e.count;
        }
      })
      excelData.unshift(totalRow);
    }

    else if (fromTable == 'dos-table') {
      excelData = excelData.map((e: any) => {
        return this.returnData(e,'dates1');
      })
    }
    else if (fromTable == 'dop-table') {
      excelData = excelData.map((e: any) => {
        return this.returnData(e,'datesPending');
      })
    }

    excelData = this.removeCurrentTeamNameFromExcel(excelData);

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_Pendency - Other Teams_${this.date}`, options);
    this.showLoader.exportCSVLoader = false;
  }

  returnData(e:any,property:any){
    let obj:any={};
    if(property == 'counts1'){
      for(const i of this.constants.teamData){
        obj['client'] = e.clientName;
        obj['office'] = e.officeName;
        obj[i.teamName] =  e[property][i.unFormatedName];
      }
      return obj;
    } else{
         for(const i of this.constants.teamData){
        obj['client'] = e.clientName;
        obj['office'] = e.officeName;
        obj[i.teamName] =  e[property][i.unFormatedName] ?  this.datePipe.transform(new Date(e[property][i.unFormatedName]),'MMM dd, YYYY') : "-";
      }
      return obj;
    }
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
    if (filterProperty == "clientName") {
      this.filteredClientName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e['checked'] = false;
        }
      });
      this.filterClientName("selectAll");
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
    console.log(this.pendencyData);
    
    // this.sortOfficesOnly(this.pendencyData);
  }

  sortOfficesOnly(data:any){
      this.pendencyData =  data.sort((a:any,b:any)=>a.officeName.localeCompare(b.officeName))
    console.log(this.pendencyData);
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
  
  showFilterOptionOfficeName(data: any) {
    if (!this.pendencyData) return;
    this.filteredOfficeName = JSON.parse(JSON.stringify(data));
    
    const newArray:any = [];
    const seenOfficeNames :any= {};
    this.filteredOfficeName.forEach((item:any) => {
      if (!seenOfficeNames.hasOwnProperty(item.officeName)) {
        seenOfficeNames[item.officeName] = true;
        newArray.push({...item,'checked':true});
      }
    });
    this.filteredOfficeName = newArray;
    this.sortFilteredData(this.filteredOfficeName,'officeName');
  }

  filterClientName(e?: any, filterProperty?: any){
    if (!this.pendencyData) return;
    if (!e) {
      this.filteredItems = JSON.parse(JSON.stringify(this.pendencyData));
      this.isFilterAllSelected.clientName = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredClientName.length; i++) {
        if (this.filteredClientName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.clientName = isAllSelected;
      this.filteredItems = this.pendencyData.filter((item: any) => {
        return this.filteredClientName.some((checkbox: any) => {
          return checkbox.checked && item.clientName == checkbox.clientName;
        });
      });
      this.clearTotalCount();
      this.total(this.filteredItems);

    }
  }

  showFilterOptionClientName(data: any) {
    if (!this.pendencyData) return;
    this.filteredClientName = JSON.parse(JSON.stringify(data));
    const newArray:any = [];
    const seenClientNames :any= {};
    this.filteredClientName.forEach((item:any) => {
      if (!seenClientNames.hasOwnProperty(item.clientName)) {
        seenClientNames[item.clientName] = true;
        newArray.push({'checked':true,'clientName':item.clientName});
      }
    });
    this.filteredClientName = newArray;
    console.log(newArray);
    
    this.sortFilteredData(this.filteredClientName,'clientName');
  }

  switchTab(tab: any) {
    if (!this.pendencyData) return;
    if(tab == 'withoutDos'){
      this.tabValue='withoutDos';
      this.tabSwitch.withoutDos = true;
      this.tabSwitch.withDos=false;
      this.tabSwitch.withDateOfPending = false;
      setTimeout(()=>{
        this.setTopOnTotalRow();
      });
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

  sortFilteredData(filterValue: any,sortBy:any) {
        filterValue.sort((a:any,b:any)=>{
           return a[sortBy].localeCompare(b[sortBy]);
    });
  }

  showHideFilteredDropdown(filterName:any){
    filterName == 'officeName' ? this.showFilteredDropdown.officeName = true  : this.showFilteredDropdown.officeName = false;
    filterName == 'clientName' ? this.showFilteredDropdown.clientName = true  : this.showFilteredDropdown.clientName = false;
    this.filterName = filterName;
  }

  @HostListener('mouseleave') onMouseLeave(event: Event){
    if(event?.target) {
      setTimeout(() => {
       this.showFilteredDropdown[this.filterName] = false;
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
    this.showLoader.exportPDFLoader = true;
    if(this.filteredItems.length!=0){
    const matchedTeam = this.constants.teamData.find((item:any) => item.teamId == this.currentTeamId);
      let teamsData:any = [];
      this.constants.teamData.forEach((team:any)=>{
      if(team.teamId != this.currentTeamId){
        teamsData.push(team.teamName)
      }
    });
    teamsData = teamsData.sort((a:any, b:any) => a.localeCompare(b)); 
    let data = {"fileName":"AllPendancy","data": this.filteredItems,"clientName": this.clientName,"tabSwitch":this.tabValue,"currentTeamName":matchedTeam.unFormatedName,"totalCount":this.totalCount,"currentTeamId":this.currentTeamId,"teamsData":teamsData};
    this. _service.allPendancyPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.date = new Date();
        this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`
        this.downloadService.saveBolbData(res.body, `${localStorage.getItem("selected_clientName")}_Pendancy- Other Teams_${this.date}.pdf`);
        this.showLoader.exportPDFLoader = false;
      }else{
        console.log("something went wrong");
        this.showLoader.exportPDFLoader = false;
      }
    })
  }
}

clearTotalCount(){
  this.totalCount.forEach((e:any)=>{
    e.count=0;
  })
}

openSpecificPendencyData(user:any,teamId:any){

  const queryParams = { pageName: 'Other' };
  const url =  this.router.createUrlTree([`/pendency/${user.officeUuid}/${user.clientUuid}/${teamId}`],{queryParams}).toString();
   window.open(url, '_blank');
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


}
