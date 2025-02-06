import { Component, Input, OnInit, EventEmitter, Output, ViewEncapsulation, ViewChild, AfterViewInit } from '@angular/core';
import { PmlDatePicker } from '../datepicker-options';

@Component({
  selector: 'app-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DatePickerComponent implements OnInit, AfterViewInit {

  @Output() emitToParent = new EventEmitter();

  @Input() inputDate: string;

  @Input() parentModel: any;

  @ViewChild('datePicker') datePickerRef: any;

  constructor(public pmlDatePicker: PmlDatePicker) { }

  ngOnInit() { }

  ngAfterViewInit() { }

  onSelectDate(date: any) {
    this.emitToParent.emit({ action: 'changeDatePicker', value: date, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
  }

  removeReadonly(evn: any) {
    let ths = this;
    setTimeout(() => {
      if (ths.datePickerRef.isOpened) {
        ths.datePickerRef.toggle();
      }
    }, 500);

    let rd = evn.currentTarget.childNodes[0].childNodes[0].getAttribute("readonly");
    if (rd == "") {
      evn.currentTarget.childNodes[0].childNodes[0].removeAttribute('readonly');
      evn.currentTarget.childNodes[0].childNodes[0].setAttribute('maxlength', '10');
      let dpElement = evn.currentTarget.childNodes[0].childNodes[0];
      evn.currentTarget.childNodes[0].childNodes[0].addEventListener('blur', function () {
        if (!ths.datePickerRef.isOpened) {
          ths.validateDate(dpElement);
        }
      });
    };
  }

  resetDate(){
    this.emitToParent.emit({ action: 'changeDatePicker', value: null, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
    this.datePickerRef.displayValue = null;
    this.datePickerRef.innerValue = new Date();
  }

  validateDate(event: any) {
    let value = event.value;
    if (value === '' || value == null) {
      this.emitToParent.emit({ action: 'changeDatePicker', value: null, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
      this.datePickerRef.displayValue = null;
      this.datePickerRef.innerValue = new Date();
      return;
    }
    
    var pattern = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/

    if (!pattern.test(value)) {
      this.emitToParent.emit({ action: 'changeDatePicker', value: null, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
      this.datePickerRef.displayValue = null;
      this.datePickerRef.innerValue = new Date();
      return;
    }

    var parts = value.split("/");
    var day = parseInt(parts[1], 10);
    var month = parseInt(parts[0], 10);
    var year = parseInt(parts[2], 10);

    if (year < 1000 || year > 3000 || month == 0 || month > 12 || day < 1) {
      this.emitToParent.emit({ action: 'changeDatePicker', value: null, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
      this.datePickerRef.displayValue = null;
      this.datePickerRef.innerValue = new Date();
      return;
    }

    var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

    if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
      monthLength[1] = 29;

    if (day > monthLength[month - 1]) {
      this.emitToParent.emit({ action: 'changeDatePicker', value: null, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
      this.datePickerRef.displayValue = null;
      this.datePickerRef.innerValue = new Date();
      return;
    }

    this.emitToParent.emit({ action: 'changeDatePicker', value: value, model: this.datePickerRef.elementRef.nativeElement.parentNode.id });
    setTimeout(() => {
      if (this.datePickerRef.isOpened) {
        this.datePickerRef.toggle();
      }
    }, 2000);
  }

}
