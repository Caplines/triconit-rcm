import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { BaseService } from 'src/app/service/base-service.service';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';


@Component({
  selector: 'app-login-component',
  templateUrl: './login-component.component.html',
    styleUrls: ['./login-component.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null,
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];
  
  forgotPassObj={
    email:'',
    showForgotPasswordBox:false,
    showResetEmailMsg:false
  }


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService,private router:Router,private _baseService: BaseService) { }

  ngOnInit(): void {
    /* if (this.tokenStorage.getToken()) {
       this.isLoggedIn = true;
       this.roles = this.tokenStorage.getUser().roles;
     }*/
  }

  onSubmit(): void {
    const { username, password } = this.form;
    this.authService.login(username, password, (result: any) => {
      console.log(result);
      if (result.status == 200) {
        this.tokenStorage.saveData(result.data, result.data.token);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        //this.roles = this.tokenStorage.getUser().roles;
        this.reloadPage();
      } else {
        this.errorMessage = result.error;
        this.isLoginFailed = true;
      }

    });
    /*
    this.authService.login(username, password).subscribe({
      next: data => {
       //this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveData(data,data.accessToken);                                                                    

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.reloadPage();
      },
      error: err => {
        console.log(err);
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    });
    */
  }

  reloadPage(): void {
    window.location.reload();
  }

  showForgotPassBox(){
    this.forgotPassObj.showForgotPasswordBox=true;
    this.errorMessage = '';
  }

  forgotPass(){
    if(this.forgotPassObj.email.includes("@")){
      this._baseService.forgotPassword({"email":this.forgotPassObj.email},(callback:any)=>{
        if(callback.status && callback.result.message === 'Password has been updated' ){
          console.log(callback)
          this.forgotPassObj.showResetEmailMsg = true;
          this.forgotPassObj.showForgotPasswordBox = false;
          this.forgotPassObj.email= '';
          this.errorMessage='';
        } else { 
          this.errorMessage = callback.result.message;
          this.forgotPassObj.email= '';
        }
      })
    } else{
      this.errorMessage="Please Enter the Email";
      this.forgotPassObj.email= '';
    }
  }
}
