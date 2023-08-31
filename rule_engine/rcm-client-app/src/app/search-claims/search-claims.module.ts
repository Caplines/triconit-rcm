import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SearchClaimsRoutingModule } from './search-claims-routing.module';
import { SearchClaimsComponent } from './search-claims.component';
import { MultiSelectDropdownModule } from '../shared/multi-select-dropdown/multi-select-dropdown/multi-select-dropdown.module';


@NgModule({
  declarations: [SearchClaimsComponent],
  imports: [
    CommonModule,
    SearchClaimsRoutingModule,
    MultiSelectDropdownModule
  ],
  exports:[SearchClaimsComponent]
})
export class SearchClaimsModule { }
