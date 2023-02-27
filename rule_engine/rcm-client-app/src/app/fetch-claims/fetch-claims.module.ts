import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FetchClaimsRoutingModule } from './fetch-claims-routing.module';
import { FetchClaimsComponent } from './fetch-claims/fetch-claims.component';
import { PipesModule } from '../pipe/pipe-module'; 
import { HeaderComponent } from '../header/header-component/header.component';

@NgModule({
  declarations: [
    FetchClaimsComponent,
    
  ],
  imports: [
    CommonModule,
    FetchClaimsRoutingModule,
    PipesModule,
    HeaderComponent
  ]
})
export class FetchClaimsModule { 
  constructor() { 
    
  }
}
