import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClaimsComponent } from './claims/claims.component';
import { ClaimsRoutingModule } from './claims-routing.module';
import { BillingClaimsModule } from '../billing-claims/billing-claims.module';

@NgModule({
  declarations: [
    ClaimsComponent
  ],
  imports: [
    CommonModule,
    ClaimsRoutingModule,
    BillingClaimsModule
  ]
})
export class ClaimsModule {
  constructor() {

  }
}
