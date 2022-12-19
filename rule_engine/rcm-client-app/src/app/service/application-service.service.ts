import { Injectable } from '@angular/core';
import { BaseService } from './base-service.service';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class ApplicationServiceService extends BaseService {

  constructor(http: HttpClient) {
    super(http);
  }

  fetchClaimData(callback:any){

    this.getData({},"/api/fetch-claims",callback);

  }
}
