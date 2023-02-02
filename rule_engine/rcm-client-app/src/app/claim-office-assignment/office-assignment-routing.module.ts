import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OfficeAssignmentComponent } from './office-assignment/office-assignment.component';
const routes: Routes = [
  { path: '', component: OfficeAssignmentComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClaimAssignmentRoutingModule{ }
