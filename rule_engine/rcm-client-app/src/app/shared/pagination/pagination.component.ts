import { Component, EventEmitter, Input, Output,ElementRef, ViewChildren, QueryList } from '@angular/core';

@Component({
  selector: 'pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent{
  currentValue:number = 1;
  @Input() paginationPages:any = [];
  @Output() emitToParent = new EventEmitter();
  @ViewChildren('link') linkElememtRef: QueryList<ElementRef>;
  
  @Input() currentPage: number;
  @Input() totalPages: number;
  @Input() isSearchClaimsPage:boolean=false;
  

  ngOnInit(){
    this.getPagesArray();
  }

  getPagesArray(): number[] {
    const pagesArray: number[] = [];
    for (let i = 1; i <= this.totalPages; i++) {
      pagesArray.push(i);
    }
    return pagesArray;
  }

  
  navigateToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.emitToParent.emit({action:'changePage',value:page});
    }
  }


  activeLink(e:any){
    this.linkElememtRef.forEach((e:any)=>{
      e.nativeElement.classList.remove("active")
    });
     e.target.classList.add("active");
     this.currentValue = e.target.innerText;
     this.emitToParent.emit({action:'pageNum',value: this.currentValue - 1});
  }

  prevBtn(){
    if(this.currentValue > 1){
      let link = document.getElementsByClassName('page-link');
      this.linkElememtRef.forEach((e:any)=>{
        e.nativeElement.classList.remove("active")
      });
      this.currentValue--;
      link[this.currentValue-1]?.classList.add("active");
      this.emitToParent.emit({action:'pageNum',value: this.currentValue - 1});
    }
  }
  
  nxtBtn(){
    if(this.currentValue < this.paginationPages.length){
      let link = document.getElementsByClassName('page-link');
      this.linkElememtRef.forEach((e:any)=>{
        e.nativeElement.classList.remove("active")
      });
      this.currentValue++;
      link[this.currentValue-1]?.classList.add("active");
      this.emitToParent.emit({action:'pageNum',value: this.currentValue - 1});
    }
  }
}
