import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'messagetypePipe'
})
export class MessageTypePipe implements PipeTransform {
    transform(value:number) {
    if(value == 1){
        return "Fail";
    }else if(value == 2){
        return "Pass";
    }else if(value == 3){
        return "Alert";
    }
    return "Notset";   
    }
}