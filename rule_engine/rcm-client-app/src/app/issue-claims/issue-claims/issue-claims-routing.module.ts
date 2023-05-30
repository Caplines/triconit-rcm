import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IssueClaimComponent } from '../issue-claims.component';

const routes: Routes = [{path:'',component:IssueClaimComponent}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IssueClaimRoutingModule { }
