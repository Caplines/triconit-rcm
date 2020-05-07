import {Component, OnInit, ViewEncapsulation, Input, Output, EventEmitter} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
import {ScrapModel}  from "../../model/model.scrap";


@Component({
  selector: 'app-scrappopup',
  templateUrl: './scrappopup.component.html',
  styleUrls: ['./scrappopup.css'],
  encapsulation: ViewEncapsulation.None
})
export class ScrapPopupComponent implements OnInit {
	@Input() scrapm:ScrapModel;
	@Output() emitToParent = new EventEmitter<any>();
	@Input() showScrapPopup:boolean;
	@Input() scrapType:number;
	@Input() offName:string;
	scrapData: any;
	filterType:string='All';
	arrayOfKeys:any;
	linkData:string="";
	
	constructor(public applicationService: ApplicationService) { }

	ngOnInit() {
		this.scrapDataSite();
		}
	
	scrapDataSite() {
	    let ths=this;	
		this.scrapData=[];
		this.arrayOfKeys=[];
		this.applicationService.scrapSite(this.scrapm, 'scrapsite', (result) => { 
			this.emitToParent.emit({action: "showLoading", value: false});
			if (result.status=='OK' && result.data){
				//console.log(result.data);
				this.scrapData = result.data;
				// console.log(this.ivfmData);
				// this.arrayOfKeys = Object.keys(this.ivfmData);
				if (!this.isEmpty(this.scrapData)){this.arrayOfKeys = Object.keys(this.scrapData);
				if (this.arrayOfKeys[0]=='Office Not Set up'){
					alert(this.arrayOfKeys[0]);
					this.emitToParent.emit({action: "showScrapPopup", value: false});
				}
				if (this.arrayOfKeys[0]=='Done'){
					
					this.emitToParent.emit({action: "showScrapPopup", value: true});
					
				}
                if (this.arrayOfKeys[0].indexOf("Scrapping Initiated - ")>-1){
				    let links=this.arrayOfKeys[0].split("Scrapping Initiated - ")[1];
				    let link=links.split("-_-_-");
				    this.linkData="https://docs.google.com/spreadsheets/d/"+link[0]+"/edit#gid="+link[1];
					this.emitToParent.emit({action: "showScrapPopup", value: true});
					
				}
                if (this.arrayOfKeys[0]=='Already Running'){
					alert("Some one is already running the Scraping.. ");
					this.emitToParent.emit({action: "showScrapPopup", value: false});
					
				}
			  }
			 	// this.emitToParent.emit({action: "showScrapPopup", value:
				// true});
				if (this.isEmpty(this.scrapData)){
					alert("No Data found in sheet.");
					this.emitToParent.emit({action: "showScrapPopup", value: false});
					}
				
			} else {
				if (!result.data){
					alert("No Data found in sheet.");
				}
			
				this.emitToParent.emit({action: "showScrapPopup", value: false});
			}
		});
	}
	
	closePopup() {
		this.emitToParent.emit({action: "showScrapPopup", value: false});
	}
	

	
	
	isEmpty(obj) {
	  for(var key in obj) {
		if(obj.hasOwnProperty(key))
		  return false;
		}
	  return true;
	}
	
	
	

}
