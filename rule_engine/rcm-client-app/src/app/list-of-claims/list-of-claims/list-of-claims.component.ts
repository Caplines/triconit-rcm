import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService } from '../../service/application-service.service';
import { AppConstants } from '../../constants/app.constants';
import { ClaimAssociateDetailModel } from '../../models/claim-associate-detail-model';
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";
import { ngxCsv } from 'ngx-csv/ngx-csv';
import Utils from '../../util/utils';
import { Title } from '@angular/platform-browser';

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
  isSorted: boolean = false;
  loader: any = { 'billingLoader': false, 'listClaimLoader': false,'exportPDFLoader':false,'exportCSVLoader':false };
  showFilteredDropdown: any= {'officeName':false,'claimType':false,'insuranceType':false,'insuranceName':false,'lastTeamWorked':false,'actionRequired':false};
  filteredItems: any = [];
  filteredOfficeName: any = [];
  selectedCheckboxOptions: any = [];
  date:any;

  isFilterAllSelected:any={'officeName':false,'claimType':false,'insuranceType':false,'insuranceName':false,'lastTeamWorked':false,'actionRequired':false};
  filteredClaimType:any=[];
  filteredInsuranceName:any=[];
  filteredInsuranceType:any=[];
  filteredActionRequired:any=[];
  // filteredLastTeamWorked:any=[];
  clientName:string='';

  constructor(private appService: ApplicationServiceService, public appConstants: AppConstants,private title:Title) {
    this.selectedBtype = this.appConstants.BILLING_ID;
    title.setTitle(Utils.defaultTitle + "List Of Claims");
  }


  ngOnInit(): void {
    this.fetchClaims(this.selectedSubtype);
    this.clientName = localStorage.getItem("selected_clientName");
  }

  fetchOfficeByUuid() {
    this.appService.fetchOfficeByUuid((res: any) => {
      if (res.status) {
        res.data = res.data.map((e:any)=>{
          return{
            ...e,
            "officeName":e.name,
          }
        })
        this.showFilterOptionOfficeName(res.data);
      }
    })
  }



  fetchClaims(subType: string) {
    this.loader.listClaimLoader = true;
    let ths = this;
    ths.appService.fetchAssociateClaimDet(ths.selectedBtype, subType, (res: any) => {
      if (res.status === 200) {
        ths.claimDetail =  this.removePrefix(res.data);
        // ths.claimDetail =  res.data;
        ths.loader.listClaimLoader = false;
        this.filterOfficeName();
        this.fetchOfficeByUuid();
        this.filterOptionClaimType();
        this.filterOptionActionRequired();
        this.filterOptionInsuranceName();
        this.filterOptionInsuranceType();
        // this.filterOptionLastTeamWorked();
      } 
      // else {
      //   this.loader.listClaimLoader = false;
      //   if(res.data == "not Autorized")
      //   this.logout();
      //   //ERROR
      // }

    });
  }

  filterOptionClaimType(){
  
    this.filteredClaimType.push({'checked':true,'claimType':'Primary'},{'checked':true,'claimType':'Secondary'})
    this.isFilterAllSelected.claimType = true;
  }

  filterOptionActionRequired(){
    this.filteredActionRequired.push({'checked':true,'actionRequired':"Billing","statusType":1},{'checked':true,'actionRequired':"Re-Billing","statusType":2});
    this.isFilterAllSelected.actionRequired = true;
  }

  filterOptionInsuranceName(){
    this.filteredItems.forEach((e:any)=>{
      if(e.claimId.includes("_P")){
        this.filteredInsuranceName.push({'checked':true,'insuranceName':e.primaryInsurance});
        e['insuranceName']=e.primaryInsurance;
      }else if(e.claimId.includes("_S")){
        this.filteredInsuranceName.push({'checked':true,'insuranceName':e.secondaryInsurance});
        e['insuranceName']=e.secondaryInsurance;
      }
    })
    this.isFilterAllSelected.insuranceName = true;
  }

  filterOptionInsuranceType(){
    this.filteredItems.forEach((e:any)=>{
      if(e.claimId.includes("_P")){
        this.filteredInsuranceType.push({'checked':true,'insuranceType':e.prName});
        e['insuranceType']=e.prName;
      }else if(e.claimId.includes("_S")){
        this.filteredInsuranceType.push({'checked':true,'insuranceType':e.secName});
        e['insuranceType']=e.secName;
      }
    })
    this.filteredInsuranceType = Array.from(new Set(this.filteredInsuranceType.map((a:any) => a.insuranceType)))
      .map((insuranceType:any) => {
        return this.filteredInsuranceType.find((a:any) => a.insuranceType === insuranceType);
      });

    this.isFilterAllSelected.insuranceType = true;
  }

  // filterOptionLastTeamWorked(){
  //   this.appConstants.teamData.forEach((e:any)=>{
  //     this.filteredLastTeamWorked.push({'checked':true,'lastTeam':e.teamName});
  //   })
  //   this.isFilterAllSelected.lastTeamWorked = true;
  // }

  removePrefix(data:any){
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
          if(ele['claimId'].includes("_P")){
            ele['claimType'] ="Primary" 
          }else if(ele['claimId'].includes("_S")){
            ele['claimType']="Secondary"
          }
      })
    });
    
  }

  filterOfficeName(e?: any,filterProperty?:any) {
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

  filterClaimType(filterProperty:any){
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
  }

  filterActionRequired(filterProperty:any){
    let isAllSelected: boolean = true;
    for (let i = 0; i < this.filteredActionRequired.length; i++) {
      if (this.filteredActionRequired[i].checked == false) {
        isAllSelected = false;
        break;
      }
    }
    this.isFilterAllSelected.actionRequired = isAllSelected;
    this.filteredItems = this.claimDetail.filter((item: any) => {
      return this.filteredActionRequired.some((checkbox: any) => {
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
  }

  filterInsuranceName(filterProperty:any){
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
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
  }

  filterInsuranceType(filterProperty:any){
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
        return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
      });
    });
  }

  // filterLastTeamWorked(filterProperty:any){
  //   let isAllSelected: boolean = true;
  //   for (let i = 0; i < this.filteredLastTeamWorked.length; i++) {
  //     if (this.filteredLastTeamWorked[i].checked == false) {
  //       isAllSelected = false;
  //       break;
  //     }
  //   }
  //   this.isFilterAllSelected.lastTeamWorked = isAllSelected;
  //   this.filteredItems = this.claimDetail.filter((item: any) => {
  //     return this.filteredLastTeamWorked.some((checkbox: any) => {
  //       return checkbox.checked && checkbox[filterProperty] == item[filterProperty];
  //     });
  //   });
  // }

  saveToPdf(divName: any) {
    this.loader.exportPDFLoader=true;
    let m:any=document.querySelector(".table-wrapper-scroll-y");
    m.classList.remove('table-wrapper-scroll-y');
    m.classList.remove('table-inner-scrollbar');
    html2canvas(<any>document.getElementById(divName)).then(canvas => {
      const content = canvas.toDataURL('image/png');
      let pdf = new jsPDF('p', 'mm', 'a4');
      let width = pdf.internal.pageSize.getWidth();
      let height = canvas.height * width / canvas.width;
      // Insert office name
      pdf.setFontSize(10);  // Adjust the font size as needed
      pdf.text(`RCM Tool-${this.clientName}`, 3, 10);
      pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`);
      this.loader.exportPDFLoader=false;
      m.classList.add('table-wrapper-scroll-y');
      m.classList.add('table-inner-scrollbar');
    });
  }

  exportToCsv() {
    this.loader.exportCSVLoader=true;
    let options: any = {
      showLabels:true,
      headers: ["Office", "Patient ID", "Patient Name",'Date of Service',  "Claim Age","Timely Filing Limit (Days)", "Claim Type","Action Required", "Insurance Name","Insurance Type",  "Estimated Amount" ]
    }
    let excelData: any;
    excelData = [...this.filteredItems];  //creating a copy of data so that nothing affects original data.
    excelData = excelData.map((e: any) => {
      if (e.dos) {
        let date: Date = new Date(e.dos);
        e = { ...e, dos: `${date.getMonth()+1}/${date.getDate()}/${date.getFullYear()}` };
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
      if(e.lastTeam == null){
        e = {...e,lastTeam:'-'}
      }
      return e;
    })      //method add value as "-" or "0", if its empty or null.

    excelData = excelData.map(
      ({ claimId,opdos, opdt, secTotal, uuid, billedAmount, statusType,  ...newClaimData }: any) => newClaimData);    //methods removes unwanted properties that are not going to display in CSV.

      excelData = excelData.map((e:any)=>{
        return{
          "Office Name":e.officeName,
          "Patient ID":e.patientId,
          "Patient Name":e.patientName,
          'Date of Service':e.dos,
          "Claim Age":e.claimAge,
          "Timely Filing Limit (Days)":e.timelyFilingLimitData ? e.timelyFilingLimitData : "-",
          "Claim Type":e.claimType,
          "Action Required":e.actionRequired,
          "Insurance Name":e.primaryInsurance ? e.primaryInsurance : e.secondaryInsurance,
          "Insurance Type":e.prName? e.prName : e.secName,
          "Estimated Amount": e.claimId?.endsWith("_P") ? (e.primTotal ? e.primTotal : "0") : e.secTotal ? e.secTotal : "0",
        }
      })  //method aligns the header to the value in CSV.

      this.date = new Date();
      this.date = `${this.date.getMonth()+1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      console.log(excelData.sort());
      new ngxCsv(excelData,`${localStorage.getItem("selected_clientName")}_List_of_Claims_${this.date}`, options);
      this.loader.exportCSVLoader=false;
  }


  selectAll(event:any,filterProperty:any){
    if(filterProperty == "officeName"){
      this.filteredOfficeName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterOfficeName("selectAll");
    }
    if(filterProperty == "claimType"){
      this.filteredClaimType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterClaimType("claimType");
    }
    if(filterProperty == "actionRequired"){
      this.filteredActionRequired.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterActionRequired("statusType");
    }
    if(filterProperty == "insuranceName"){
      this.filteredInsuranceName.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceName("insuranceName");
    }
    if(filterProperty == "insuranceType"){
      this.filteredInsuranceType.forEach((e: any) => {
        if (event.target.checked) {
          e.checked = true;
        } else {
          e.checked = false;
        }
      });
      this.filterInsuranceType("insuranceType");
    }
    // if(filterProperty == "lastTeam"){
    //   this.filteredLastTeamWorked.forEach((e: any) => {
    //     if (event.target.checked) {
    //       e.checked = true;
    //     } else {
    //       e.checked = false;
    //     }
    //   });
    //   this.filterLastTeamWorked("lastTeam");
    // }
  }

  logout() {
    Utils.logout();
  }
}
