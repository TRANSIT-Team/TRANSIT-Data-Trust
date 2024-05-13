import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutsourceRightsComponent } from './outsource-rights.component';

describe('OutsourceRightsComponent', () => {
  let component: OutsourceRightsComponent;
  let fixture: ComponentFixture<OutsourceRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutsourceRightsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OutsourceRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
