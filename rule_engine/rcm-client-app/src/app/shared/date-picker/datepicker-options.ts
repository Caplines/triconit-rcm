import { DatepickerOptions } from 'ng2-datepicker';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class PmlDatePicker {

    public DATE_PICKER_OPTIONS: DatepickerOptions = {
       // minYear: getYear(new Date()) - 30, // minimum available and selectable year
            // maxYear: getYear(new Date()) + 30, // maximum available and selectable year
            placeholder: 'Select Date', // placeholder in case date model is null | undefined, example: 'Please pick a date'
            //format: 'LLLL do yyyy', // date format to display in input
            format: "MM/dd/yyyy",
            //format: "yyyy-MM-dd HH:mm",
            formatTitle: 'LLLL yyyy',
            formatDays: 'EEEEE',
            firstCalendarDay: 0, // 0 - Sunday, 1 - Monday
            // locale: locale, // date-fns locale
            position: 'top',
            inputClass: 'pml_dp', // custom input CSS class to be applied
            //calendarClass: 'datepicker-default', // custom datepicker calendar CSS class to be applied
            scrollBarColor: '#dfe3e9', // in case you customize you theme, here you define scroll bar color
            // keyboardEvents: true // enable keyboard events
            // timePicker: false
    }
}
    
// export const DatePickerOptions: DatepickerOptions = {
//     // minYear: getYear(new Date()) - 30, // minimum available and selectable year
//     // maxYear: getYear(new Date()) + 30, // maximum available and selectable year
//     placeholder: '', // placeholder in case date model is null | undefined, example: 'Please pick a date'
//     //format: 'LLLL do yyyy', // date format to display in input
//     format: "dd/MM/yyyy",
//     //format: "yyyy-MM-dd HH:mm",
//     formatTitle: 'LLLL yyyy',
//     formatDays: 'EEEEE',
//     firstCalendarDay: 0, // 0 - Sunday, 1 - Monday
//     // locale: locale, // date-fns locale
//     position: 'top',
//     inputClass: '', // custom input CSS class to be applied
//     //calendarClass: 'datepicker-default', // custom datepicker calendar CSS class to be applied
//     scrollBarColor: '#dfe3e9', // in case you customize you theme, here you define scroll bar color
//     // keyboardEvents: true // enable keyboard events
//     // timePicker: false
// };