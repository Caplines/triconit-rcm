import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserStatusComponent } from '../users-status.component';

const routes: Routes = [
  { path: '', component: UserStatusComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserStatusRoutingModule { }
