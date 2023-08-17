import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OtherTeamsWorkComponent } from './other-teams-work/other-teams-work.component'
const routes: Routes = [
  { path: '', component: OtherTeamsWorkComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OtherTeamsWorkComponentRoutingModule { }
