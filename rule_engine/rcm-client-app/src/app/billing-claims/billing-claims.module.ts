import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BillingClaimsRoutingModule } from './billing-claims-routing.module';
import { BillingClaimsComponent } from './billing-claims.component';
import { FormsModule } from '@angular/forms';
import { ApplicationServiceService } from '../service/application-service.service';
import { ClaimService } from '../service/claim.service';
import { DownLoadService } from '../service/download.service';
import { PipesModule } from '../pipe/pipe-module';


@NgModule({
  declarations: [BillingClaimsComponent],
  imports: [
    CommonModule,
    BillingClaimsRoutingModule,
    FormsModule,
    PipesModule
  ],
  providers:[ApplicationServiceService,ClaimService, DownLoadService],
  exports:[BillingClaimsComponent]
})
export class BillingClaimsModule { }
