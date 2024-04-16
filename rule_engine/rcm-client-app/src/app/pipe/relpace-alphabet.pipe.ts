import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relpaceAlphabet'
})
export class RelpaceAlphabetPipe implements PipeTransform {

  transform(value: any):any {

    if (value) {
      value = value.split(",");
      let commaSeperatedValue:any = [];
      for (let i = 0; i < value.length; i++) {
        if (!value[i].includes("-NO-DATA-")) { 
          commaSeperatedValue.push(value[i]);
        }
      }
      return commaSeperatedValue;
    }

    return null;
  }

}
