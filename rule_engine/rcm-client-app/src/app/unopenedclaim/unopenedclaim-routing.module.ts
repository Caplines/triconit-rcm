import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { unopenedclaimComponent } from './unopenedclaim.component';

const routes: Routes = [
  {
    path: '',
    component: unopenedclaimComponent
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class unopenedclaimRoutingModule { }
