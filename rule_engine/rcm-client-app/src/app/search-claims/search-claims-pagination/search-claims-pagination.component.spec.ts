import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchClaimsPaginationComponent } from './search-claims-pagination.component';

describe('SearchClaimsPaginationComponent', () => {
  let component: SearchClaimsPaginationComponent;
  let fixture: ComponentFixture<SearchClaimsPaginationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchClaimsPaginationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchClaimsPaginationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
