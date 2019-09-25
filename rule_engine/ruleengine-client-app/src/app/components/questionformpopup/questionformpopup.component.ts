import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {AccountService} from "../../services/account.service";

@Component({
  selector: 'app-questionformpopup',
  templateUrl: './questionformpopup.component.html',
  styleUrls: ['./questionformpopup.component.css']
})
export class QuestionformPopupComponent implements OnInit {
	
  @Input() questionData:any;
  @Input() showValidation:any;
  @Output() emitToParent = new EventEmitter<any>();
  arrayOfKeys:any;
  questionFormData:any;
  answerData:any = [];
  showQuestionSaveLoading: boolean = false;
  myError= {'PC':false};
  keyString="";
  constructor(public accountService: AccountService) { }

  ngOnInit() {
	  console.log(this.questionData.questionAnswer);
	  //at least one Question will  be there 
	  let x=this.questionData.questionAnswer[0];
	  
	  this.keyString=x.patId+"-%%-"+x.tpId+"-%%-"+x.officeId;
	   
	let answers = this.groupBy(this.questionData.questionAnswer, 'questionId');
	this.questionData.dataHeader.forEach((question, index)=> {
		question['questionAnswers'] = [];
		if (answers[question.id]){
			answers[question.id].forEach((answer) => {
				question['questionAnswers'].push(answer);
				if(question.questionType == 'C_B_A') {
					this.splitAnswer(question['questionAnswers']);
				}
			});
		}	
	});
	this.questionFormData = this.groupBy(this.questionData.dataHeader, 'ruleName');
	this.arrayOfKeys = Object.keys(this.questionFormData);
	
	//console.log(this.questionFormData);
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
	//console.log(result);
	if(index != -1) {
		this.answerData[index].answer = result.answer;
	} else {
		this.answerData.push({'answerId': result.answerId, 'answer': result.answer,'questionId':result.questionId});		
	}
			
	
  }
  
  //Consent Form Requirements No
  //Major Service Form Requirements 'NO'
 markMandatoryAnswer(){
	 let ths=this;
	 ths.myError.PC=false;
	  
	  let pass=false;
	  //console.log(ths.answerData);
	  //debugger;
	  let pc=ths.providerChangeAnsCheck();
	  //console.log(pc);
	  let xRayN=ths.ansCommonCheck(5,'narx_','X-Rays/Narratives/Perio Requirements','X-Rays/Narratives/Perio Requirements missing.');
	  let perA=ths.ansCommonCheck(22,'prex_','Pre-Authorization Requirements','Pre-Authorization Requirements missing.');
	  
	  //console.log("xRayN",xRayN);
	  if (pc.pass && xRayN.pass && perA.pass) pass=true;
	  else {
		  alert(pc.mess+xRayN.mess+ perA.mess);
	  }
	  //Issue when no such question is there pass will be false need this
	  return true;
  }
 
  saveFormData() {
	//  console.log(999);
	if (this.markMandatoryAnswer()){  
		
		this.showQuestionSaveLoading = true;
		this.answerData.push({'answerId': -1000, 'answer': this.keyString,'questionId':-1000});
		this.accountService.saveUserInput(this.answerData,  (result) => {
			this.showQuestionSaveLoading = false;	
			this.answerData = [];
			//From IVF Screen Condition
			if (this.showValidation=="1"){
				this.emitToParent.emit({action: "showValidation", value: true});
			}
	
		});	
	}
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
  
  providerChangeAnsCheck(){
	  let ths=this;
	  let pass=true;
	  ths.answerData.forEach(function (value) {
		  //console.log(value.answer);
		  let an=value.answer;
		  if (an){
			  if (value.questionId){
				  //PC
				  if( value.questionId== 16 && value.answer == 'true undefined'){
				 // alert('Please Enter Reference No.');
				  ths.myError.PC=true;
				  pass=false;
			  }
			  }
		  }
		});
	  
	  if (!ths.myError.PC && this.questionFormData['Provider Change']){
		  let value=ths.questionFormData['Provider Change'];
		  value.forEach(function (value2) {
		  if( value2.id== 16 ){
			  value2.questionAnswers.forEach(function (value3) {
				  //console.log(value3);
				  if ((typeof(value3.answer2)=='undefined' && value3.answer1==true) ||
					  (value3.answer2=='' && value3.answer1==true)){
					  //alert('Please Enter Reference No.');
					  ths.myError.PC=true;
					  pass=false;
				  }
			  });
			  
		  }
		  });
	  }
	  
	  return pass?{'pass':pass,'mess':''}:{'pass':pass,'mess':'Please Enter Reference No.\n'};

  }
  
 ansCommonCheck(qId,boxName,name,mess){
      let ths=this;
	  let pass=true;
	  let ckOne=false;

	  let value=ths.questionFormData[name];
	  //debugger;
	  if (value){
		  value.forEach(function (value2) {
		  if( value2.id== qId ){
			  value2.questionAnswers.forEach(function (value3) {
				  document.getElementById(boxName+value3.answerId).classList.remove("ref_error");
				  
			  });
			  
		  }
		  });
	  
	  }
	  ths.answerData.forEach(function (value) {
		  //console.log(value.answer);
		  let an=value.answer;
		  if (an){
			  if (value.questionId){
				  //PC
				  if( value.questionId== qId && value.answer && value.answer.trim() == ''){
					  document.getElementById(boxName+value.answerId).classList.add("ref_error");	  
				 // alert('Please Enter Reference No.');
				  ckOne=true;
				  pass=false;
			  }
			  }
		  }
		});
	  
	  if (!ckOne && this.questionFormData[name]){
		  value=this.questionFormData[name];
		  if (value){
		  value.forEach(function (value2) {
		  if( value2.id== qId ){
			  value2.questionAnswers.forEach(function (value3) {
				  //console.log(value3);
				  //console.log("aaaaaaa",":"+value3.answer+":"+":"+boxName+value3.answerId);
				  
				  if ((typeof(value3.answer)=='undefined' ) ||   (value3.answer=='')){
					  document.getElementById(boxName+value3.answerId).classList.add("ref_error");
					  //alert('Please Enter Reference No.');
					  pass=false;
				  }
			  });
			  
		  }
		  });
	   }
	  }
	  
	  return pass?{'pass':pass,'mess':''}:{'pass':pass,'mess':mess+'\n'};

  }

}
