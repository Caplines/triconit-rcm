import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'claimIdPipe'
})
export class ClaimIdPipe implements PipeTransform {
    transform(value:string) {
    if (!value) {
        return "";
    }
    return value.replace('_P',"").replace('_S',"");
        
    }
}