import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SearchClaimsComponent } from './search-claims.component';

const routes: Routes = [
  {
    path:'',
    component:SearchClaimsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SearchClaimsRoutingModule { }
