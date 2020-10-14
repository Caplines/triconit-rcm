import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Office} from "../../model/model.office";
import {ApplicationService} from "../../services/application.service";
import {Router,ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-ext',
  templateUrl: './dynamic_ivf.component.html',
  styleUrls: ['./dynamic_ivf.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DynamicIVFComponent implements OnInit {
    //items = [
     //   {name: "Apple", type: "fruit"},
     //   {name: "Carrot", type: "vegetable"},
      //  {name: "Orange", type: "fruit"}];

	section1:any={};
    section2:any={};
    //section3:any=[];

    items =[
    	{
    	 section:1,
    	 name: "main section1",
    	 columns: 5,
    	 
    	 data:[
    		{name: "Office<br/> Name", type: "dpf",value:"Lavaca", dt:false,idn:1,idv:21,ch:1,cv:1,rc:1},
    		{name: "Patient<br/> Name", type: "fill",value:"Test", dt:false,idn:2,idv:22,ch:1,cv:1,rc:1},
    		{name: "Insurance<br/> Name", type: "fill",value:"abc", dt:false,idn:3,idv:23,ch:1,cv:1,rc:1},
    		{name: "Tax ID", type: "fill",value:"113", dt:false,idn:4,idv:24,ch:1,cv:1,rc:1},
    		{name: "Member<br/>SSN:", type: "fill",value:"525864917", dt:true,idn:5,idv:25,ch:1,cv:1,rc:2},
    		{name: "Policy <br/>Holder:", type: "fill",value:"Alejandro Vera", dt:true,idn:6,idv:26,ch:1,cv:1,rc:1},
    		{name: "Patient<br/> DOB:", type: "fill",value:"2005-09-12", dt:true,idn:7,idv:27,ch:1,cv:1,rc:1}
    	     ]
    	
        },
        
        {
       	 section:2,
       	 name: "Policy/Plan Information",
       	 columns: 5,
       	 data:[
       		{name: "D0120", type: "fill",value:" 121", dt:false,idn:1,idv:26,ch:1,cv:1,rc:1},
       		{name: "D0ABC", type: "fill",value:"Test", dt:false,idn:1,idv:27,ch:1,cv:1,rc:1},
       		{name: "D0ABCDED", type: "fill",value:"abc", dt:false,idn:1,idv:28,ch:1,cv:1,rc:1},
       		{name: "D1ABCDED", type: "fill",value:"113", dt:false,idn:1,idv:29,ch:1,cv:1,rc:1},
       		
       		{name: "D2120", type: "fill",value:" 121", dt:false,idn:1,idv:30,ch:1,cv:1,rc:1},
       		{name: "D2ABC", type: "fill",value:"Test", dt:false,idn:1,idv:31,ch:1,cv:1,rc:1},
       		{name: "D2ABCDED", type: "fill",value:"abc", dt:false,idn:1,idv:32,ch:1,cv:1,rc:1},
       		{name: "D2ABCDED", type: "fill",value:"113", dt:false,idn:1,idv:33,ch:1,cv:1,rc:1}
       		 ]
       	
           }
    ];
    //https://github.com/valor-software/ng2-dragula
 droppedItems:any=[];
offices:any;
constructor( private router: Router) { }
	 // this.offices =this.route.snapshot.data['offs'].data;
	 // this.offices.push({"name":"All OFFICES","uuid":"All offices"});
  ngOnInit() {
	 let th=this;
	 th.items.forEach((i,v)=>{
		 console.log(i);
		 if (i.section==1){
			 th.section1=[];
			 console.log(i);
			 let c=i.columns*2;//10
			 let ct=1;
			 let chtemp=0;
			 let cvtemp=0;
			 
			 let rtemp=0;
			 let fa=[];
			 let pa=[];
			 i.data.forEach((j,v)=>{
				 chtemp= chtemp+ j.ch;//1
				 cvtemp= cvtemp+ j.cv;//1
				 rtemp= rtemp+ j.rc;//1
				// debugger;
				 if (chtemp+cvtemp<=c){
					 
					pa.push(j); 
				 }else{
					 fa.push(pa);
					 chtemp=cvtemp=0;
					 pa=[];
					 pa.push(j);
				 }
			     	 
			 });
			 if (pa.length>0) fa.push(pa);
			 th.section1.name=i.name;
			 th.section1.section=i.section;
			 th.section1.data=fa;
		 }
		 console.log(th.section1);
	 });
	 /*
	 th.items.forEach((i,v)=>{
		 console.log(i);
		 if (i.section==1){
			// th.section1=i.data;
			 th.createSectionData(i.data);
		 }else if (i.section==2){
			 th.section2=i.data;
		 }
		 
	 });
	 */
  }
      
onItemDrop(e: any) {
  // Get the dropped data here
	debugger;
	this.droppedItems.push(e.dragData);
	 this.items =this.arrayRemove(this.items,e.dragData);
}

onItemDrop2(e: any) {
	  // Get the dropped data here
	console.log(e);
	debugger;
	this.items.push(e.dragData[0]);
	  
	}

onItemDrop1(e: any) {
	  // Get the dropped data here
	
	  this.droppedItems.push(e.dragData);
	 
	}
  
  

arrayRemove(arr, value) {
	return arr.filter(function(ele){ return !(ele.name == value.name && ele.type == value.type ); });
 }


onItemDropSection1(e: any){
	console.log(e);
	console.log(e.nativeEvent.target.id);
	
	
	console.log(e.dragData);
	let temp1=[];
	//temp1.data=[];
	let index1=0; 
	let index11=0; 
	let index2=0; 
	let index22=0;
	let fd1=false;
	let fd2=false;
	
	this.section1.data.forEach((j,v)=>{
		j.forEach((j1,v1)=>{
			if (e.nativeEvent.target.id === j1.idn+''+j1.idv){
				index1=v1;
				index11=v;
				fd1=true;
			}
			if (e.dragData.idn+''+e.dragData.idv === j1.idn+''+j1.idv){
				index2=v1;
				index22=v;
				fd2=true;
			}
		});
		console.log(index1+"---"+index2);//2 //1
		console.log("-------------");//2 //1
		console.log(index11+"---"+index22+"--"+fd1+"---"+fd2);//2 //1
		if (index11==index22 && fd1 && fd2){
			[j[index1], j[index2]] = [j[index2], j[index1]];
			fd1=fd2=false;
		}
		temp1.push(j);
		console.log(j);
		/*j.forEach((j1,v1)=>{
			if (j1.idn+''+j1.idv==e.nativeEvent.target.id){
				
			}
		});*/
		 
	     	 
	 });
	this.section1.data=temp1;
	
	if (index11!=index22 && fd1 && fd2){
		
		
		[this.section1.data[index11][index1], this.section1.data[index22][index2]] = 
		[this.section1.data[index22][index2], this.section1.data[index11][index1]];
	}
	console.log();
	
}

createSectionData(d:any){
	
	let thtml="";
	let num=1;
	let tradded=true;
	d.forEach((i,v)=>{
		 
		if (num%2==1){
			thtml=thtml+"<tr class='sec-1-tr'>";
			tradded=false;
		}
		thtml=thtml+"<td class='sec-1-td'>";
		thtml=thtml+"<div style='display:inline-block;float:left' draggable (onDrop)='onItemDrop2($event)'> [dragData]='(k)' ";
		thtml=thtml+"<label for='comp-name' ><div style='display:inline-block'>"+i.name+"</div></label>";
		thtml=thtml+"<input type='text' name='comp-name' value='"+i.value+"'/>";
		thtml=thtml+"</div></td>";
		
		if (num%2==0){
			thtml=thtml+"</tr>";
			tradded=true;
		}
		console.log(thtml);
		 num++;
		
	});
	
	if (!tradded){
		thtml=thtml+"</tr>";
	}
	document.getElementById("section-1").innerHTML=thtml;	
   }


saveFinalData(){
	    
	 this.items=[];
	 this.items.push(this.section1);
	 this.items.push(this.section2);
	 //this.items.push();
	 //this.items.push();
	 console.log(this.items);
	 
 }

}
