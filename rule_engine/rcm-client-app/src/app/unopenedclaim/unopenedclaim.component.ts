import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import Utils from '../util/utils';
import { ApplicationServiceService } from '../service/application-service.service';

interface UnOpenedClaim {
  id: number;
  createdDate: string;
  updatedDate: string;
  teamId: number;
  active: boolean;
  comments: string;
  officeName: string;
  clientName: string;
  claimId: string;
}

@Component({
  selector: 'app-unopenedclaim',
  templateUrl: './unopenedclaim.component.html',
  styleUrls: ['./unopenedclaim.component.scss']
})

export class unopenedclaimComponent implements OnInit {
  unOpenedClaimData: UnOpenedClaim[] = [];
  unOpenedclaimUuid: any = "";
  alertMsgClaimId: string = "";
  alertSavedMsg: string = "";
  multipleActiveRecordsMsg: string = "";
  isSaving: boolean = false;

  constructor(public appService: ApplicationServiceService, private title: Title) {
    title.setTitle(Utils.defaultTitle + "UnOpened Claims");
  }

  ngOnInit() { }

  searchClaim() {
    this.alertMsgClaimId = "";
    this.multipleActiveRecordsMsg = "";
    this.unOpenedClaimData = [];
    this.isSaving = false;
    this.appService.searchUnopenedClaim(this.unOpenedclaimUuid, (res: any) => {
      if (res.status == 200 && res.data) {
        this.unOpenedClaimData = res.data;

        const activeRecords = this.unOpenedClaimData.filter((claim: any) => claim.active);
        if (activeRecords.length > 1) {
          this.multipleActiveRecordsMsg = "Multiple active records found.";
        }

      }
      else if (res.data == null) {
        this.alertMsgClaimId = "Invalid claim Id";
        setTimeout(() => { this.alertMsgClaimId = ""; }, 2000);
      }
      else {
        this.alertMsgClaimId = "Failed to fetch data";
        setTimeout(() => { this.alertMsgClaimId = ""; }, 2000);
        console.error("Failed to fetch data", res.message);
      }
    })
  }

  saveSingleRow(id: number, active: boolean) {
    this.alertSavedMsg = "";
    this.isSaving = true;

    let params = {
      id: id,
      claimUuid: this.unOpenedclaimUuid,
      active: active
    };

    this.appService.updateUnopenedClaim(params, (res: any) => {
      if (res.status == 200) {
        this.alertSavedMsg = res.data;
        setTimeout(() => { this.alertSavedMsg = ""; }, 2000);
      }
      else {
        this.alertSavedMsg = res.data;
        setTimeout(() => { this.alertSavedMsg = ""; }, 2000);
      }
    })
  }

}
