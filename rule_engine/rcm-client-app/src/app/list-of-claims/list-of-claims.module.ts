import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PipesModule } from '../pipe/pipe-module';
import { HeaderComponent } from '../header/header-component/header.component';
import { ListOfClaimsComponent } from './list-of-claims/list-of-claims.component';
import { ListOfClaimsRoutingModule } from './list-of-claims-routing.module';
import { FormsModule } from '@angular/forms';
import { ApplicationServiceService } from '../service/application-service.service';
import { DownLoadService } from '../service/download.service';
@NgModule({
  declarations: [
    ListOfClaimsComponent,

  ],
  providers: [ApplicationServiceService, DownLoadService],
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
