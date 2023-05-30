import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IssueClaimComponent } from './issue-claims.component';

describe('IssueClaimComponent', () => {
  let component: IssueClaimComponent;
  let fixture: ComponentFixture<IssueClaimComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IssueClaimComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IssueClaimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
