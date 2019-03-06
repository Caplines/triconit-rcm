import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-enreports',
  templateUrl: './enreportspopup.component.html',
  styleUrls: ['./enreportspopup.css'],
  encapsulation: ViewEncapsulation.None
})
export class EnReportspopupComponent implements OnInit {
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
 
  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All offices"});
  }

  ngOnInit() {
  }
  
  
}
