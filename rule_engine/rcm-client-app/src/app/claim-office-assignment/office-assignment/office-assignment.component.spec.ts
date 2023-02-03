import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAssignmentComponent } from './office-assignment.component';

describe('FetchClaimsComponent', () => {
  let component: OfficeAssignmentComponent;
  let fixture: ComponentFixture<OfficeAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAssignmentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
