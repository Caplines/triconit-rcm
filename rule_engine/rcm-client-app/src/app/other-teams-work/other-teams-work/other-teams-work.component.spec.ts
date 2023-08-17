import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OtherTeamsWorkComponent } from './other-teams-work.component

describe('OtherTeamsWorkComponent', () => {
  let component: OtherTeamsWorkComponent;
  let fixture: ComponentFixture<OtherTeamsWorkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OtherTeamsWorkComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OtherTeamsWorkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
