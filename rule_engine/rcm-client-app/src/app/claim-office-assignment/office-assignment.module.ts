import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClaimAssignmentRoutingModule } from './office-assignment-routing.module';
import { OfficeAssignmentComponent } from './office-assignment/office-assignment.component';
import { FormsModule } from '@angular/forms';
import { HeaderModule } from '../header/header.module';

@NgModule({
  declarations: [
    OfficeAssignmentComponent
  ],
  imports: [
    CommonModule,
    ClaimAssignmentRoutingModule,
    FormsModule ,
    HeaderModule
  ]
})
export class ClaimAssignmentModule { 
  constructor() { 
    
  }
}
