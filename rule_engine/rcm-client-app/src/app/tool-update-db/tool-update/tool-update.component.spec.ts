import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolUpdateComponent } from './tool-update.component';

describe('FetchClaimsComponent', () => {
  let component: ToolUpdateComponent;
  let fixture: ComponentFixture<ToolUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ToolUpdateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToolUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
