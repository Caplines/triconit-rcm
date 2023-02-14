import { NgModule } from '@angular/core';
import { ClaimTypeFilter } from './claim-type-pipe';
import { ClaimIdPipe } from './claim-id-pipe';

@NgModule({
  imports: [
    // dep modules
  ],
  declarations: [ 
    ClaimTypeFilter,
    ClaimIdPipe
  ],
  exports: [
    ClaimTypeFilter,
    ClaimIdPipe
  ]
})
export class PipesModule {}
