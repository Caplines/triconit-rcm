import { Component, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { ClaimAssignmentDataModel } from '../../models/claim-assignmen-data-model';
import { ClaimAssignmentModel } from '../../models/claim-assignment.model';
import { ClaimAssignmentPullModel } from '../../models/claim-assignment-pull-model';
import { BillingList } from '../../models/billing-list-model';
import { Title } from '@angular/platform-browser';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { Router } from '@angular/router';
import Utils from '../../util/utils';
import { DownLoadService } from 'src/app/service/download.service';

@Component({
  selector: 'claim-office-assignment',
  templateUrl: './office-assignment.component.html',
  styleUrls: ['./office-assignment.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class OfficeAssignmentComponent implements OnInit {

  // claimAssignmentModel: ClaimAssignmentModel = new ClaimAssignmentModel();
  claimAssigmentPullModel: ClaimAssignmentPullModel = new ClaimAssignmentPullModel();
  bl: BillingList = new BillingList();

  claimData: Array<ClaimAssignmentDataModel>;

  bType: string = "-1";
  insType: string = "All";

  isSorted: any = {};
  teamId: any;
  userByTeam: any = [];
  assignOfficeDetails: any = { 'assignOfficeDetails': [], 'teamId': '' };
  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };
  loader: any = { 'showLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false, 'assignLoader': false };
  showExportLoader: boolean = false;
  totalClaimData: any = { 'oldestOpdt': '', 'oldestOpdos': '', 'totalCount': 0, 'totalRemLiteReject': 0, 'totalcountAndRemLiteReject': 0 }
  clientName: string = '';
  date: any;

  showFilteredDropdown: any = { 'officeName': false,'companyName':false };
  isFilterAllSelected: any = { 'officeName': false,'companyName':false };
  filteredCompanyName:any=[];
  filteredItems: any = [];
  filterName:any;

  constructor(private appService: ApplicationServiceService, private title: Title, private router: Router, private downloadService: DownLoadService) {
    title.setTitle(Utils.defaultTitle + "Claim Office Assignment");
    this.claimData = [];//{} as FreshClaimPLogs;
    console.log(this.router.url)
  }

  ngOnInit(): void {
    this.fetchClaimAssignments();
    this.teamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
    this.getUserByTeamId();
    this.assignOfficeDetails.teamId = this.teamId;
    this.setTopOnTotalRow();
    window.addEventListener("resize", this.setTopOnTotalRow);  //event added todynamically set style top on totalRow
  }

  fetchClaimAssignments() {
    let ths = this;
    ths.loader.showLoader = true;
    ths.claimAssigmentPullModel.claimType = [];
    ths.claimAssigmentPullModel.insuranceType = [];
    ths.totalClaimData.totalCount = ths.totalClaimData.totalRemLiteReject = ths.totalClaimData.totalcountAndRemLiteReject = 0;
    if (ths.bType == '-1') {
      ths.bl.bills.forEach(e => {
        if (e.key != '-1')
          ths.claimAssigmentPullModel.claimType.push(Number(e.key));
      });
    } else {
      ths.claimAssigmentPullModel.claimType.push(Number(ths.bType));
    }
    if (ths.insType == 'All') {
      ths.claimAssigmentPullModel.insuranceType = null;
      /*
      ths.bl.insTypes.forEach(e => {
        if (e.key != 'All')
          ths.claimAssigmentPullModel.insuranceType.push(String(e.key))
      });*/
    } else {
      ths.claimAssigmentPullModel.insuranceType.push(ths.insType);
    }

    ths.appService.fetchClaimAssignments(ths.claimAssigmentPullModel, (res: any) => {

      if (res.status === 200) {
        ths.claimData = res.data;
        this.showFilterOptioncompanyName(ths.claimData);
        this.filterCompanyName();
        this.setTopOnTotalRow();
        ths.loader.showLoader = false;
      } else {
        //ERROR
      }

    });
  }

  getCompany() {
    this.appService.fetchCompanyNameData((callback: any) => {
      if (callback) {
        console.log(callback);
      }
    })
  }

  getUserByTeamId() {
    this.appService.fetchUserByTeamId((callback: any) => {
      if (callback) {
        this.userByTeam = callback.data
      }
    })
  }

  selectNewAssignedUser(evt: any, officeUuid: any) {

    if (this.assignOfficeDetails.assignOfficeDetails.length == 0) {
      this.assignOfficeDetails.assignOfficeDetails.push(
        {
          'userId': evt.target.value,
          'officeId': officeUuid
        })
    } else {
      this.assignOfficeDetails.assignOfficeDetails.find((e: any) => {
        if (e.officeId === officeUuid) {
          e.userId = evt.target.value;
        }
      })
      let officeIdExist = this.assignOfficeDetails.assignOfficeDetails.some((e: any) => e.officeId === officeUuid);
      if (!officeIdExist) {
        this.assignOfficeDetails.assignOfficeDetails.push(
          {
            'userId': evt.target.value,
            'officeId': officeUuid
          });
      }

    }
    console.log(this.assignOfficeDetails.assignOfficeDetails)
  }

  saveAssignments() {
    this.loader.assignLoader = true;
    this.appService.assignOffice(this.assignOfficeDetails, (callback: any) => {
      if (callback.status == 200) {
        this.loader.assignLoader = false;
        this.showAlertPopup(callback);
        // scrollTo(0,0);
        this.assignOfficeDetails.assignOfficeDetails = [];
      } else {
        this.showAlertPopup(callback);
        scrollTo(0, 0);
      }
    })
  }

  sortData(data: any, sortProp: string, order: any, sortType: string) {
    this.appService.sortData(data, sortProp, order, sortType);
  }


  calcCount(data: any) {
    this.totalClaimData.totalCount= 0;
    data.forEach((e: any) => {
      this.totalClaimData.totalCount = this.totalClaimData.totalCount + e.count;
    });
  }

  calcRemLiteReject(data: any) {
    this.totalClaimData.totalRemLiteReject=0;
    data.forEach((e: any) => {
      this.totalClaimData.totalRemLiteReject = this.totalClaimData.totalRemLiteReject + e.remoteLiteRejections;
    });
  }

  calcCountAndRemLiteReject(data: any) {
    this.totalClaimData.totalcountAndRemLiteReject=0;
    data.forEach((e: any) => {
      if(this.teamId == 7){
        this.totalClaimData.totalcountAndRemLiteReject = this.totalClaimData.totalcountAndRemLiteReject + e.count + e.remoteLiteRejections;
        
      } else{
        this.totalClaimData.totalcountAndRemLiteReject = this.totalClaimData.totalcountAndRemLiteReject + e.count ;
      }
    });
  }

  exportToCsv() {
    this.loader.exportCSVLoader = true;
    let options: any = {
      showLabels: true,
      headers: ["Client","Office", "User Assignment", "Oldest Pending Date", "Oldest Pending DOS", "# of Claims to be Billed", this.teamId==7? "# of RemoteLite Rejections" : '', this.teamId==7?"Total Pendency":''],
    }
    let excelData: any = JSON.parse(JSON.stringify(this.filteredItems));
    excelData = excelData.map((e: any) => {
      e['officeAssignedTo'] = e.fname ? e.fname + " " + e.lname : "-";
      // if(e.opdosd){
      //   let date:Date = new Date(e.opdosd);
      //   e.opdosd =  `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}`;
      //   //e.opdosd =  "Jan 14', 2023";
      // }else{
      //   e.opdosd = 'N/A';
      // }
      // if(e.opdtd){
      //   let date:Date = new Date(e.opdtd);
      //   e.opdtd =  `${this.getMonthName(date.getMonth())} ${date.getDate()}, ${date.getFullYear()}`;
      // }else{
      //     e.opdtd = 'N/A';
      // }
      e['totalBillingRejection'] = e.remoteLiteRejections + e.count;
      return e;
    })

    excelData = excelData.map(
      ({ officeUuid, assignedUser, ...newClaimData }: any) => newClaimData);

    excelData = excelData.map((e: any) => {
      return {
        "Client":e.companyName,
        "Office": e.officeName,
        "User Assignment": e.officeAssignedTo,
        "Oldest Pending Date": e.opdtd,
        "Oldest Pending DOS": e.opdosd,
        "# of Claims to be Billed": e.count,
        "# of RemoteLite Rejections": this.teamId ==7 ? e.remoteLiteRejections : '',
        "Total Pendency":  this.teamId==7? e.count + e.remoteLiteRejections  : ''
      }
    })

    excelData.unshift(                                        //method is used to show Total Row in CSV.
      {
        "Office": 'Total',
        "Client": '-',
        "User Assignment": '-',
        "Oldest Pending Date": '-',
        "Oldest Pending DOS": '-',
        "# of Claims to be Billed": this.totalClaimData.totalCount,
        "# of RemoteLite Rejections": this.teamId==7? this.totalClaimData.totalRemLiteReject:'',
        "Total Pendency": this.teamId==7?this.totalClaimData.totalcountAndRemLiteReject:''
      }
    )

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`
    new ngxCsv(excelData, `${this.clientName}_Pendency_${this.date}`, options);
    this.loader.exportCSVLoader = false;

  }

  showAlertPopup(res: any) {
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    res.status == 400 ? this.alert.isError = true : this.alert.isError = false;
    this.alert.alertMsg = res.message ? res.message : res.result.message;
  }
  getMonthName(month: any) {
    const monthNames = [
      "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];
    return monthNames[month];
  }

  setTopOnTotalRow() {
    let thead: any = document.querySelector("thead tr th")
    let totalRow: any = document.querySelector(".totalRow");
    if (totalRow) {
      totalRow.style.top = thead.clientHeight + "px";
    }
  }
  downloadPdf() {
    this.loader.exportPDFLoader = true;
    let data = { "fileName": "Pendancy", "data": this.filteredItems, "totalCount": this.totalClaimData.totalCount, "totalRemLiteReject": this.totalClaimData.totalRemLiteReject, "totalcountAndRemLiteReject": this.totalClaimData.totalcountAndRemLiteReject, "clientName": this.clientName ,"currentTeamId":this.teamId };
    this.appService.pendancyPdfDownload(data, "pdf", (res: any) => {
      if (res.status === 200) {
        this.date = new Date();
       this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`
        this.downloadService.saveBolbData(res.body, `${this.clientName}_Pendancy_${this.date}.pdf`);
        this.loader.exportPDFLoader = false;
      } else {
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    })
  }

  get isRoleAsso() {
    return Utils.isRoleAsso();
  }

  filterCompanyName(e?: any, filterProperty?: any){
    if (!this.claimData) return;
    if (!e) {
      this.filteredItems = JSON.parse(JSON.stringify(this.claimData));
      this.isFilterAllSelected.companyName = true;
    } else {
      let isAllSelected: boolean = true;
      for (let i = 0; i < this.filteredCompanyName.length; i++) {
        if (this.filteredCompanyName[i].checked == false) {
          isAllSelected = false;
          break;
        }
      }
      this.isFilterAllSelected.companyName = isAllSelected;
      this.filteredItems = this.claimData.filter((item: any) => {
        return this.filteredCompanyName.some((checkbox: any) => {
          return checkbox.checked && item.companyName == checkbox.companyName;
        })
      })
    }
      this.calcCount(this.filteredItems);
      this.calcRemLiteReject(this.filteredItems);
      this.calcCountAndRemLiteReject(this.filteredItems);
      this.addTotalCountAndRemLiterejectField(this.filteredItems);
  }
  
  showFilterOptioncompanyName(data: any) {
    
    if (!this.claimData) return;
    this.filteredCompanyName = JSON.parse(JSON.stringify(data));
    const newArray:any = [];
    const seencompanyNames :any= {};
    this.filteredCompanyName.forEach((item:any) => {
      if (!seencompanyNames.hasOwnProperty(item.companyName)) {
        seencompanyNames[item.companyName] = true;
        newArray.push({'checked':true,'companyName':item.companyName});
      }
    });
    this.filteredCompanyName = newArray;
    console.log(newArray);
    
    this.sortFilteredData(this.filteredCompanyName,'companyName');
  }

  sortFilteredData(filterValue: any,sortBy:any) {
    filterValue.sort((a:any,b:any)=>{
       return a[sortBy].localeCompare(b[sortBy]);
});
}

addTotalCountAndRemLiterejectField(data:any){
  data.forEach((e:any)=>{
    if(this.teamId == 7){
      e['totalBillingRejection'] = e.remoteLiteRejections+e.count;
    } else{
      e['totalBillingRejection'] = e.count;
    }
  })

}


showHideFilteredDropdown(filterName:any){
  filterName == 'companyName' ? this.showFilteredDropdown.companyName = true  : this.showFilteredDropdown.companyName = false;
  this.filterName = filterName;
}

@HostListener('mouseleave') onMouseLeave(event: Event){
  if(event?.target) {
    setTimeout(() => {
     this.showFilteredDropdown[this.filterName] = false;
    }, 500);
  }
} 

selectAll(event: any, filterProperty: any) {

  if (filterProperty == "companyName") {
    this.filteredCompanyName.forEach((e: any) => {
      if (event.target.checked) {
        e.checked = true;
      } else {
        e['checked'] = false;
      }
    });
    this.filterCompanyName("selectAll");
  }
}
}