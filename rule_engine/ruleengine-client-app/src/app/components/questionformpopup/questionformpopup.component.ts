import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {AccountService} from "../../services/account.service";

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
  
  constructor(public accountService: AccountService) { }

  ngOnInit() {
	let answers = this.groupBy(this.questionData.questionAnswer, 'questionId');
	console.log(this.questionData.dataHeader);
	console.log(answers);
	this.questionData.dataHeader.forEach((question, index)=> {
		question['questionAnswers'] = [];
		console.log(answers[index+1]);
		if (answers[index+1]){
		answers[index+1].forEach((answer) => {
			question['questionAnswers'].push(answer);
		});
		}
	
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
	console.log(result);
	if(index != -1) {
		this.answerData[index].answer = result.answer;
	} else {
		this.answerData.push({'answerId': result.answerId, 'answer': result.answer,'questionId':result.questionId});		
	}
	console.log(this.answerData);
	this.accountService.saveUserInput(this.answerData,  (result) => { 
		this.emitToParent.emit({action: "showLoading", value: false});
		if (result.status=='OK' && result.data){
			this.emitToParent.emit({action: "showIvfData", value: true});
			
		} else {
				this.emitToParent.emit({action: "showIvfData", value: false});
		}
	});
	
  }
  
  
  
  closePopup() {
	this.emitToParent.emit({action: "showQuestionData", value: false});
  }
  
  saveRadioChanges(result, value) {
	result.answer = value;
	this.saveChanges(result);
  }

}
