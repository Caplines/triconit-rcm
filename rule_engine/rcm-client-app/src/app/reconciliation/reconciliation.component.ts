import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import { ReconcilltationRequestModel } from '../models/reconcillation-request-model';
import { ReconcilltationResponseModel } from '../models/reconcillation-request-model';
@Component({
  selector: 'app-reconciliation',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reconciliation.component.html',
  styleUrls: ['./reconciliation.component.scss']
})
export class ReconciliationComponent {

  private title = inject(Title);
  
  reconcileResponseData: ReconcilltationResponseModel[] = [];
  reconcilltationRequestModel: ReconcilltationRequestModel = {
    officeUuid: 'Select'
  }

  loader: boolean = false;
  officeData: any = [];
  toggleLinks: boolean = false;
  datesDiff: number = 1000000;
  toggleState: { [key: string]: boolean } = {};

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

  toggleText(id: string) {
    this.toggleState[id] = !this.toggleState[id];
  }

  getReconcileData() {
    this.loader = true;
    this.toggleState = {};
    this.reconcileResponseData = [];
    this._service.fetchReconcileData(this.reconcilltationRequestModel, (res: any) => {
      if (res.status === 200) {
        console.log(res.data);
        setTimeout(() => {
          this.reconcileResponseData = res.data;
          this.loader = false;
        }, 0)
      }
    });
  }
}
