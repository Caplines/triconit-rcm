import {  Headers, RequestOptions} from '@angular/http';
export class AuthHeader {
    
    static createAuthHeader():RequestOptions{
        let headers = new Headers();
        //headers.append('Accept', 'application/json')
        // creating base64 encoded String from user name and password
        //var base64Credential: string = btoa( user.email+ ':' + user.password);
        headers.append("Authorization", "Bearer " + localStorage.getItem("token"));
        return new RequestOptions({ headers: headers });
    }
    
    static getToken():string{
        return localStorage.getItem("token");
    }
    
}