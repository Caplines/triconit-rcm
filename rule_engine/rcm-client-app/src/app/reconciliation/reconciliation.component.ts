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
  reconcilltationRequestModel: ReconcilltationRequestModel = {
    officeUuid: 'Select'
  }

  loader: boolean = false;
  officeData: any = [];
  toggleLinks: boolean = false;
  datesDiff: number = 1000000;

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

  isValidForm(): boolean {
    if (!this.reconcilltationRequestModel.officeUuid || !this.reconcilltationRequestModel.startDate ||
      !this.reconcilltationRequestModel.endDate || this.calculateDateDiff() > this.datesDiff)
      return false;
    else
      return true;
  }

  calculateDateDiff(): number {
    const startDate = new Date(this.reconcilltationRequestModel.startDate).getTime();
    const endDate = new Date(this.reconcilltationRequestModel.endDate).getTime();
    const diffInDays = Math.floor((endDate - startDate) / (1000 * 60 * 60 * 24));
    if (diffInDays < 0) {
      return this.datesDiff + 1;
    }
    return diffInDays;
  }

  getReconcileData() {
    this.loader = true;

    this._service.fetchReconcileData(this.reconcilltationRequestModel, (res: any) => {
      if (res.status === 200) {
        console.log(res.data);
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
    });

  }

}
