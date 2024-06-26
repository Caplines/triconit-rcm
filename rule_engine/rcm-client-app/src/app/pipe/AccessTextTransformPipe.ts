import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'accessTextTransform'
})
export class AccessTextTransformPipe implements PipeTransform {
    transform(value: string): string {
        if (value === 'Read') {
            return 'Read Only';
        } else if (value === 'Edit') {
            return 'Edit Access';
        }
        return value;
    }
}
