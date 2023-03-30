import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MultiSelectDropdownComponent } from '../multi-select-dropdown.component';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [MultiSelectDropdownComponent],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports:[MultiSelectDropdownComponent]
})
export class MultiSelectDropdownModule { }
