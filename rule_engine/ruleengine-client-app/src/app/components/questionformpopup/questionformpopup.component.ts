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
  showLoading: boolean = false;
  
  constructor(public accountService: AccountService) { }

  ngOnInit() {
	let answers = this.groupBy(this.questionData.questionAnswer, 'questionId');
	this.questionData.dataHeader.forEach((question, index)=> {
		question['questionAnswers'] = [];
		if (answers[index+1]){
			answers[index+1].forEach((answer) => {
				question['questionAnswers'].push(answer);
				if(question.questionType == 'C_B_A') {
					this.splitAnswer(question['questionAnswers']);
				}
			});
		}	
	});
	this.questionFormData = this.groupBy(this.questionData.dataHeader, 'ruleName');
	this.arrayOfKeys = Object.keys(this.questionFormData);
	
	console.log(this.questionFormData);
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
  }
  
  saveFormData() {
	this.showLoading = true;
	console.log(this.answerData);
	this.accountService.saveUserInput(this.answerData,  (result) => {
		this.showLoading = false;	
		console.log(result);
		this.answerData = [];
	});	
  }
  
  closePopup() {
	this.emitToParent.emit({action: "showQuestionData", value: false});
  }
  
  saveRadioChanges(result, value) {
	result.answer = value;
	this.saveChanges(result);
  }
  
  splitAnswer(value) {
	if(value[0].answer == "") {
		value[0]['answer1'] = "";
		value[0]['answer2'] = "";
	} else {	
		value[0]['answer1'] = (value[0].answer.split(" ")[0] == 'true');
		value[0]['answer2'] = value[0].answer.split(" ")[1];
	}		
  }
  
  mergeAnswer(result) {
	result.answer = result.answer1 + " " + result.answer2;
	this.saveChanges(result);
  }

}
