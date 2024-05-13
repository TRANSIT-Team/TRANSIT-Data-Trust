import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutsourceCompaniesComponent } from './outsource-companies.component';

describe('OutsourceCompaniesComponent', () => {
  let component: OutsourceCompaniesComponent;
  let fixture: ComponentFixture<OutsourceCompaniesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutsourceCompaniesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OutsourceCompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
