import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ToolUpdateClaimsRoutingModule } from './tool-update-routing.module';
import { ToolUpdateComponent } from './tool-update/tool-update.component';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header-component/header.component';

@NgModule({
  declarations: [
    ToolUpdateComponent
  ],
  imports: [
    CommonModule,
    ToolUpdateClaimsRoutingModule,
    FormsModule ,
    HeaderComponent     
  ]
})
export class ToolUpdateModule { 
  constructor() { 
    
  }
}
