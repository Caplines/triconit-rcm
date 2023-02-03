import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FetchClaimsRoutingModule } from './fetch-claims-routing.module';
import { FetchClaimsComponent } from './fetch-claims/fetch-claims.component';
import { ClaimTypeFilter } from '../pipe/claim-type-pipe';

@NgModule({
  declarations: [
    FetchClaimsComponent,
    ClaimTypeFilter
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
