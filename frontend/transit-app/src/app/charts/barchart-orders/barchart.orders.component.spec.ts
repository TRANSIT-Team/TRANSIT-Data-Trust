import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarchartOrdersComponent } from './barchart.orders.component';

describe('BarchartComponent', () => {
  let component: BarchartOrdersComponent;
  let fixture: ComponentFixture<BarchartOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BarchartOrdersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BarchartOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
