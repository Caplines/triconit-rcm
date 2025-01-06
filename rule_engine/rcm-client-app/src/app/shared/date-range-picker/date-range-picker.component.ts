import { Component, EventEmitter, Output, Input } from '@angular/core';
declare var $: any;
@Component({
  selector: 'app-date-range',
  templateUrl: './date-range-picker.component.html',
  styleUrls: ['./date-range-picker.component.scss'],
})
export class DateRangePickerComponent {
  value: any;
  dateField: any;
  @Input() isSearchClaimsPage: boolean;
  @Output() emitToParent: any = new EventEmitter();
  constructor() { }

  ngOnInit() {
    this.updateRangeConfig();
    if (!this.isSearchClaimsPage) {
      this.emitDefaultDate();
    }
  }

  ngAfterViewInit() {
    this.dateField = document.querySelector(".daterangepicker");
    this.dateField.style.display = "none";
  }

  openDatePicker(e: any) {
    if (this.dateField) {
      this.dateField.style.display = "block";
    }

  }

  ngOnDestroy() {
    const dateRangePicker = document.querySelector('.daterangepicker');
    if (dateRangePicker) {
      dateRangePicker.remove();
    }
  }

  updateRangeConfig() {
    let options: any = {
      autoUpdateInput: true
    };
    options.ranges = {
      'Clear': [null, null],
      'Today': [new Date(), new Date()],
      'Yesterday': [new Date(new Date().getTime() - 24 * 60 * 60 * 1000), new Date(new Date().getTime() - 24 * 60 * 60 * 1000)],
      'Last Week': [new Date(new Date().getTime() - 6 * 24 * 60 * 60 * 1000), new Date()],
      'Last 30 Days': [new Date(new Date().getTime() - 29 * 24 * 60 * 60 * 1000), new Date()],
      'Month to Date': [new Date(new Date().getFullYear(), new Date().getMonth(), 1), new Date()],
      'This Month': [new Date(new Date().getFullYear(), new Date().getMonth(), 1), new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0)],
      'Last Month': [new Date(new Date().getFullYear(), new Date().getMonth() - 1, 1), new Date(new Date().getFullYear(), new Date().getMonth(), 0)]
    };

    if (!this.isSearchClaimsPage) {
      options.startDate = options.ranges['Month to Date'][0];
      options.endDate = options.ranges['Month to Date'][1];
    }

    $('#config-demo').daterangepicker(options, (start: any, end: any, label: any) => {
      //TRICON if condition
      // if (start == null || isNaN(start._i)) {
      //   this.emitToParent.emit({ action: 'getSelectedDateRange', value: { startDate: null, endDate: null } });
      // } else {
        this.emitToParent.emit({ action: 'getSelectedDateRange', value: { startDate: start.format("YYYY-MM-DD"), endDate: end.format("YYYY-MM-DD") } });
      // }

    }).click();
  }

  clearField(){
    let inputDateEl:any = document.querySelector("#config-demo");
    inputDateEl.value = '';
  }

  emitDefaultDate(){
    let sDate = new Date(Date.UTC(new Date().getFullYear(), new Date().getMonth(), 1));
    let eDate = new Date();

    this.emitToParent.emit({
      action: 'getSelectedDateRange',
      value: {
        startDate: sDate.toISOString().split('T')[0],
        endDate: eDate.toISOString().split('T')[0]
      }
    });
  }

}
