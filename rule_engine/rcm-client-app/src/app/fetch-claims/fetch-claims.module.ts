import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FetchClaimsRoutingModule } from './fetch-claims-routing.module';
import { FetchClaimsComponent } from './fetch-claims/fetch-claims.component';
import { PipesModule } from '../pipe/pipe-module'; 
import { HeaderModule } from '../header/header.module';

@NgModule({
  declarations: [
    FetchClaimsComponent,
    
  ],
  imports: [
    CommonModule,
    FetchClaimsRoutingModule,
    PipesModule,
    HeaderModule
  ]
})
export class FetchClaimsModule { 
  constructor() { 
    
  }
}
