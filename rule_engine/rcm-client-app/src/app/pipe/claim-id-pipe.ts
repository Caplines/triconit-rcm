import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'claimIdPipe'
})
export class ClaimIdPipe implements PipeTransform {
    transform(value: string) {
        if (!value) {
            return "";
        }
        if (value.indexOf("_arc_") > 0) value = value.split("_arc_")[1];
        return value.replace('_P', "").replace('_S', "");

    }
}