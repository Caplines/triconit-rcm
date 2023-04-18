import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TreatmentPlanRoutingModule } from './treatment-plan-routing.module';
import { TreatmentPlanComponent } from '../treatment-plan.component';


@NgModule({
  declarations: [TreatmentPlanComponent],
  imports: [
    CommonModule,
    TreatmentPlanRoutingModule
  ],
  exports:[TreatmentPlanComponent]
})
export class TreatmentPlanModule { }
