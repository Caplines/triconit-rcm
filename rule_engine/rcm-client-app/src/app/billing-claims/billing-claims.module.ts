import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BillingClaimsRoutingModule } from './billing-claims-routing.module';
import { BillingClaimsComponent } from './billing-claims/billing-claims.component';
import { PipesModule } from '../pipe/pipe-module'; 
import { HeaderComponent } from '../header/header-component/header.component';
import { ApplicationServiceService } from '../service/application-service.service';
import { ClaimService } from '../service/claim.service';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    BillingClaimsComponent
  ],
  providers: [ApplicationServiceService,ClaimService],
  imports: [
    CommonModule,
    FormsModule,
    BillingClaimsRoutingModule,
    PipesModule,
    HeaderComponent
  ]
})
export class BillingClaimsModule { 
  constructor() { 
    
  }
}
