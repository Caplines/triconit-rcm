import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {User} from "../../model/model.user";
import {Office} from "../../model/model.office";
import {AccountService} from "../../services/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class RegisterComponent implements OnInit {
  user: User = new User();
  office: Office = new Office();
  errorMessage: string;
  offices:any;
  passwordAgain:any;
  list: any[] = [{"name" : "Treatment","val"  : 1},
	              {"name" : "Claim","val"  : 2}];
  constructor(public accountService: AccountService, public router: Router) {
  }

  ngOnInit() {
      this.accountService.getOffices((result) => {
        //console.log(result);
        this.offices=result;
      });
       
  }

  register() {
	  console.log(this.user);
	  if(this.user.password==this.passwordAgain && this.user.officeId) {
		this.accountService.createAccount(this.user,(data) => {
		    console.log(data);
		    if(data.message == "User Created Successfully") {
                this.router.navigate(['/login']);
            } else {
                this.errorMessage = data.message;
            }	    
		}
		)//
	  }
  
 }
}
