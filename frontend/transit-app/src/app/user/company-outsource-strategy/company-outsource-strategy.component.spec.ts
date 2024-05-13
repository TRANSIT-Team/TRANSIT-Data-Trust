import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyOutsourceStrategyComponent } from './company-outsource-strategy.component';

describe('CompanyOutsourceStrategyComponent', () => {
  let component: CompanyOutsourceStrategyComponent;
  let fixture: ComponentFixture<CompanyOutsourceStrategyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompanyOutsourceStrategyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyOutsourceStrategyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
