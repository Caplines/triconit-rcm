import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePickerComponent } from './date-picker.component';
import { DatepickerModule } from 'ng2-datepicker';

@NgModule({
  imports: [
    CommonModule,
    DatepickerModule
    
  ],
  declarations: [DatePickerComponent],
  exports: [DatePickerComponent]
})
export class RcmDatePickerModule { }
