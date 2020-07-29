import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'resultTypeFilter'
    //,pure: false
})
export class ResultTypeFilterPipe implements PipeTransform {
    transform(items: any[], fil: any[]): any {
        if (!items || !fil) {
            return items;
        }
        let fl =  fil.find(x => x == "All");
        if (fl=='All'){
        	return items;
        }
        let x=items.filter(function(item){
     	   return fil.filter(function(fil1){
     	      return item.resultType.toLowerCase() == fil1;
     	   }).length != 0
     	});
        return x;
        
        // filter items array, items which match and return true will be
        // kept, false will be filtered out
        //return items.filter(item => item.resultType.toLowerCase().indexOf(fil) !== -1);
    }
}