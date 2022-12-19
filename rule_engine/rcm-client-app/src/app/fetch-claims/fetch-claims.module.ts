import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FetchClaimsRoutingModule } from './fetch-claims-routing.module';
import { FetchClaimsComponent } from './fetch-claims/fetch-claims.component';


@NgModule({
  declarations: [
    FetchClaimsComponent
  ],
  imports: [
    CommonModule,
    FetchClaimsRoutingModule
  ]
})
export class FetchClaimsModule { 
  constructor() { 
    
  }
}
