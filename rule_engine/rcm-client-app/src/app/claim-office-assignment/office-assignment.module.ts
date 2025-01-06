import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MultiSelectDropdownModule } from '../shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';
import { ClaimAssignmentRoutingModule } from './office-assignment-routing.module';
import { OfficeAssignmentComponent } from './office-assignment/office-assignment.component';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header-component/header.component';

@NgModule({
  declarations: [
    OfficeAssignmentComponent
  ],
  imports: [
    CommonModule,
    ClaimAssignmentRoutingModule,
    FormsModule,
    HeaderComponent,
    MultiSelectDropdownModule
  ]
})
export class ClaimAssignmentModule {
  constructor() {

  }
}
