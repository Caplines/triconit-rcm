import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingClaimsComponent } from './billing-claims.component';

describe('BillingClaimsComponent', () => {
  let component: BillingClaimsComponent;
  let fixture: ComponentFixture<BillingClaimsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BillingClaimsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BillingClaimsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
