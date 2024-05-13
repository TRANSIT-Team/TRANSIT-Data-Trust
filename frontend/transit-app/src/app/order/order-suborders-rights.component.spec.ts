import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderSubordersRightsComponent } from './order-suborders-rights.component';

describe('OrderSubordersRightsComponent', () => {
  let component: OrderSubordersRightsComponent;
  let fixture: ComponentFixture<OrderSubordersRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderSubordersRightsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderSubordersRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
