import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderDatesInfoComponent } from './order-dates-info.component';

describe('OrderDeliveryInfoComponent', () => {
  let component: OrderDatesInfoComponent;
  let fixture: ComponentFixture<OrderDatesInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderDatesInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderDatesInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
