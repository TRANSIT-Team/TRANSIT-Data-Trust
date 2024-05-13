import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderStatusChipComponent } from './order-status-chip.component';

describe('OrderStatusChipComponent', () => {
  let component: OrderStatusChipComponent;
  let fixture: ComponentFixture<OrderStatusChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderStatusChipComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderStatusChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
