import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PipesModule } from '../pipe/pipe-module';
import { HeaderComponent } from '../header/header-component/header.component';
import {OtherTeamsWorkComponent } from './other-teams-work/other-teams-work.component';
import { OtherTeamsWorkComponentRoutingModule} from './other-teams-work-routing.module';
import { FormsModule } from '@angular/forms';
import { ApplicationServiceService } from '../service/application-service.service';
import { DownLoadService } from '../service/download.service';
@NgModule({
  declarations: [
    OtherTeamsWorkComponent,

  ],
  providers: [ApplicationServiceService, DownLoadService],
  imports: [
    CommonModule,
    OtherTeamsWorkComponentRoutingModule,
    PipesModule,
    HeaderComponent,
    FormsModule
  ]
})
export class OtherTeamsWorkModule {
  constructor() {

  }
}
