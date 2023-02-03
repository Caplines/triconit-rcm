import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'claimTypeFilter'
})
export class ClaimTypeFilter implements PipeTransform {
    transform(value:string) {
    if (!value) {
        return false;
    }
    return value.endsWith('_P');
        
    }
}