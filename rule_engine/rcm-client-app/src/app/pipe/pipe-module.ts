import { NgModule } from '@angular/core';
import { ClaimTypeFilter } from './claim-type-pipe';
import { ClaimSubmissionTeamPipe } from './claim-submission-team-pipe';
import { ClaimIdPipe } from './claim-id-pipe';
import { MessageTypePipe } from './message-type.pipe';
import { GeneralDataFilter } from './general-filter';
import {RelpaceAlphabetPipe} from "./relpace-alphabet.pipe";
import { AccessTextTransformPipe } from './AccessTextTransformPipe';
@NgModule({
  imports: [
    // dep modules
  ],
  declarations: [
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter,
    ClaimSubmissionTeamPipe,
    RelpaceAlphabetPipe,
    AccessTextTransformPipe
  ],
  exports: [
    ClaimTypeFilter,
    ClaimIdPipe,
    MessageTypePipe,
    GeneralDataFilter,
    ClaimSubmissionTeamPipe,
    RelpaceAlphabetPipe,
    AccessTextTransformPipe

  ]
})
export class PipesModule { }
