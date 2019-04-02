import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router,ActivatedRoute} from "@angular/router";
import {ScrapModel} from "../../model/model.scrap";
import {DatepickerOptions} from 'ng2-datepicker';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-scrap',
  templateUrl: './scrap.component.html',
  styleUrls: ['./scrap.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class ScrapComponent implements OnInit {
  scrapmodel: ScrapModel = new ScrapModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  
  showLoading: boolean = false;
  showScrapForm: boolean = false;
  showSrcapPopup: boolean = false;

  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All"});
  }

  ngOnInit() {
  }
  
  
}
