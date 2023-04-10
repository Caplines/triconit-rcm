import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AllPendencyComponent } from './all-pendency.component';

const routes: Routes = [
  { path:'',component: AllPendencyComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AllPendencyRoutingModule { }
