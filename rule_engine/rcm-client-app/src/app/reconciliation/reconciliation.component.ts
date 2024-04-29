import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import { ReconcilltationRequestModel } from '../models/reconcillation-request-model';
@Component({
  selector: 'app-reconciliation',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reconciliation.component.html',
  styleUrls: ['./reconciliation.component.scss']
})
export class ReconciliationComponent {

  private title = inject(Title);

  reconcilData: any = [];
  reconcilltationRequestModel: ReconcilltationRequestModel = {}
  loader: boolean = false;
  officeData: any = [];
  toggleLinks: boolean = false;
  constructor(private _service: ApplicationServiceService) {
    this.title.setTitle("RCM TOOL - Reconciliation");
  }

  ngOnInit() {
    this.fetchOffice();

  }

  fetchOffice() {
    this._service.fetchOfficeData((res: any) => {
      if (res) {
        console.log(res);
        this.officeData = res.data;
      }
    })
  }

  selectOffice(event: any) {
    console.log(event);
    this.reconcilltationRequestModel.officeUuid = event.target.value;
  }

  getReconcileData() {
    this.loader = true;

    this._service.fetchReconcileData(this.reconcilltationRequestModel, (res: any) => {
      if (res.status === 200) {
        console.log(res.data);
      }
    });
    setTimeout(() => {
      this.reconcilData = [
        {
          title: "Primary & Secondary Unbilled",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com', 'htttps://google.com']
        },
        {
          title: "Primary Open & Secondary Unbilled",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com']
        },
        {
          title: "Primary Closed & Secondary Unbilled",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com']
        },
        {
          title: "Primary Closed & Secondary Open",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com']
        },
        {
          title: "Primary & Secondary Closed",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com']
        },
      ];
      this.loader = false;
    }, 0
    )
  }

}
