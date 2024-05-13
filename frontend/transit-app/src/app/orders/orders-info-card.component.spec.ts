import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdersInfoCardComponent } from './orders-info-card.component';

describe('OrderInfoCardComponent', () => {
  let component: OrdersInfoCardComponent;
  let fixture: ComponentFixture<OrdersInfoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrdersInfoCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrdersInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
