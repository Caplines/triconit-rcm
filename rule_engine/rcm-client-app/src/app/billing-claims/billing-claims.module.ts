import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BillingClaimsRoutingModule } from './billing-claims-routing.module';
import { BillingClaimsComponent } from './billing-claims/billing-claims.component';
import { PipesModule } from '../pipe/pipe-module'; 
@NgModule({
  declarations: [
    BillingClaimsComponent
  ],
  imports: [
    CommonModule,
    BillingClaimsRoutingModule,
    PipesModule
  ]
})
export class BillingClaimsModule { 
  constructor() { 
    
  }
}
