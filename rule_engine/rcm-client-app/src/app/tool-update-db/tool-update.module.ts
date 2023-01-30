import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ToolUpdateClaimsRoutingModule } from './tool-update-routing.module';
import { ToolUpdateComponent } from './tool-update/tool-update.component';


@NgModule({
  declarations: [
    ToolUpdateComponent
  ],
  imports: [
    CommonModule,
    ToolUpdateClaimsRoutingModule
  ]
})
export class ToolUpdateModule { 
  constructor() { 
    
  }
}
