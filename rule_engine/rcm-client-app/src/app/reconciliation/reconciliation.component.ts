import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { ApplicationServiceService } from '../service/application-service.service';

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
  loader: boolean = false;
  officeData: any = [];

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

  getReconcileData() {
    this.loader = true;
    setTimeout(() => {
      this.reconcilData = [
        {
          title: "Primary & Secondary Unbilled",
          claimsES: 100,
          claimsRCM: 200,
          discrepancies: ['htttps://google.com','htttps://google.com']
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
