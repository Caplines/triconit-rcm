import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TreatmentPlanComponent } from '../treatment-plan.component';

const routes: Routes = [{path:'',component:TreatmentPlanComponent}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TreatmentPlanRoutingModule { }
