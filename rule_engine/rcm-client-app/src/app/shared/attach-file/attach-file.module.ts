import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttachFileComponent } from './attach-file.component';



@NgModule({
  declarations: [AttachFileComponent],
  imports: [
    CommonModule
  ],
  exports:[
    AttachFileComponent
  ]
})
export class AttachFileModule { }
