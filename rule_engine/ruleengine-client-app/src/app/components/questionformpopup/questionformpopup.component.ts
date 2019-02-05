import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-questionformpopup',
  templateUrl: './questionformpopup.component.html',
  styleUrls: ['./questionformpopup.component.css']
})
export class QuestionformPopupComponent implements OnInit {
	
  @Input() questionData:any;
  @Output() emitToParent = new EventEmitter<any>();
  arrayOfKeys:any;
  questionFormData:any;
  
  constructor() { }

  ngOnInit() {
	let answers = this.groupBy(this.questionData.questionAnswer, 'questionId');
	this.questionData.dataHeader.forEach((question, index)=> {
		question['questionAnswers'] = [];
		answers[index+1].forEach((answer) => {
			question['questionAnswers'].push(answer);
		});
	});
	this.questionFormData = this.groupBy(this.questionData.dataHeader, 'ruleName');
	console.log(this.questionFormData);
	this.arrayOfKeys = Object.keys(this.questionFormData);
  }
  
  groupBy(arr, property) {
	  return arr.reduce(function(memo, x) {
		if (!memo[x[property]]) { memo[x[property]] = []; }
		memo[x[property]].push(x);
		return memo;
	  }, {});
  }
  
  saveChanges(answer) {
	console.log(answer);
  }
  
  closePopup() {
	this.emitToParent.emit({action: "showQuestionData", value: false});
  }

}
