import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import { AppConstants } from '../constants/app.constants';
import { FormsModule } from '@angular/forms';
import Utils from '../util/utils';
@Component({
  selector: 'app-manage-section',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './manage-section.component.html',
  styleUrls: ['./manage-section.component.scss'],
})
export class ManageSectionComponent {

  clients: any = [];
  selectedClient: string = '';
  sectionList: any = [];
  teamsData: any = this.constants.teamData;
  manageSectionData: any = [];
  loader: boolean = false;
  userManageSectionData: any = [];
  alert: any = { 'showAlertPopup': false, 'alertMsg': '', 'isError': false };

  // itemsPerPage: number = 1;
  // currentPage: number = 1;
  // filteredValue: any = [];
  // filterItems: any = [];
  @Input() inputConfig: any;

  copiedManageSectionData: any = [];

  constructor(private _service: ApplicationServiceService, public constants: AppConstants, private title: Title) {
    title.setTitle(Utils.defaultTitle + "Manage Section");
  }

  ngOnInit() {
    if (this.inputConfig && this.inputConfig?.isEditSection) {
      this.fetchUserIdSectionData();
    } else {
      this.fetchSectionData();
    }
    this.fetchClientNames();
  }

  fetchSectionData() {
    this.loader = true;
    this._service.fetchManageSectionData((res: any) => {
      if (res) {
        this.manageSectionData = res.data;
        this.copiedManageSectionData = JSON.parse(JSON.stringify(this.manageSectionData));
        this.loader = false;

      }
    })
  }


  updateSelectedData(event: any, clientUuid: string, teamId: number, sectionId: number, viewAccess: any, editAccess: any, type: any) {
    const clientIndex = this.manageSectionData.findIndex((client: any) => client.clientUuid === clientUuid);
    if (clientIndex === -1) {
      this.manageSectionData.push({
        clientUuid,
        teamsWithSections: [{
          teamId,
          sectionData: [{ sectionId, viewAccess, editAccess: editAccess }]
        }]
      });
    } else {
      const teamIndex = this.manageSectionData[clientIndex].teamsWithSections.findIndex((team: any) => team.teamId === teamId);

      if (teamIndex === -1) {
        this.manageSectionData[clientIndex].teamsWithSections.push({
          teamId,
          sectionData: [{ sectionId, viewAccess, editAccess: editAccess }]
        });
      } else {
        const sectionIndex = this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData.findIndex((section: any) => section.sectionId === sectionId);
        if (sectionIndex === -1) {
          this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData.push({ sectionId, viewAccess, editAccess: editAccess });
        } else {
          this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
          this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = editAccess;

          if (viewAccess && type === 'view') {
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
          } else if (!viewAccess && type === 'view') {
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = false;
          }
          else if (editAccess && type === 'edit') {
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = true;
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = editAccess;
          }
          else if (!editAccess && type === 'edit') {
            // this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
            this.manageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = false;
          }
        }
      }
    }


  }


  editSelectedData(event: any, clientUuid: string, teamId: number, sectionId: number, viewAccess: any, editAccess: any, type: any) {
    const clientIndex = this.userManageSectionData.findIndex((client: any) => client.clientUuid === clientUuid);
    if (clientIndex === -1) {
      this.userManageSectionData.push({
        clientUuid,
        userUuid: this.inputConfig?.uuid,
        teamsWithSections: [{
          teamId,
          sectionData: [{ sectionId, viewAccess, editAccess: event.target.checked }]
        }]
      });
    } else {
      const teamIndex = this.userManageSectionData[clientIndex].teamsWithSections.findIndex((team: any) => team.teamId === teamId);

      if (teamIndex === -1) {
        this.userManageSectionData[clientIndex].teamsWithSections.push({
          teamId,
          sectionData: [{ sectionId, viewAccess, editAccess: event.target.checked }]
        });
      } else {
        const sectionIndex = this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData.findIndex((section: any) => section.sectionId === sectionId);
        if (sectionIndex === -1) {
          this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData.push({ sectionId, viewAccess, editAccess: event.target.checked });
        } else {
          this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
          this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = event.target.checked;

          if (viewAccess && type === 'view') {
            this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = viewAccess;
          } else if (!viewAccess && type === 'view') {
            this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = false;
          }
          else if (event.target.checked && type === 'edit') {
            this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].viewAccess = true;
            this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = event.target.checked;
          }
          else if (!event.target.checked && type === 'edit') {
            this.userManageSectionData[clientIndex].teamsWithSections[teamIndex].sectionData[sectionIndex].editAccess = false;
          }
        }
      }
    }
  }


  saveManageSecData(idx: any) {
    let params: any = this.manageSectionData[idx];
    this._service.saveManageSectionData([params], (res: any) => {
      if (res) {
        this.showAlertPopup(res);
      }
    })

  }

  saveManageUserSecData(idx: any) {
    let params: any = this.userManageSectionData[idx];
    this._service.saveUserManageSectionData([params], (res: any) => {
      if (res) {
        this.showAlertPopup(res);
      }
    })

  }

  trackByFn(index: any, item: any) {
    return item.id;
  }

  // displayPage(page: number) {
  //   this.loader = true;
  //   this.currentPage = page;
  //   let startIndex = (page - 1) * this.itemsPerPage;
  //   let endIndex = startIndex + this.itemsPerPage;
  //   this.filteredValue = this.manageSectionData.slice(startIndex, endIndex);
  //   this.filterItems = this.filteredValue;
  //   this.loader = false;
  //   window.scrollTo(0, 0);
  // }

  showAlertPopup(res: any) {
    this.alert.showAlertPopup = true;
    setTimeout(() => { this.alert.showAlertPopup = false; }, 2000);
    res.status == 400 ? this.alert.isError = true : this.alert.isError = false;
    this.alert.alertMsg = res.message ? res.message : "Success";
    setTimeout(() => {
      scrollTo(0, 0);
    }, 0);
  }

  fetchUserIdSectionData() {
    this.loader = true;
    this._service.fetchUserManageSectionData(this.inputConfig.uuid, (res: any) => {
      if (res) {
        this.manageSectionData = res.data;
        this.userManageSectionData = JSON.parse(JSON.stringify(this.manageSectionData));
        this.loader = false;
      }
    })
  }

  fetchClientNames() {
    this._service.getClientsName((res: any) => {
      if (res) {
        console.log(res);
        this.clients = res.data;
      }
    })
  }

  selectClient(event: any) {
    if (this.inputConfig && this.inputConfig?.isEditSection) {
      this.manageSectionData = this.userManageSectionData.filter((e: any) => e.clientName == event.target.value);
    } else {
      this.manageSectionData = this.copiedManageSectionData.filter((e: any) => e.clientName == event.target.value);
    }
  }
}
