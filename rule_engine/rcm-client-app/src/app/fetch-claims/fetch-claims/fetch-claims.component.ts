import { Component, OnInit } from '@angular/core';
import { ApplicationServiceService  } from '../../service/application-service.service';


@Component({
  selector: 'app-fetch-claims',
  templateUrl: './fetch-claims.component.html',
  styleUrls: ['./fetch-claims.component.scss']
})
export class FetchClaimsComponent implements OnInit {

  constructor(private appService: ApplicationServiceService) { }

  ngOnInit(): void {

    this.fetchClaims();
  }

  fetchClaims(){

    // this.appService.fetchClaimData((res:any)=>{
    //   if (res.status=== 200){

    //   }else{
    //     //ERROR
    //   }
      
    // });
  }

  

}
