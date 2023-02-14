import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BillingClaimsComponent } from './billing-claims/billing-claims.component';
const routes: Routes = [
  { path: '', component: BillingClaimsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BillingClaimsRoutingModule { }
