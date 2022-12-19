import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FetchClaimsComponent } from './fetch-claims/fetch-claims.component';
const routes: Routes = [
  { path: '', component: FetchClaimsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FetchClaimsRoutingModule { }
