import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllPendencyComponent } from './all-pendency.component';

describe('AllPendencyComponent', () => {
  let component: AllPendencyComponent;
  let fixture: ComponentFixture<AllPendencyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllPendencyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllPendencyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
