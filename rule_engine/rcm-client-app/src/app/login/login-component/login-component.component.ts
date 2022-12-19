import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../service/auth-service.service';
import { TokenStorageService } from '../../service/token-storage.service';


@Component({
  selector: 'app-login-component',
  templateUrl: './login-component.component.html',
  styleUrls: ['./login-component.component.scss']
})
export class LoginComponent implements OnInit {
  form: any = {
    username: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];


  //https://www.bezkoder.com/angular-13-jwt-auth/

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService) { }

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
}
