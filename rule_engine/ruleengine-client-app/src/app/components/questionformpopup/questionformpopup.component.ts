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
  answerData:any = [];
  
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
	this.arrayOfKeys = Object.keys(this.questionFormData);
  }
  
  groupBy(arr, property) {
	  return arr.reduce(function(memo, x) {
		if (!memo[x[property]]) { memo[x[property]] = []; }
		memo[x[property]].push(x);
		return memo;
	  }, {});
  }
  
  saveChanges(result) {
	let index = this.answerData.findIndex(updateAns => updateAns.answerId == result.answerId);
	if(index != -1) {
		this.answerData[index].answer = result.answer;
	} else {
		this.answerData.push({'answerId': result.answerId, 'answer': result.answer});		
	}
  }
  
  closePopup() {
	this.emitToParent.emit({action: "showQuestionData", value: false});
  }

}
