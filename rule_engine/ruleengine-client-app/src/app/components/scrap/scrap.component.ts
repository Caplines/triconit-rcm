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
  scrap: ScrapModel = new ScrapModel();
  errorMessage: string;
  offices:any;
  userName: any;
  userType: any;
  
  showLoading: boolean = false;
  showScrapForm: boolean = false;
  showSrcapPopup: boolean = false;
  showParam:any = { Roster : false, MCNADENTAL : false, MCNADENTALUI : false, Dentaq : false, DentaqUI : false };

dateOptions: DatepickerOptions = {
		displayFormat: 'MM/DD/YYYY',
		placeholder: 'Click to select a DOB'
	  };
  typeMap: any = { 'Roster': "1", 'MCNADENTAL': "2", 'MCNADENTALUI': "3", 'Dentaq': "4", 'DentaqUI': "5" };

  constructor(public accountService: AccountService, public router: Router,private route: ActivatedRoute, private datePipe: DatePipe) {
	  this.offices =this.route.snapshot.data['offs'].data;
	  this.offices.push({"name":"All OFFICES","uuid":"All"});
  }

  ngOnInit() {
		this.dateOptions.barTitleIfEmpty = this.datePipe.transform(new Date(), 'MMMM y');
	  }
  
  scrapParam(value) {
	  //this.scrap = new ScrapModel();
	  let filter = this.showParam;
	  Object.keys(filter).forEach(function(key, result) {
		  if(key == value) {
		  	filter[key] = true;
		  } else {
		  	filter[key] = false;
		  }
		  return filter;
		 });
	  this.showScrapForm = true;
	  this.scrap.scrapType = this.typeMap[value];
  }
  
  runScrapReport() {
	  console.log(this.scrap);
	  this.showLoading = true;
	  setTimeout(()=>{
		  this.showLoading = false;
	  }, 2000);
  }
  
}
