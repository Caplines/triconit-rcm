import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";
import {EnReportsModel} from "../../model/model.enreports";
import Utils from '../../util/utils';



@Component({
  selector: 'app-usersettings',
  templateUrl: './usersettings.component.html',
  styleUrls: ['./usersettings.css'],
  encapsulation: ViewEncapsulation.None
})

export class UserSettingsComponent {
	 userName: any;
	 showuserdata:boolean=false;
	 showLoading:boolean=false;
	 userData: any;
     isAdmin:boolean=false;

	 constructor(public applicationService: ApplicationService) {
		 this.isAdmin = Utils.checkAdmin();
	  }

      findUserByName(){
		  this.showLoading = true;
	      this.applicationService.findUserByUserName(this.userName,(result) =>{
		  if (result.status=='OK'){
			  this.showLoading = false;
				this.userData = result.data;
				this.showuserdata = true;
				if (this.isEmpty(this.userData)){
					alert("No user Found.");
					this.showLoading = false;
				}else{
					
				}
		 } else if (result.status=='BAD_REQUEST'){
			 alert('User does not Exists');
					this.showuserdata = false;
					this.showLoading = false;
			}
	});
	  
  }
  
  isEmpty(obj) {
	    for(var key in obj) {
	      if(obj.hasOwnProperty(key))
	        return false;
	    }
	    return true;
  }
  
  receiveChildrenEmitter(event) {
		if(event['action'] == "showuserdata" ) {
			this.showuserdata = event['value'];
			this.userName='';
			 this.showLoading = event['value'];
		}
  } 
  
}
