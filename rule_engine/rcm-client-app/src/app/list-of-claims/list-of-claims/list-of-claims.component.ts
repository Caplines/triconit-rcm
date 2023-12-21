import { Component, OnInit, LOCALE_ID, Inject, HostListener } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';
import { formatNumber } from '@angular/common';
import { DownLoadService } from 'src/app/service/download.service';

@Component({
  selector: 'app-list-of-claims',
  templateUrl: './list-of-claims.component.html',
  styleUrls: ['./list-of-claims.component.scss']
})
export class ListOfClaimsComponent implements OnInit {

  selectedBtype: number = 0;
  selectedSubtype: string = "Fresh";
  claimDetail: Array<ClaimAssociateDetailModel>;
  expandCollapse: boolean = true;
  switchBox: any = { 'billing': true, 'reBilling': false };
  isSorted: any = {};
  loader: any = { 'billingLoader': false, 'listClaimLoader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  showFilteredDropdown: any = { 'officeName': false, 'claimType': false, 'insuranceType': false, 'insuranceName': false, 'lastTeamWorked': false, 'actionRequired': false, 'ageBracket' : false };
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  date: any;

  isFilterAllSelected: any = { 'officeName': false, 'claimType': false, 'insuranceType': false, 'insuranceName': false, 'lastTeamWorked': false, 'actionRequired': false, 'ageBracket' : false };

  filteredColumnData:any={insuranceName:[],insuranceType:[],actionRequired:[],lastTeamWorked:[],ageBracket:[],claimType:[]};
  clientName: string = '';
  isFilterValueExist: boolean = false;
  isLastTeam: boolean = false;
  fliterName: string = '';
  tabSwitch: any = { 'Fresh': true, 'sendBack': false, 'MyClaims': false };
  tabValue:any;
  accessToListOfClaims:any;
  currentTeamId:number;
  @HostListener('mouseleave') onMouseLeave(event: Event) {
    if (event?.target) {
      setTimeout(() => {
        this.showFilteredDropdown[this.fliterName] = false;
      }, 500);
    }
  }

  constructor(@Inject(LOCALE_ID) private locale: string, private appService: ApplicationServiceService, public appConstants: AppConstants, private title: Title,private downloadService:DownLoadService) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    this.isAccessToListOfClaims();
    this.clientName = localStorage.getItem("selected_clientName");
    this.currentTeamId = Utils.selectedTeam();
  }

  isAccessToListOfClaims(){
    if(Utils.selectedTeam() == 7 || Utils.selectedTeam() == 3){
      this.accessToListOfClaims = true;
      this.fetchClaims(this.selectedSubtype);
    } else{
      this.accessToListOfClaims=false;
    }
    
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



  fetchClaims(subType: string) {
    this.loader.listClaimLoader = true;
    let ths = this;
    if (subType == 'sendBack') {
      this.isLastTeam = true;
      this.selectedSubtype = 'sendBack';
    } else {
      this.isLastTeam = false;
      this.selectedSubtype = 'Fresh';
    }
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, subType, (res: any) => {
      if (res.status === 200) {
        ths.claimDetail = res.data;
        let data: any = ths.claimDetail.map((e: any) => {
          if (e.claimId.endsWith("_P")) {
            e['EstAmount'] = e.primeSecSubmittedTotal;
          } else {
            e['EstAmount'] = e.secTotal;
          }
          return e;
        })
        ths.claimDetail = data;
        //console.log(ths.claimDetail);

        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
        this.filterOptionClaimType(subType);
        this.filterOptionActionRequired(subType);
        this.filterOptionInsuranceName(subType);
        this.filterOptionInsuranceType(subType);
        this.filterOptionLastTeamWorked();
        this.filterOptionAgeBracket(subType);
        this.showAgeBracket_WithColor_AndClaimIdDigits();
      }
      // else {
      //   this.loader.listClaimLoader = false;
      //   if(res.data == "not Autorized")
      //   this.logout();
      //   //ERROR
      // }

    });
  }

