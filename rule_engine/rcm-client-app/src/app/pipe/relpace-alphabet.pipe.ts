import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'relpaceAlphabet'
})
export class RelpaceAlphabetPipe implements PipeTransform {

  transform(value: any): any {
    let commaSeperatedValue: any = [];
    if (value) {
      value = value.split(",");
      for (let i = 0; i < value.length; i++) {
        let trimmedValue: any = value[i].trim();
        if (trimmedValue !== "-NO-DATA-") {
          commaSeperatedValue.push(trimmedValue);
        }
      }
      // Remove duplicates values
      commaSeperatedValue = Array.from(new Set(commaSeperatedValue));
      return commaSeperatedValue;
    }
    return null;
  }

}
