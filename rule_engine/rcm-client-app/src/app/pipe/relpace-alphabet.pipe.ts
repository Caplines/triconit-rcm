import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relpaceAlphabet'
})
export class RelpaceAlphabetPipe implements PipeTransform {

  transform(value: any):any {
    let commaSeperatedValue:any = [];
    if (value) {
      value = value.split(",");
    
      for (let i = 0; i < value.length; i++) {
        if (!value[i].includes("-NO-DATA-")) { 
          commaSeperatedValue.push(value[i]);
        }
      }
      return commaSeperatedValue;
    }

    // Remove duplicates using a Set
     commaSeperatedValue = Array.from(new Set(commaSeperatedValue));
    return null;
  }

}