  filterOptionClaimType(subType: string) {
    if (subType == 'Fresh' && this.isFilterValueExist) {
      this.filteredColumnData.claimType = [];
    }
    if (subType == 'Fresh') {
      this.filteredColumnData.claimType.push({ 'checked': true, 'claimType': 'Primary' }, { 'checked': true, 'claimType': 'Secondary' });
      this.isFilterValueExist = true;
    }
    this.isFilterAllSelected.claimType = true;
  }

  filterOptionAgeBracket(subType: string) {
    if ((subType == 'Fresh' && this.isFilterValueExist) || (subType == 'sendBack' && this.isFilterValueExist)) {
      this.filteredColumnData.ageBracket = [];
    }
    if (subType == 'Fresh') {
      this.filteredColumnData.ageBracket.push({ 'checked': true, 'ageBracket': '0-30' }, { 'checked': true, 'ageBracket': '31-60' } , { 'checked': true, 'ageBracket': '61-90' }, { 'checked': true, 'ageBracket': '90+' });
      this.isFilterValueExist = true;
    }
    if (subType == 'sendBack') {
      this.filteredColumnData.ageBracket.push({ 'checked': true, 'ageBracket': '0-30' }, { 'checked': true, 'ageBracket': '31-60' } , { 'checked': true, 'ageBracket': '61-90' }, { 'checked': true, 'ageBracket': '90+' });
      this.isFilterValueExist = true;
    }
    this.isFilterAllSelected.ageBracket = true;
  }

  filterOptionActionRequired(subType: string) {
    if (subType == 'Fresh' && this.isFilterValueExist) {
      this.filteredColumnData.actionRequired = [];
    }
    if (subType == 'Fresh') {
      if(this.currentTeamId==3){
        this.filteredColumnData.actionRequired.push({ 'checked': true, 'actionRequired': "Auditing", "statusType": 1 });
      }else{
        this.filteredColumnData.actionRequired.push({ 'checked': true, 'actionRequired': "Billing", "statusType": 1 }, { 'checked': true, 'actionRequired': "Re-Billing", "statusType": 2 });
      }
   
      this.isFilterValueExist = true;
    }
    this.isFilterAllSelected.actionRequired = true;
  }

  filterOptionInsuranceName(subType: string) {
    if ((subType == 'Fresh' && this.isFilterValueExist) || (subType == 'sendBack' && this.isFilterValueExist)) {
      this.filteredColumnData.insuranceName = [];
    }
    if (subType == 'Fresh') {
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
    }
    if (subType == 'sendBack') {
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
    }
    this.sortFiltereData(this.filteredColumnData.insuranceName);
    this.isFilterAllSelected.insuranceName = true;
  }

  filterOptionInsuranceType(subType: string) {
    if ((subType == 'Fresh' && this.isFilterValueExist) || (subType == 'sendBack' && this.isFilterValueExist)) {
      this.filteredColumnData.insuranceType = [];
    }
    if (subType == 'Fresh') {
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
    }
    if (subType == 'sendBack') {
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
    }
    this.sortFiltereData(this.filteredColumnData.insuranceType);
    this.isFilterAllSelected.insuranceType = true;
  }

  filterOptionLastTeamWorked() {
    this.appConstants.teamData.forEach((e: any) => {
      this.filteredColumnData.lastTeamWorked.push({ 'checked': true, 'lastTeam': e.teamName });
    })
    this.isFilterAllSelected.lastTeamWorked = true;
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
      this.addOrRemoveFilterOffice();
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
  }

