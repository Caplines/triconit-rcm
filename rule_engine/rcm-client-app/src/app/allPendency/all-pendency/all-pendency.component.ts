import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { ngxCsv } from 'ngx-csv';
import { ApplicationServiceService } from 'src/app/service/application-service.service';
import Utils from '../../util/utils';
@Component({
  selector: 'all-pendency',
  templateUrl: './all-pendency.component.html',
  styleUrls: ['./all-pendency.component.scss']
})
export class AllPendencyComponent {

  pendencyData: any = [];
  teamData: any = [];
  currentTeamId: any;
  showLoader: any = { 'loader': false, 'exportPDFLoader': false, 'exportCSVLoader': false };
  date: any;
  clientName: string = '';
  showFilteredDropdown: any = { 'officeName': false };
  isFilterAllSelected: any = { 'officeName': false };
  filteredOfficeName: any = [];
  filteredItems: any = [];
  tabSwitch: any = { 'withoutDos': true, 'withDos': false };
  isSorted: any = {};

  totalCount: any = [{ "teamName": "Internal_Audit", "count": 0, "teamId": 3 }, { "teamName": "Aging", "count": 0, "teamId": 4 }, { "teamName": "Posting", "count": 0, "teamId": 5 }, { "teamName": "Quality", "count": 0, "teamId": 6 }, { "teamName": "Billing", "count": 0, "teamId": 7 }];

  constructor(private _service: ApplicationServiceService, private title: Title) {
    title.setTitle(Utils.defaultTitle + "Pendency - Other Teams")
  }
  ngOnInit(): void {
    this.teamData = [{ "teamName": "Internal_Audit", "teamId": 3 }, { "teamName": "Aging", "teamId": 4 }, { "teamName": "Posting", "teamId": 5 }, { "teamName": "Quality", "teamId": 6 }, { "teamName": "Billing", "teamId": 7 }];
    this.getAllPendencyDetails();
    this.currentTeamId = localStorage.getItem("selected_teamId");
    this.clientName = localStorage.getItem("selected_clientName");
  }
  getAllPendencyDetails() {
    this.showLoader.loader = true;
    this._service.fetchAllPendency((res: any) => {
      if (res.status == 200) {
        this.showLoader.loader = false;
        this.pendencyData = res.data.onlyOffice;
        this.total(this.pendencyData);
        this.fetchOfficeByUuid();
        this.filterOfficeName();

      }
    })

  }



  total(data: any) {
    if (data) {
      data.forEach((e: any) => {
        this.teamData.forEach((team: any) => {
          if (team.teamId != this.currentTeamId) {
            this.totalCount.find((ele: any) => {
              if (ele.teamName == team.teamName) {
                ele.count = ele.count + e.counts1[team.teamName.toUpperCase()];
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
      pdf.text(`Pendency - Other Teams-${this.clientName}`, 2, 6);
      pdf.addImage(content, "PNG", 0, 15, width, height);
      this.date = new Date();
      this.date = `${this.date.getMonth() + 1}/${this.date.getDate()}/${this.date.getFullYear()}`;
      pdf.save(`${localStorage.getItem("selected_clientName")}_All_Pendency_${this.date}`);
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
          'Aging': e.counts1['AGING'],
          'Posting': e.counts1['POSTING'],
          'Quality': e.counts1['QUALITY'],
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
          'InternalAudit': e.dates1['INTERNAL_AUDIT'] ? e.dates1['INTERNAL_AUDIT'] : "-",
          'Aging': e.dates1['AGING'] ? e.dates1['AGING'] : "-",
          'Posting': e.dates1['POSTING'] ? e.dates1['POSTING'] : "-",
          'Quality': e.dates1['QUALITY'] ? e.dates1['QUALITY'] : "-",
          'Billing': e.dates1['BILLING'] ? e.dates1['BILLING'] : "-",
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
  fetchOfficeByUuid() {
    this._service.fetchOfficeByUuid((res: any) => {
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
    if (!this.pendencyData) return;
    this.filteredOfficeName = data;
    this.filteredOfficeName.forEach((e: any) => {
      this.pendencyData.forEach((ele: any) => {
        e['checked'] = true;
      })
    });

    this.sortFiltereData(this.filteredOfficeName);
  }

  switchTab(tab: any) {
    if (!this.pendencyData) return;
    tab.withoutDos = !tab.withoutDos;
    tab.withDos = !tab.withDos;
    this.filteredItems = this.pendencyData;
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

}
