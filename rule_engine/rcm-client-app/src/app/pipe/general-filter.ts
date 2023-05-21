import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'generalFilter'
})
export class GeneralDataFilter implements PipeTransform {
    transform(items: any[], property: string, check: string, property2?: string, check2?: string): any {
        if (!items) {
            return items;
        }
        // filter items array, items which match and return true will be
        // kept, false will be filtered out
        if (property2 == undefined) {
            return items.filter(item => item[property] === check);
        } else {
            return items.filter(item => item[property] === check && item[property2] === check2);
        }

    }
}