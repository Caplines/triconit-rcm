import {  HttpHeaders , HttpRequest} from '@angular/common/http';
export class AuthHeader {
    
    static createAuthHeader():any{
        let headers = new HttpHeaders();
        //headers.append('Accept', 'application/json')
        // creating base64 encoded String from user name and password
        //var base64Credential: string = btoa( user.email+ ':' + user.password);
        headers.append("Authorization", "Bearer " + localStorage.getItem("token"));
        const requestOptions = { headers: headers };
        return  { headers: headers };
    }
    
    static getToken():string{
        let ls:any =localStorage;
        return ls.getItem("token");
    }
    
}