import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderParentorderInfoComponent } from './order-parentorder-info.component';

describe('OrderParentorderInfoComponent', () => {
  let component: OrderParentorderInfoComponent;
  let fixture: ComponentFixture<OrderParentorderInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderParentorderInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderParentorderInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
