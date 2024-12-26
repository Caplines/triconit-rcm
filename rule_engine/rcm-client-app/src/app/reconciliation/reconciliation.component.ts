import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';
import { ReconcilltationRequestModel } from '../models/reconcillation-request-model';
import { ReconcilltationResponseModel } from '../models/reconcillation-request-model';
import { DatePipe } from '@angular/common';
import { PmlDatePicker } from '../shared/date-picker/datepicker-options';
import { DateRangeModule } from '../shared/date-range-picker/date-range-picker.module';
@Component({
  selector: 'app-reconciliation',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DateRangeModule],
  templateUrl: './reconciliation.component.html',
  styleUrls: ['./reconciliation.component.scss']
})
export class ReconciliationComponent {

  private title = inject(Title);
  
  reconcileResponseData: ReconcilltationResponseModel[] = [];
  reconcilltationRequestModel: ReconcilltationRequestModel = {
    officeUuid: '',
    startDate: null,
    endDate: null
  }

  loader: boolean = false;
  officeData: any = [];
  toggleLinks: boolean = false;
  datesDiff: number = 1000;
  toggleState: { [key: string]: boolean } = {};
  currentToggle: string = '';

  constructor(private _service: ApplicationServiceService, public datepipe: DatePipe, public pmlDatePicker: PmlDatePicker) {
    this.title.setTitle("RCM Tool - Reconciliation");
  }

  ngOnInit() {
    this.fetchOffice();

  }

  fetchOffice() {
    this._service.fetchOfficeData((res: any) => {
      if (res) {
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
    if (this.currentToggle === id && this.toggleState[id]) {
      this.toggleState[id] = false;
      this.currentToggle = '';
    } else {
      if (this.currentToggle) {
        this.toggleState[this.currentToggle] = false;
      }
      this.toggleState[id] = !this.toggleState[id];
      if (this.toggleState[id]) {
        this.currentToggle = id;
      } else {
        this.currentToggle = '';
      }
    }
  }

  getReconcileData() {
    this.loader = true;
    this.toggleState = {};
    this.reconcileResponseData = [];
    this._service.fetchReconcileData({
      ...this.reconcilltationRequestModel,
      startDate: this.transformDate(this.reconcilltationRequestModel.startDate),
      endDate: this.transformDate(this.reconcilltationRequestModel.endDate)
    }, (res: any) => {
      if (res.status === 200) {
        setTimeout(() => {
          this.reconcileResponseData = res.data;
          this.loader = false;
        }, 0)
      }
    });
  }

  transformDate(date: Date) {
    return this.datepipe.transform(date, 'yyyy-MM-dd');
  }

  receiveChildrenEvent(event: any) {
    if (event['action'] == 'getSelectedDateRange') {
      if (event.value?.startDate == 'Invalid date') {
        this.reconcilltationRequestModel.startDate = null;
        this.reconcilltationRequestModel.endDate = null;
      }
      else {
        this.reconcilltationRequestModel.startDate = new Date(event.value.startDate);
        this.reconcilltationRequestModel.endDate = new Date(event.value.endDate);
      }
    }
    this.calculateDateDiff();
  }
}
