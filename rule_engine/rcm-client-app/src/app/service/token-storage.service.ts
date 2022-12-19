import { Injectable } from '@angular/core';
import Utils from '../util/utils';
//const TOKEN_KEY = 'auth-token';
//const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  constructor() { }

  signOut(): void {
    Utils.resetLocalStorage();
   // window.localStorage.clear();
  }

  //public saveToken(token: string): void {
    //Utils.setLocalStorage();
  //  window.localStorage.removeItem(TOKEN_KEY);
   // window.localStorage.setItem(TOKEN_KEY, token);
  //}

 // public getToken(): string | null {
   // return window.localStorage.getItem(TOKEN_KEY);
 // }

  public saveData(user: any,token :any): void {
    Utils.resetLocalStorage();
     Utils.setLocalStorage(user,token);
   
  }

 /* public getUser(): any {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }*/
}