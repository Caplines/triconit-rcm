import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClaimAssignmentRoutingModule } from './office-assignment-routing.module';
import { OfficeAssignmentComponent } from './office-assignment/office-assignment.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    OfficeAssignmentComponent
  ],
  imports: [
    CommonModule,
    ClaimAssignmentRoutingModule,
    FormsModule      
  ]
})
export class ClaimAssignmentModule { 
  constructor() { 
    
  }
}
