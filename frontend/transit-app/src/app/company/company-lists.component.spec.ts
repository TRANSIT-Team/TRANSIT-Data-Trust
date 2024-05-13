import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyListsComponent } from './company-lists.component';

describe('CompanyListsComponent', () => {
  let component: CompanyListsComponent;
  let fixture: ComponentFixture<CompanyListsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompanyListsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyListsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
