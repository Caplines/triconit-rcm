import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ToolUpdateClaimsRoutingModule } from './tool-update-routing.module';
import { ToolUpdateComponent } from './tool-update/tool-update.component';
import { FormsModule } from '@angular/forms';
import { HeaderModule } from '../header/header.module';

@NgModule({
  declarations: [
    ToolUpdateComponent
  ],
  imports: [
    CommonModule,
    ToolUpdateClaimsRoutingModule,
    FormsModule ,
    HeaderModule     
  ]
})
export class ToolUpdateModule { 
  constructor() { 
    
  }
}
