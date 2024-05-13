import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderDatesComponent } from './order-dates.component';

describe('OrderDatesComponent', () => {
  let component: OrderDatesComponent;
  let fixture: ComponentFixture<OrderDatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderDatesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderDatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