  filterLastTeamWorked(filterProperty: any) {
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredColumnData.lastTeamWorked.length; i++) {
      if (this.filteredColumnData.lastTeamWorked[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.lastTeamWorked = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredColumnData.lastTeamWorked.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
  }

  exportToCsv() {
    this.loader.exportCSVLoader = true;
    let options: any = {
      showLabels: true,
      headers: ["Office", "Claim Id", "Patient ID","Patient Name", 'DOS', "Claim Age", "TFL", "Pending Since Date", "Age Bracket", "Claim Type", "Action Required", "Insurance Name", "Insurance Type", "Est. Amount", this.tabSwitch.sendBack ? "BillingAmount" : '', this.isLastTeam ? "Last Team that Worked on this claim" : ""]
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
      if(this.currentTeamId==3){
        e = { ...e, ['actionRequired']: "Auditing" };
      }
       else{
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
      if (e.lastTeam == null) {
        e = { ...e, lastTeam: '-' }
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


    if (this.tabSwitch.Fresh || this.tabSwitch.MyClaims) {


      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim Id":e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          "Pending Since Date":e.pendingSince,
          "Age Bracket":e.ageBracket,
          "Claim Type": e.claimType,
          "Action Required": e.actionRequired,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Est. Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Last Team that Worked on this claim": this.isLastTeam ? e.lastTeam : ""
        }
      })
      excelData = excelData.map(
        ({ claimId, opdos, opdt, secTotal, uuid, statusType, billedAmount, EstAmount, ...newClaimData }: any) => newClaimData);

    } else {
      excelData = excelData.map((e: any) => {
        return {
          "Office Name": e.officeName,
          "Claim Id":e.newClaimId,
          "Patient ID": e.patientId,
          "Patient Name": e.patientName,
          'DOS': e.dos,
          "Claim Age": e.claimAge,
          "TFL": e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          "Pending Since Date":e.pendingSince,
          "Age Bracket":e.ageBracket,
          "Claim Type": e.claimType,
          "Action Required": e.actionRequired,
          "Insurance Name": e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type": e.prName ? e.prName : e.secName,
          "Estimated Amount": e.claimId?.endsWith("_P") ? (e.primeSecSubmittedTotal ? '$' + formatNumber(e.primeSecSubmittedTotal, this.locale, '.0-0').toString() : "$0") : e.secTotal ? '$' + formatNumber(e.secTotal, this.locale, '.0-0').toString() : "$0",
          "Billing Amount": e.billedAmount ? '$' + formatNumber(e.billedAmount, this.locale, '.0-0').toString() : "$0",
          "Last Team that Worked on this claim": this.isLastTeam ? e.lastTeam : ""
        }
      })  //method aligns the header to the value in CSV.
      excelData = excelData.map(
        ({ claimId, opdos, opdt, secTotal, uuid, statusType, EstAmount, ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.
    }

    this.date = new Date();
    this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
    //console.log(excelData.sort());
    new ngxCsv(excelData, `${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
    this.loader.exportCSVLoader = false;
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
      
      // event.target.checked ? '' : this.multiselectInsuranceChild.clearAll('locInsuranceName','filterByInsuranceName');
      // this.multiselectInsuranceChild.clearAll('locInsuranceName','filterByInsuranceName');

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
    // if(filterProperty == "lastTeam"){
    //   this.filteredColumnData.lastTeamWorked.forEach((e: any) => {
    //     if (event.target.checked) {
    //       e.checked = true;
    //     } else {
    //       e.checked = false;
    //     }
    //   });
    //   this.filterLastTeamWorked("lastTeam");
    // }
  }

  sortFiltereData(filterValue: any) {
    filterValue.sort((a: any, b: any) => {
      const nameA = Object.keys(filterValue[0])[1] == 'insuranceType' ? a.insuranceType?.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? a.insuranceName?.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? a.officeName?.toUpperCase() : '';// ignore upper and lowercase
      const nameB = Object.keys(filterValue[0])[1] == 'insuranceType' ? b.insuranceType?.toUpperCase()
        : Object.keys(filterValue[0])[1] == 'insuranceName' ? b.insuranceName?.toUpperCase()
          : Object.keys(filterValue[0])[4] == 'officeName' ? b.officeName?.toUpperCase() : '';// ignore upper and lowercase
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

  logout() {
    Utils.logout();
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

  get isRoleLead() {
    return Utils.isRoleLead();
  }

  fetchClaimsLead(subType: string) {
    this.loader.listClaimLoader = true;
    let ths = this;
    if (subType == 'sendBack') {
      this.isLastTeam = true;
    } else {
      this.isLastTeam = false;
    }
    ths.appService.fetchLeadClaimDet(ths.selectedBtype, subType, (res: any) => {
      if (res.status === 200) {
        ths.claimDetail = this.removePrefix(res.data);
        // ths.claimDetail =  res.data;
        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
        this.filterOptionClaimType(subType);
        this.filterOptionActionRequired(subType);
        this.filterOptionInsuranceName(subType);
        this.filterOptionInsuranceType(subType);
        this.filterOptionLastTeamWorked();
        this.filterOptionAgeBracket(subType)
        this.showAgeBracket_WithColor_AndClaimIdDigits();
      }
      // else {
      //   this.loader.listClaimLoader = false;
      //   if(res.data == "not Autorized")
      //   this.logout();
      //   //ERROR
      // }

    });
  }
  switchTab(tab: any) {
    if (!this.claimDetail) return;
    if (tab == 'Fresh') {
      this.tabValue='Fresh';
      this.tabSwitch.Fresh = true;
      this.tabSwitch.sendBack = false;
      this.tabSwitch.MyClaims = false;
      this.fetchClaims('Fresh');
    }
    else if (tab == 'sendBack') {
      this.tabValue='sendBack';
      this.tabSwitch.Fresh = false;
      this.tabSwitch.sendBack = true;
      this.tabSwitch.MyClaims = false;
      this.fetchClaims('sendBack');
    }
    else if (tab == 'MyClaims') {
      this.tabValue='MyClaims';
      this.tabSwitch.Fresh = false;
      this.tabSwitch.sendBack = false;
      this.tabSwitch.MyClaims = true;
      this.fetchClaimsLead('Fresh');
    }

    // tab.withDos = !tab.withDos;
    // tab.withDateOfPending = !tab.withDateOfPending;
    // this.filteredItems = this.pendencyData;
    // let event = { target: { checked: true } };  //added so that when tab is swtiched then by default all data should show.
    // this.selectAll(event, 'officeName');
  }

  downloadPdf(){
    this.loader.exportPDFLoader = true;
    if(this.filteredItems.length!=0){
    let data = {"fileName":"List_Of_Claims","data": this.filteredItems,"clientName": this.clientName,"tabSwitch":this.tabValue,"currentTeamId":this.currentTeamId};
    this. appService.lisOfClaimsPdfDownload(data,"pdf",(res: any) => {
      if (res.status === 200){
        this.date = new Date();
        this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
        console.log(res.body);
        this.downloadService.saveBolbData(res.body, `${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}.pdf`);
        this.loader.exportPDFLoader = false;
      }else{
        console.log("something went wrong");
        this.loader.exportPDFLoader = false;
      }
    })
  }
  }
  
  showAgeBracket_WithColor_AndClaimIdDigits(){
    let currentDate:any = new Date().setHours(0,0,0,0); // To set the time equal
    this.filteredItems.forEach((e:any) => {
      if(e.dos){
           let dos:any = new Date(e.dos);
           const diffTime = Math.abs(currentDate - dos);
           let diffDays:any = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
           e.ageBracket = (diffDays <= 30) ? `0-30`  : (diffDays > 30 && diffDays <= 60) ?  `31-60` : (diffDays > 60 && diffDays <= 90) ? `61-90` : (diffDays > 90) ? `90+` : '';
      }
      if(e.claimId){
        e.newClaimId = e.claimId.replace('_P',"").replace('_S',"")
      }
     if(e.claimAge && e.timelyFilingLimitData){
       if(Number(e.timelyFilingLimitData) - e.claimAge < 30){
       e.colorChange = true;
      }
      else{
        e.colorChange = false;
      }
     }
    });
  }  

}