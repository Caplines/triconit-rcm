import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttachFileComponent } from './attach-file.component';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [AttachFileComponent],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports:[
    AttachFileComponent
  ]
})
export class AttachFileModule { }
