import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'resultTypeFilter'
    //,pure: false
})
export class ResultTypeFilterPipe implements PipeTransform {
    transform(items: any[], filter: any): any {
        if (!items || !filter) {
            return items;
        }
        if (filter=='All'){
        	return items;
        }
        
        // filter items array, items which match and return true will be
        // kept, false will be filtered out
        return items.filter(item => item.resultType.toLowerCase().indexOf(filter) !== -1);
    }
}