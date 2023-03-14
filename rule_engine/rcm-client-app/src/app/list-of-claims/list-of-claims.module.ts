import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PipesModule } from '../pipe/pipe-module'; 
import { HeaderComponent } from '../header/header-component/header.component';
import { ListOfClaimsComponent } from './list-of-claims/list-of-claims.component';
import { ListOfClaimsRoutingModule } from './list-of-claims-routing.module';
import { FormsModule } from '@angular/forms';
@NgModule({
  declarations: [
    ListOfClaimsComponent,
    
  ],
  imports: [
    CommonModule,
    ListOfClaimsRoutingModule,
    PipesModule,
    HeaderComponent,
    FormsModule
  ]
})
export class ListOfClaimsModule { 
  constructor() { 
    
  }
}
